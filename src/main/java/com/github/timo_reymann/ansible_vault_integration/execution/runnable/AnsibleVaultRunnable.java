package com.github.timo_reymann.ansible_vault_integration.execution.runnable;

import org.jetbrains.annotations.NotNull;

/**
 * Abstraction for ansible vault task
 */
public interface AnsibleVaultRunnable {
    /***
     * Execute task using ansible vault
     * @throws Exception Exceptions during execution
     */
    void run() throws Exception;

    /**
     * Get message for success
     *
     * @return String
     */
    @NotNull
    String getSuccessMessage();
}
