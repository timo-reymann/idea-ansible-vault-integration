package de.timo_reymann.ansible_vault_integration.intention.menu

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.progress.ProgressManager
import com.intellij.psi.PsiFile
import de.timo_reymann.ansible_vault_integration.config.AnsibleConfigurationService
import de.timo_reymann.ansible_vault_integration.execution.AnsibleVaultTask
import de.timo_reymann.ansible_vault_integration.intention.AnsibleVaultIdentityPopup
import de.timo_reymann.ansible_vault_integration.runnable.AnsibleVaultRunnable
import de.timo_reymann.ansible_vault_integration.runnable.DecryptFileAnsibleVaultRunnable
import de.timo_reymann.ansible_vault_integration.runnable.EncryptFileAnsibleVaultRunnable
import org.jetbrains.yaml.psi.YAMLFile
import setVisible

open class VaultFileMenuAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val psiFile = e.getData(LangDataKeys.PSI_FILE) ?: return
        val content = psiFile.text ?: return
        val isVaulted = content.startsWith("\$ANSIBLE_VAULT")
        val title = "${if (isVaulted) "Decrypt" else "Encrypt"} file with ansible-vault"

        if (isVaulted) {
            runTask(psiFile, title, DecryptFileAnsibleVaultRunnable(psiFile.project, psiFile, content))
        } else {
            val vaultIdentities = AnsibleConfigurationService.getInstance(psiFile.project)
                .getAggregatedConfig()
                .vaultIdentities

            if (vaultIdentities != null && vaultIdentities.isNotEmpty()) {
                AnsibleVaultIdentityPopup(vaultIdentities) {
                    runTask(
                        psiFile,
                        title,
                        EncryptFileAnsibleVaultRunnable(psiFile.project, psiFile, content, it, false)
                    )
                }.showCentered()
            } else {
                runTask(
                    psiFile,
                    title,
                    EncryptFileAnsibleVaultRunnable(psiFile.project, psiFile, content, null, false)
                )
            }
        }
    }

    override fun update(e: AnActionEvent) {
        val file = e.getData(LangDataKeys.PSI_FILE) ?: return
        if (file !is YAMLFile) {
            e.setVisible(false)
            return
        }

        e.setVisible(true)
    }

    private fun runTask(psiFile: PsiFile, title: String, runnable: AnsibleVaultRunnable) {
        val task = AnsibleVaultTask(
            psiFile.project,
            title,
            runnable
        )

        ProgressManager.getInstance()
            .run(task)
    }
}


