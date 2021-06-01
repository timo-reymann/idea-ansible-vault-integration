package com.github.timo_reymann.ansible_vault_integration.execution.commandline;

public class NoOpAnsibleCommandLineTransformer implements AnsibleCommandLineTransformer {
    @Override
    public String transformFileName(String input) {
        return input;
    }
}
