package com.github.timo_reymann.ansible_vault_integration.execution.commandline;

import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.project.Project;

public class AnsibleVaultCommandLineBuilder {
    private final GeneralCommandLine generalCommandLine;
    private final AnsibleCommandLineTransformer ansibleCommandLineTransformer;

    public AnsibleVaultCommandLineBuilder(String executable, AnsibleCommandLineTransformer ansibleCommandLineTransformer) {
        this.generalCommandLine = new GeneralCommandLine(ansibleCommandLineTransformer.transformFileName(executable));
        this.ansibleCommandLineTransformer = ansibleCommandLineTransformer;
    }

    public AnsibleVaultCommandLineBuilder withFilePathParameter(String parameter) {
         withParameter(ansibleCommandLineTransformer.transformFileName(parameter));
         return this;
    }

    public AnsibleVaultCommandLineBuilder withParameter(String parameter) {
         generalCommandLine.withParameters(parameter);
         return this;
    }

    public AnsibleVaultCommandLineBuilder withEnv(String variable, String value) {
         generalCommandLine.withEnvironment(variable, value);
         return this;
    }

    public AnsibleVaultCommandLineBuilder withFilePathEnv(String variable, String filepath) {
         withEnv(variable, ansibleCommandLineTransformer.transformFileName(filepath));
        return this;
    }

    public GeneralCommandLine getCommandLine(Project project) {
        return this.ansibleCommandLineTransformer.transformCommandLine(project, this.generalCommandLine);
    }
}
