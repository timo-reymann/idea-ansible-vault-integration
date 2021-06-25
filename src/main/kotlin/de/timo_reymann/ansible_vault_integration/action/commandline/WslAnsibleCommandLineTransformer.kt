package de.timo_reymann.ansible_vault_integration.action.commandline

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.wsl.WSLCommandLineOptions
import com.intellij.execution.wsl.WSLDistribution
import com.intellij.openapi.project.Project

class WslAnsibleCommandLineTransformer(private val distribution: WSLDistribution) : AnsibleCommandLineTransformer {
    override fun transformFileName(input: String?): String? {
        return distribution.getWslPath(input!!)
    }

    override fun transformCommandLine(project: Project, commandLine: GeneralCommandLine): GeneralCommandLine? {
        val wslCommandLineOptions = WSLCommandLineOptions()
            .setExecuteCommandInShell(false)
            .setRemoteWorkingDirectory(transformFileName(project.basePath))
        return try {
            distribution
                .patchCommandLine<GeneralCommandLine>(commandLine, project, wslCommandLineOptions)
        } catch (e: ExecutionException) {
            null
        }
    }
}
