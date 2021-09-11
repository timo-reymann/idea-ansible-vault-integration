package de.timo_reymann.ansible_vault_integration.config

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
        AnsibleConfigurationFileParser(getFile("ansible.cfg"), 0)
    }

    @Test
    fun `Verify stock config file does not crash the parser`() {
        AnsibleConfigurationFileParser(getFile("ansible-stock.cfg"), 0)
    }

    @Test
    fun `Validate checksum calculation works`() {
        assertEquals(
            "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
            AnsibleConfigurationFileParser(getFile("ansible.cfg"), 0).calculateChecksum()
        )
        assertEquals(
            "843071daf968ddc5452bc54780a651a527792271be400d1e3a31e33dc11f1bdd",
            AnsibleConfigurationFileParser(getFile("ansible-stock.cfg"), 0).calculateChecksum()
        )
    }

    @Test
    fun `Validate changes on disk are detected`() {
        val tempFile = Files.createTempFile("", "")
        Files.write(
            tempFile.toAbsolutePath(),
            listOf("[defaults]")
        )

        val parser = AnsibleConfigurationFileParser(tempFile.toFile(), 0)
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
        val parser = AnsibleConfigurationFileParser(getFile("ansible.cfg"), 0)
        parser.parseVaultIdentityList()
    }

    @Test
    fun `Verify vault identities are getting parsed correctly`() {
        val parser = AnsibleConfigurationFileParser(getFile("ansible-vault-identities.cfg"), 0)
        assertEquals(
            listOf(
                VaultIdentity("my_first_vault", "~/ansible/passwords/my_first_vault"),
                VaultIdentity("my_second_vault", "~/ansible/passwords/my_second_vault")
            ), parser.parseVaultIdentityList()
        )
    }

    @Test
    fun `Verify merge works with one empty`() {
        val merged = mergeConfigs(
            listOf(
                AnsibleConfigurationFile(listOf(VaultIdentity("test", "test")), "123", 1),
                AnsibleConfigurationFile(null, "123", 0)
            )
        )
        assertEquals(AnsibleConfigurationFile(listOf(VaultIdentity("test", "test")),null,null), merged)
    }

    @Test
    fun `Verify merge works with priority`() {
        val merged = mergeConfigs(
            listOf(
                AnsibleConfigurationFile(listOf(VaultIdentity("test", "test")), "123", 1),
                AnsibleConfigurationFile(listOf(VaultIdentity("test0", "test0")), "123", 0)
            )
        )
        assertEquals(AnsibleConfigurationFile(listOf(VaultIdentity("test", "test")),null,null), merged)
    }
}
