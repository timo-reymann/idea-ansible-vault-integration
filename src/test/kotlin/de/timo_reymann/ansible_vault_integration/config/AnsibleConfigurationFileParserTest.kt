package de.timo_reymann.ansible_vault_integration.config

import de.timo_reymann.ansible_vault_integration.action.config.AnsibleConfigurationFileParser
import de.timo_reymann.ansible_vault_integration.action.config.VaultIdentity
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File
import java.nio.file.Files
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AnsibleConfigurationFileParserTest {
    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private fun getFile(name: String): File = File(javaClass.getResource("/${name}").file)

    @Test
    fun `Verify empty file does not crash the parser`() {
        AnsibleConfigurationFileParser(getFile("ansible.cfg"))
    }

    @Test
    fun `Verify stock config file does not crash the parser`() {
        AnsibleConfigurationFileParser(getFile("ansible-stock.cfg"))
    }

    @Test
    fun `Validate checksum calculation works`() {
        assertEquals(
            "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
            AnsibleConfigurationFileParser(getFile("ansible.cfg")).calculateChecksum()
        )
        assertEquals(
            "843071daf968ddc5452bc54780a651a527792271be400d1e3a31e33dc11f1bdd",
            AnsibleConfigurationFileParser(getFile("ansible-stock.cfg")).calculateChecksum()
        )
    }

    @Test
    fun `Validate changes on disk are detected`() {
        val tempFile = Files.createTempFile("", "")
        Files.write(
            tempFile.toAbsolutePath(),
            listOf("[defaults]")
        )

        val parser = AnsibleConfigurationFileParser(tempFile.toFile())
        val config = parser.parse()

        assertFalse("No change should be stated when there is none") { parser.hasChangedOnDisk(config) }

        Files.write(
            tempFile.toAbsolutePath(),
            listOf(
                "[defaults]",
                "vault_identity_list = dev@~/.foo.bar"
            )
        )
        assertTrue("Configuration change should be detected after actual change") { parser.hasChangedOnDisk(config) }
    }

    @Test
    fun `Verify vault identity list parsing doesnt crash on empty list`() {
        val parser = AnsibleConfigurationFileParser(getFile("ansible.cfg"))
        parser.parseVaultIdentityList()
    }

    @Test
    fun `Verify vault identities are getting parsed correctly`() {
        val parser = AnsibleConfigurationFileParser(getFile("ansible-vault-identities.cfg"))
        assertEquals(listOf(
            VaultIdentity("my_first_vault","~/ansible/passwords/my_first_vault"),
                    VaultIdentity("my_second_vault","~/ansible/passwords/my_second_vault")
        ), parser.parseVaultIdentityList())
    }
}
