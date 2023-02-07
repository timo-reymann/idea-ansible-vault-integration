package de.timo_reymann.ansible_vault_integration.runnable.file

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiFile
import de.timo_reymann.ansible_vault_integration.execution.action.AnsibleVaultDecryptAction
import de.timo_reymann.ansible_vault_integration.runnable.AnsibleVaultRunnable
import de.timo_reymann.ansible_vault_integration.runnable.VaultRunnableMode
import de.timo_reymann.ansible_vault_integration.runnable.VaultRunnableType
import de.timo_reymann.ansible_vault_integration.util.AnsibleVaultedStringUtil

class DecryptFileAnsibleVaultRunnable(
    private val containingFile: PsiFile
) : AnsibleVaultRunnable {
    @Throws(Exception::class)
    override fun run() {
        val decrypted = AnsibleVaultDecryptAction(
            containingFile.project,
            containingFile,
            AnsibleVaultedStringUtil.addPrefix(String(containingFile.virtualFile.contentsToByteArray()))
        ).execute()

        WriteCommandAction.runWriteCommandAction(containingFile.project) {
            containingFile.virtualFile.setBinaryContent(decrypted.toByteArray())
        }
    }

    override val fileName: String
        get() = containingFile.name
    override val type: VaultRunnableType
        get() = VaultRunnableType.DECRYPT
    override val mode: VaultRunnableMode
        get() = VaultRunnableMode.FILE
}
