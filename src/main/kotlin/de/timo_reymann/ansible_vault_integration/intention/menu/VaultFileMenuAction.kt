package de.timo_reymann.ansible_vault_integration.intention.menu

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.progress.ProgressManager
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import de.timo_reymann.ansible_vault_integration.config.AnsibleConfigurationService
import de.timo_reymann.ansible_vault_integration.execution.AnsibleVaultTask
import de.timo_reymann.ansible_vault_integration.intention.AnsibleVaultIdentityPopup
import de.timo_reymann.ansible_vault_integration.runnable.AnsibleVaultRunnable
import de.timo_reymann.ansible_vault_integration.runnable.DecryptFileAnsibleVaultRunnable
import de.timo_reymann.ansible_vault_integration.runnable.EncryptFileAnsibleVaultRunnable

open class VaultFileMenuAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        e.getData(LangDataKeys.VIRTUAL_FILE_ARRAY)?.forEach {
            val psiFile = PsiManager.getInstance(project).findFile(it) ?: return@forEach
            val content = psiFile.text ?: return@forEach
            val isVaulted = content.startsWith("\$ANSIBLE_VAULT")
            val title = "${if (isVaulted) "Decrypt" else "Encrypt"} file(s) with ansible-vault"

            if (isVaulted) {
                runTask(psiFile, title, DecryptFileAnsibleVaultRunnable(psiFile.project, psiFile, content))
            } else {
                val vaultIdentities = AnsibleConfigurationService.getInstance(psiFile.project)
                    .getAggregatedConfig()
                    .vaultIdentities

                if (!vaultIdentities.isNullOrEmpty()) {
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


