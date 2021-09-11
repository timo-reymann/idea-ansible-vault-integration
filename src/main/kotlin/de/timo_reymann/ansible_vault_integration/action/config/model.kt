package de.timo_reymann.ansible_vault_integration.action.config

data class VaultIdentity(
     val name: String,
     val vaultFile: String
)

data class AnsibleConfigurationFile(
     val vaultIdentities: List<VaultIdentity>,
     val checksum: String
)
