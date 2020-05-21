package com.github.timo_reymann.ansible_vault_integration.execution;

public class AnsibleVaultWrapperCallFailedException extends Exception {
    public AnsibleVaultWrapperCallFailedException(String message) {
        super(message);
    }
}
