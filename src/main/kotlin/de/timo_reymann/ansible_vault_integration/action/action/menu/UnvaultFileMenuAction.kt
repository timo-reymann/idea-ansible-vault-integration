package de.timo_reymann.ansible_vault_integration.action.action.menu

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import de.timo_reymann.ansible_vault_integration.action.runnable.DecryptFileAnsibleVaultRunnable
import de.timo_reymann.ansible_vault_integration.action.runnable.EncryptFileAnsibleVaultRunnable

class UnvaultFileMenuAction : BaseMenuAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val psiFile = e.getData(LangDataKeys.PSI_FILE) ?: return
        val encrypted = psiFile.text ?: return
        runTask(
            psiFile,
            "Decrypt file with ansible-vault",
            DecryptFileAnsibleVaultRunnable(psiFile.project, psiFile, encrypted)
        )
    }
}
