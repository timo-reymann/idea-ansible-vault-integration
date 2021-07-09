package de.timo_reymann.ansible_vault_integration.action.action.menu

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import de.timo_reymann.ansible_vault_integration.action.runnable.EncryptFileAnsibleVaultRunnable

class VaultFileMenuAction : BaseMenuAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val psiFile = e.getData(LangDataKeys.PSI_FILE) ?: return
        val plainYaml = psiFile.text ?: return
        runTask(
            psiFile,
            "Encrypt file with ansible-vault",
            EncryptFileAnsibleVaultRunnable(psiFile.project, psiFile, plainYaml, false)
        )
    }
}


