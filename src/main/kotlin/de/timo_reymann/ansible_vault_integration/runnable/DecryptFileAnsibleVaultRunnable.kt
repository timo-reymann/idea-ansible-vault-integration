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
            AnsibleVaultedStringUtil.addPrefix(containingFile.text)
        ).execute()

        WriteCommandAction.runWriteCommandAction(containingFile.project) {
            FileDocumentManager.getInstance()
                .getDocument(containingFile.virtualFile)?.setText(decrypted)
        }
    }

    override val successMessage: String
        get() = "File is decrypted"
}
