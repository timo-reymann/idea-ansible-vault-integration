package de.timo_reymann.ansible_vault_integration.runnable

/**
 * Abstraction for ansible vault task
 */
interface AnsibleVaultRunnable {
    /***
     * Execute task using ansible vault
     * @throws Exception Exceptions during execution
     */
    @Throws(Exception::class)
    fun run()

    /**
     * Get message for success
     *
     * @return String
     */
    val successMessage: String
}
