package de.timo_reymann.ansible_vault_integration.runnable

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import de.timo_reymann.ansible_vault_integration.config.VaultIdentity
import de.timo_reymann.ansible_vault_integration.execution.action.AnsibleVaultEncryptAction

class EncryptFileAnsibleVaultRunnable(
    private val project: Project,
    private val containingFile: PsiFile,
    private val content: String,
    private val vaultIdentity: VaultIdentity?,
    private val addPrefix: Boolean = true
) : AnsibleVaultRunnable {
    @Throws(Exception::class)
    override fun run() {
        val encrypted = AnsibleVaultEncryptAction(project, containingFile, content, vaultIdentity, addPrefix)
            .execute()
        WriteCommandAction.runWriteCommandAction(project) {
            FileDocumentManager.getInstance()
                .getDocument(containingFile.virtualFile)?.setText(encrypted)
        }
    }

    override val successMessage: String
        get() = "File encrypted"
}
