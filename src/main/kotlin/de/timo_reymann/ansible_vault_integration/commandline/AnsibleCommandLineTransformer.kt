package de.timo_reymann.ansible_vault_integration.commandline

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.project.Project

interface AnsibleCommandLineTransformer {
    fun transformFileName(input: String?): String?
    fun transformCommandLine(project: Project, commandLine: GeneralCommandLine): GeneralCommandLine? = commandLine
}
