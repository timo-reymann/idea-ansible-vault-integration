package de.timo_reymann.ansible_vault_integration.action.commandline

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.project.Project

class AnsibleVaultCommandLineBuilder(
    executable: String?,
    private val ansibleCommandLineTransformer: AnsibleCommandLineTransformer
) {
    private val generalCommandLine: GeneralCommandLine =
        GeneralCommandLine(ansibleCommandLineTransformer.transformFileName(executable))

    fun withFilePathParameter(parameter: String?): AnsibleVaultCommandLineBuilder {
        withParameter(ansibleCommandLineTransformer.transformFileName(parameter))
        return this
    }

    fun withParameter(parameter: String?): AnsibleVaultCommandLineBuilder {
        generalCommandLine.withParameters(parameter!!)
        return this
    }

    fun withEnv(variable: String?, value: String?): AnsibleVaultCommandLineBuilder {
        generalCommandLine.withEnvironment(variable!!, value!!)
        return this
    }

    fun withFilePathEnv(variable: String?, filepath: String?): AnsibleVaultCommandLineBuilder {
        withEnv(variable, ansibleCommandLineTransformer.transformFileName(filepath))
        return this
    }

    fun getCommandLine(project: Project): GeneralCommandLine? =
        ansibleCommandLineTransformer.transformCommandLine(project, generalCommandLine)
}
