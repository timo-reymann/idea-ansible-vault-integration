package de.timo_reymann.ansible_vault_integration.runnable

enum class VaultRunnableType {
    ENCRYPT,
    DECRYPT;

    override fun toString(): String {
        return when (this) {
            ENCRYPT -> "Vault"
            DECRYPT -> "Unvault"
        }
    }

    fun stringify(fileName: String): String {
        return "$this $fileName"
    }
}

enum class VaultRunnableMode {
    INLINE,
    FILE
}

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
     * File name to display
     */
    val fileName: String

    /**
     * Type of the runnable
     */
    val type: VaultRunnableType

    /**
     * Specifies if the action was run for entire file(s) or a single value
     */
    val mode : VaultRunnableMode
}
