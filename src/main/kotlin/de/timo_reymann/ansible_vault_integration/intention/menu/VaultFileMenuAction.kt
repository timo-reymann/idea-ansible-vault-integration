package de.timo_reymann.ansible_vault_integration.intention.menu

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.progress.ProgressManager
import com.intellij.psi.PsiBinaryFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import de.timo_reymann.ansible_vault_integration.config.AnsibleConfigurationService
import de.timo_reymann.ansible_vault_integration.config.VaultIdentity
import de.timo_reymann.ansible_vault_integration.execution.AnsibleVaultTask
import de.timo_reymann.ansible_vault_integration.intention.AnsibleVaultIdentityPopup
import de.timo_reymann.ansible_vault_integration.runnable.DecryptFileAnsibleVaultRunnable
import de.timo_reymann.ansible_vault_integration.runnable.EncryptFileAnsibleVaultRunnable

open class VaultFileMenuAction : AnAction() {
    private val progressManager: ProgressManager = ProgressManager.getInstance()

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val psiManager = PsiManager.getInstance(project)
        val vaultIdentities = AnsibleConfigurationService.getInstance(project).getAggregatedConfig().vaultIdentities

        e.getData(LangDataKeys.VIRTUAL_FILE_ARRAY)?.forEach {
            val psiFile = psiManager.findFile(it) ?: return@forEach

            if (psiFile is PsiBinaryFile) {
                Notifications.Bus.notify(Notification(
                    VaultFileMenuAction::class.java.canonicalName,
                    NOTIFICATION_ERROR_TITLE,
                    NOTIFICATION_BINARIES_NOT_SUPPORTED,
                    NotificationType.ERROR
                ))

                return@forEach
            }

            if (psiFile.text.isNullOrEmpty()) {
                return@forEach
            }

            processFile(vaultIdentities, psiFile)
        }
    }

    private fun processFile(vaultIdentities: List<VaultIdentity>?, psiFile: PsiFile) {
        val fileIsEncrypted = psiFile.text.startsWith("\$ANSIBLE_VAULT")
        val progressTitle = "${if (fileIsEncrypted) "Decrypting" else "Encrypting"} file(s) with ansible-vault"

        if (fileIsEncrypted) {
            progressManager.run(AnsibleVaultTask(
                psiFile.project,
                progressTitle,
                DecryptFileAnsibleVaultRunnable(psiFile)
            ))
        } else {
            if (!vaultIdentities.isNullOrEmpty()) {
                AnsibleVaultIdentityPopup(vaultIdentities) {
                    progressManager.run(AnsibleVaultTask(
                        psiFile.project,
                        progressTitle,
                        EncryptFileAnsibleVaultRunnable(psiFile, it, false)
                    ))
                }.showCentered()
            } else {
                progressManager.run(AnsibleVaultTask(
                    psiFile.project,
                    progressTitle,
                    EncryptFileAnsibleVaultRunnable(psiFile, null, false)
                ))
            }
        }
    }

    companion object {
        const val NOTIFICATION_ERROR_TITLE = "Error"
        const val NOTIFICATION_BINARIES_NOT_SUPPORTED = "Vaulting binary files not supported yet."
    }
}


