package de.timo_reymann.ansible_vault_integration.util

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AnsibleVaultedStringUtilTest {

    @Test
    fun `Test vaulted strings are detected correctly`() {
        assertFalse { AnsibleVaultedStringUtil.isVaultedString("notVaulted") }
        assertTrue {
            AnsibleVaultedStringUtil.isVaultedString(
                """
            !vault |
            test
            test
            test
        """.trimIndent()
            )
        }
    }

    @Test
    fun `Test removing prefix`() {
        val removed = AnsibleVaultedStringUtil.removePrefix(
            """
            !vault |
            test
            test
        """.trimIndent()
        )
        assertEquals(
            """
            test
            test
        """.trimIndent(), removed
        )
    }

    @Test
    fun `Test adding prefix`() {
        val prefixed = AnsibleVaultedStringUtil.addPrefix(
            """
            test
        """.trimIndent()
        )
        assertEquals(
            """
            !vault |
            test
        """.trimIndent(), prefixed
        )
    }
}
