package de.timo_reymann.ansible_vault_integration.action.config

import org.ini4j.Ini
import java.io.File
import java.io.FileInputStream
import java.security.DigestInputStream
import java.security.MessageDigest

private val DIGEST_SHA256 = MessageDigest.getInstance("SHA-256")

class AnsibleConfigurationFileParser(private val file: File) {
    private val ini: Ini = Ini(file)

    fun calculateChecksum(): String {
        val messageDigest: MessageDigest
        DigestInputStream(FileInputStream(file), DIGEST_SHA256).use {
            @Suppress("ControlFlowWithEmptyBody") // empty loop to read all information
            while (it.read() != -1);

            messageDigest = it.messageDigest
        }

        val result = StringBuilder()
        for (block in messageDigest.digest()) {
            result.append(String.format("%02x", block))
        }
        return result.toString()
    }

    fun hasChangedOnDisk(configuration: AnsibleConfigurationFile): Boolean =
        calculateChecksum() != configuration.checksum

    fun parseVaultIdentityList(): List<VaultIdentity> {
        val identities = ini.get("defaults", "vault_identity_list")

        return identities.split(',')
            .map { it.trim() }
            .map { it.split('@') }
            .filter { it.size == 2 }
            .map { VaultIdentity(it[0], it[1]) }
    }

    fun parse(): AnsibleConfigurationFile {
        return AnsibleConfigurationFile(
            checksum = calculateChecksum(),
            vaultIdentities = parseVaultIdentityList()
        )
    }
}
