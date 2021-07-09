package de.timo_reymann.ansible_vault_integration.action.runnable

import com.intellij.ide.ClipboardSynchronizer
import com.intellij.ide.CopyPasteManagerEx
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.util.ui.TextTransferable
import de.timo_reymann.ansible_vault_integration.action.execution.action.AnsibleVaultDecryptAction

class DecryptFileAnsibleVaultRunnable(
    private val project: Project,
    private val containingFile: PsiFile,
    private val raw: String
) : AnsibleVaultRunnable {
    @Throws(Exception::class)
    override fun run() {
        val decrypted = AnsibleVaultDecryptAction(project, containingFile, raw)
            .execute()
        WriteCommandAction.runWriteCommandAction(project) {
            FileDocumentManager.getInstance()
                .getDocument(containingFile.virtualFile)?.setText(decrypted)
        }
    }

    override val successMessage: String
        get() = "File is decrypted"
}
