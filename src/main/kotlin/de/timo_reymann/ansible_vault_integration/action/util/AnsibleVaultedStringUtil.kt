package de.timo_reymann.ansible_vault_integration.action.util

import com.intellij.util.containers.stream
import java.util.stream.Collectors

/**
 * Util for ansible vault string manipulation
 */
object AnsibleVaultedStringUtil {
    private const val PREFIX = "!vault |"

    fun isVaultedString(input: String?): Boolean = input != null
            && input.trim { it <= ' ' }.startsWith(PREFIX)

    fun removePrefix(vaultedString: String): String = vaultedString.splitLines()
        .stream()
        .skip(1)
        .map { obj: String -> obj.trim { it <= ' ' } }
        .collect(Collectors.joining("\n"))

    fun addPrefix(vaultedString: String): String {
        val rawLines = vaultedString.splitLines()
        val suffixedLines = arrayOfNulls<String>(rawLines.size + 1)
        suffixedLines[0] = PREFIX
        System.arraycopy(rawLines, 0, suffixedLines, 1, rawLines.size)
        return java.lang.String.join("\n", *suffixedLines)
    }
}

private fun String.splitLines(): Array<String> = this.split('\n')
    .toTypedArray()
