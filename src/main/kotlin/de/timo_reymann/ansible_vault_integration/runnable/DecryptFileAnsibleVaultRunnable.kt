package de.timo_reymann.ansible_vault_integration.runnable

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.psi.PsiFile
import de.timo_reymann.ansible_vault_integration.execution.action.AnsibleVaultDecryptAction
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

    override val successMessage: String
        get() = "${containingFile.name} unvaulted successfully"
}
