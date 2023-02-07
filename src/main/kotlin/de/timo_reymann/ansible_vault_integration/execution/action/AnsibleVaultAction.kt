package de.timo_reymann.ansible_vault_integration.execution.action

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.process.ProcessAdapter
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.wsl.WslPath
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.util.io.FileUtil
import com.intellij.psi.PsiFile
import com.intellij.util.containers.stream
import de.timo_reymann.ansible_vault_integration.bundle.AnsibleVaultIntegrationBundle
import de.timo_reymann.ansible_vault_integration.commandline.AnsibleCommandLineTransformer
import de.timo_reymann.ansible_vault_integration.commandline.AnsibleVaultCommandLineBuilder
import de.timo_reymann.ansible_vault_integration.commandline.NoOpAnsibleCommandLineTransformer
import de.timo_reymann.ansible_vault_integration.commandline.WslAnsibleCommandLineTransformer
import de.timo_reymann.ansible_vault_integration.execution.AnsibleVaultWrapperCallFailedException
import de.timo_reymann.ansible_vault_integration.settings.AnsibleVaultSettings
import java.io.File
import java.io.IOException
import java.nio.file.Path
import java.util.concurrent.atomic.AtomicLong
import java.util.stream.Collectors

abstract class AnsibleVaultAction(protected val project: Project, protected val contextFile: PsiFile) {

    protected abstract val actionName: String
    protected abstract val stdin: ByteArray
    protected abstract val parameters: List<String>

    @Throws(IOException::class)
    protected fun createTempFile(input: ByteArray): File {
        val tempFile = FileUtil.createTempFile("vault", "tmp")
        FileUtil.writeToFile(tempFile, input!!)
        return tempFile
    }

    @Throws(AnsibleVaultWrapperCallFailedException::class)
    open fun execute(): String = executeCommand()

    @Throws(AnsibleVaultWrapperCallFailedException::class)
    private fun executeCommand(): String {
        val processHandler: ProcessHandler
        val stdout: StringBuffer = StringBuffer()
        val line = AtomicLong(0)
        try {
            val contextPath = contextFile.virtualFile.toNioPath()
            processHandler = OSProcessHandler(getVaultCommandLine(project, contextPath, actionName, parameters, stdin))

            // output
            processHandler.addProcessListener(object : ProcessAdapter() {
                @Synchronized
                override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
                    val stdoutLine = event.text.trim { it <= ' ' }

                    // Omit warnings
                    if (stdoutLine.startsWith("WARNING") || stdoutLine.startsWith("DEPRECATION")) {
                        return
                    }

                    // First line is command output, remove it from stdout
                    if (line.incrementAndGet() == 1L) {
                        return
                    }
                    // Remove trailing line breaks
                    stdout.append(event.text)
                }
            })
            processHandler.startNotify()

            // Error waiting for graceful exit
            if (!processHandler.waitFor(AnsibleVaultSettings.getInstance(project).state.timeout * 1000L)) {
                processHandler.destroyProcess()
                throw AnsibleVaultWrapperCallFailedException(
                    AnsibleVaultIntegrationBundle.message(
                        "exception.AnsibleVaultWrapperCallFailedException.time_out",
                        stdout
                    )
                )
            }

            if (processHandler.getExitCode() != null && processHandler.getExitCode() != 0) {
                throw AnsibleVaultWrapperCallFailedException(
                    AnsibleVaultIntegrationBundle.message(
                        "exception.AnsibleVaultWrapperCallFailedException.exit_code",
                        processHandler.getExitCode()!!,
                        stdout
                    )
                )
            }
        } catch (e: ExecutionException) {
            throw AnsibleVaultWrapperCallFailedException(
                AnsibleVaultIntegrationBundle.message(
                    "exception.AnsibleVaultWrapperCallFailedException.internal_error",
                    e.message ?: "No message available"
                )
            )
        } catch (e: NullPointerException) {
            throw AnsibleVaultWrapperCallFailedException(
                AnsibleVaultIntegrationBundle.message(
                    "exception.AnsibleVaultWrapperCallFailedException.internal_error",
                    e.message ?: "No message available"
                )
            )
        } catch (e: IOException) {
            throw AnsibleVaultWrapperCallFailedException(
                AnsibleVaultIntegrationBundle.message(
                    "exception.AnsibleVaultWrapperCallFailedException.internal_error",
                    e.message ?: "No message available"
                )
            )
        }
        return stdout.toString()
    }

    private fun runsInWsl(vaultExecutable: String): Boolean =
        SystemInfo.isWin10OrNewer && WslPath.parseWindowsUncPath(vaultExecutable) != null

    @Throws(AnsibleVaultWrapperCallFailedException::class, IOException::class, ExecutionException::class)
    private fun getVaultCommandLine(
        project: Project,
        contextPath: Path,
        action: String,
        parameters: List<String>,
        stdin: ByteArray
    ): GeneralCommandLine {
        val state = AnsibleVaultSettings.getInstance(project).state
        val vaultExecutable = state.vaultExecutable
        val vaultArguments = getVaultArguments(state)
        val stdinFile = createTempFile(stdin)
        val ansibleCommandLineTransformer: AnsibleCommandLineTransformer = getCommandLineTransformer(vaultExecutable)

        if (!File(vaultExecutable).exists()) {
            throw AnsibleVaultWrapperCallFailedException(
                AnsibleVaultIntegrationBundle.getMessage("exception.AnsibleVaultWrapperCallFailedException.executable_not_found")
            )
        }

        return AnsibleVaultCommandLineBuilder(vaultExecutable, ansibleCommandLineTransformer)
            .withParameter(action)
            .withFilePathParameter(stdinFile.absolutePath)
            .withEnv(ENVIRONMENT_CONTEXT_DIRECTORY, contextPath.toFile().parentFile.name)
            .withFilePathEnv(ENVIRONMENT_CONTEXT_FILE, contextPath.toString())
            .withEnv(ENVIRONMENT_CONTEXT_PROJECT_BASE_PATH, project.basePath)
            .withEnv(ENVIRONMENT_CONTEXT_PROJECT_NAME, project.name)
            .getCommandLine(project)!!
            .withWorkDirectory(project.basePath)
            .withParameters(parameters)
            .withParameters(vaultArguments)
    }

    private fun getCommandLineTransformer(vaultExecutable: String) = when {
        runsInWsl(vaultExecutable) -> WslAnsibleCommandLineTransformer(
            WslPath.parseWindowsUncPath(vaultExecutable)!!.distribution
        )

        else -> NoOpAnsibleCommandLineTransformer()
    }

    private fun getVaultArguments(state: AnsibleVaultSettings) =
        state.vaultArguments
            .split(" ")
            .toTypedArray()
            .stream()
            .filter { `val`: String -> `val`.trim { it <= ' ' } != "" }
            .collect(Collectors.toList())

    companion object {
        private const val ENVIRONMENT_PREFIX = "IDEA_ANSIBLE_VAULT_"

        private const val ENVIRONMENT_CONTEXT_FILE = ENVIRONMENT_PREFIX + "CONTEXT_FILE"
        private const val ENVIRONMENT_CONTEXT_DIRECTORY = ENVIRONMENT_PREFIX + "CONTEXT_DIRECTORY"

        private const val ENVIRONMENT_CONTEXT_PROJECT_PREFIX = ENVIRONMENT_PREFIX + "CONTEXT_PROJECT_"
        private const val ENVIRONMENT_CONTEXT_PROJECT_BASE_PATH = ENVIRONMENT_CONTEXT_PROJECT_PREFIX + "BASE_PATH"
        private const val ENVIRONMENT_CONTEXT_PROJECT_NAME = ENVIRONMENT_CONTEXT_PROJECT_PREFIX + "NAME"
    }
}
