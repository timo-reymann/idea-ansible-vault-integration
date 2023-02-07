package de.timo_reymann.ansible_vault_integration.runnable

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.util.io.FileUtil
import com.intellij.psi.PsiFile
import de.timo_reymann.ansible_vault_integration.config.VaultIdentity
import de.timo_reymann.ansible_vault_integration.execution.action.AnsibleVaultEncryptAction

class EncryptFileAnsibleVaultRunnable(
    private val containingFile: PsiFile,
    private val vaultIdentity: VaultIdentity?,
    private val addPrefix: Boolean = true
) : AnsibleVaultRunnable {
    @Throws(Exception::class)
    override fun run() {
        val encrypted = AnsibleVaultEncryptAction(
            containingFile.project,
            containingFile,
            containingFile.virtualFile.contentsToByteArray(),
            vaultIdentity,
            addPrefix
        ).execute()

        WriteCommandAction.runWriteCommandAction(containingFile.project) {
            containingFile.virtualFile.setBinaryContent(encrypted.toByteArray())
        }
    }

    override val successMessage: String
        get() = "${containingFile.name} vaulted successfully"
}
