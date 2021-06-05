package com.github.timo_reymann.ansible_vault_integration.execution.commandline;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.wsl.WSLCommandLineOptions;
import com.intellij.execution.wsl.WSLDistribution;
import com.intellij.openapi.project.Project;

public class WslAnsibleCommandLineTransformer implements AnsibleCommandLineTransformer {
    private final WSLDistribution distribution;

    public WslAnsibleCommandLineTransformer(WSLDistribution distribution) {
        this.distribution = distribution;
    }

    @Override
    public String transformFileName(String input) {
        return distribution.getWslPath(input);
    }

    @Override
    public GeneralCommandLine transformCommandLine(Project project, GeneralCommandLine commandLine) {
        WSLCommandLineOptions wslCommandLineOptions = new WSLCommandLineOptions()
                .setExecuteCommandInShell(false)
                .setRemoteWorkingDirectory(transformFileName(project.getBasePath()));

        try {
            return distribution
                    .patchCommandLine(commandLine, project, wslCommandLineOptions);
        } catch (ExecutionException e) {
            return null;
        }
    }
}
