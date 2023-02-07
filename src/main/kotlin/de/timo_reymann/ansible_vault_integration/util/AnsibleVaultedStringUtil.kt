package de.timo_reymann.ansible_vault_integration.util

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.containers.stream
import java.util.stream.Collectors

/**
 * Util for ansible vault string manipulation
 */
object AnsibleVaultedStringUtil {
    private const val PREFIX = "!vault |"
    const val VAULT_FILE_PREFIX = "\$ANSIBLE_VAULT"

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

    fun isVaultedFile(virtualFile : VirtualFile): Boolean {
        val buffer = ByteArray(14)
        virtualFile.inputStream.use {
            it.read(buffer)
        }
        return String(buffer) == VAULT_FILE_PREFIX
    }
}

private fun String.splitLines(): Array<String> = this.split('\n')
    .toTypedArray()
