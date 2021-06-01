package com.github.timo_reymann.ansible_vault_integration.execution.commandline;

import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.project.Project;

public interface AnsibleCommandLineTransformer {
    String transformFileName(String input);

    default GeneralCommandLine transformCommandLine(Project project, GeneralCommandLine commandLine) {
        return commandLine;
    }
}
