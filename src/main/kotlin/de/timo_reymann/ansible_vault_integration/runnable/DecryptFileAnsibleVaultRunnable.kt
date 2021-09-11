package de.timo_reymann.ansible_vault_integration.runnable

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import de.timo_reymann.ansible_vault_integration.execution.action.AnsibleVaultDecryptAction
import de.timo_reymann.ansible_vault_integration.util.AnsibleVaultedStringUtil

class DecryptFileAnsibleVaultRunnable(
    private val project: Project,
    private val containingFile: PsiFile,
    private val raw: String
) : AnsibleVaultRunnable {
    @Throws(Exception::class)
    override fun run() {
        val decrypted = AnsibleVaultDecryptAction(project, containingFile, AnsibleVaultedStringUtil.addPrefix(raw))
            .execute()
        WriteCommandAction.runWriteCommandAction(project) {
            FileDocumentManager.getInstance()
                .getDocument(containingFile.virtualFile)?.setText(decrypted)
        }
    }

    override val successMessage: String
        get() = "File is decrypted"
}
