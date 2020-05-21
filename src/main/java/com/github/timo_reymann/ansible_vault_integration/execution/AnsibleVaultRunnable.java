package com.github.timo_reymann.ansible_vault_integration.execution;

import org.jetbrains.annotations.NotNull;

/**
 * Abstraction for ansible vault task
 *
 */
public interface AnsibleVaultRunnable {
    /***
     * Execute task using ansible vault
     * @throws Exception Exceptions during execution
     */
    public void run() throws Exception;

    /**
     * Get message for success
     *
     * @return String
     */
    @NotNull
    public String getSuccessMessage();
}
