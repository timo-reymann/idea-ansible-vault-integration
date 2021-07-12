package de.timo_reymann.ansible_vault_integration.action.runnable

import com.intellij.ide.ClipboardSynchronizer
import com.intellij.ide.CopyPasteManagerEx
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.util.ui.TextTransferable
import de.timo_reymann.ansible_vault_integration.action.execution.action.AnsibleVaultDecryptAction

class DecryptStringAnsibleVaultRunnable(
    private val project: Project,
    private val containingFile: PsiFile,
    private val raw: String
) : AnsibleVaultRunnable {
    @Throws(Exception::class)
    override fun run() {
        val decrypted = AnsibleVaultDecryptAction(project, containingFile, raw)
            .execute()
        ClipboardSynchronizer.getInstance()
            .setContent(TextTransferable(decrypted, decrypted), CopyPasteManagerEx.getInstanceEx())
    }

    override val successMessage: String
        get() = "Decrypted secret has been copied to your clipboard"
}
