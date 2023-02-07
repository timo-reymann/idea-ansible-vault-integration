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
import de.timo_reymann.ansible_vault_integration.execution.AnsibleVaultTaskRunner
import de.timo_reymann.ansible_vault_integration.intention.AnsibleVaultIdentityPopup
import de.timo_reymann.ansible_vault_integration.runnable.AnsibleVaultRunnable
import de.timo_reymann.ansible_vault_integration.runnable.DecryptFileAnsibleVaultRunnable
import de.timo_reymann.ansible_vault_integration.runnable.EncryptFileAnsibleVaultRunnable
import kotlinx.coroutines.runBlocking
import org.apache.commons.io.FileUtils
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

open class VaultFileMenuAction : AnAction() {
    private val progressManager: ProgressManager = ProgressManager.getInstance()

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val psiManager = PsiManager.getInstance(project)
        val vaultIdentities = AnsibleConfigurationService.getInstance(project).getAggregatedConfig().vaultIdentities
        runBlocking {
            val runnables = mutableListOf<AnsibleVaultRunnable>()
            e.getData(LangDataKeys.VIRTUAL_FILE_ARRAY)?.forEach {
                val psiFile = psiManager.findFile(it) ?: return@forEach

                val runnable = createFileRunnable(vaultIdentities, psiFile)
                runnables += runnable
            }

            progressManager.run(
                AnsibleVaultTaskRunner(
                    project,
                    "Processing files with ansible-vault ...",
                    runnables
                )
            )
        }
    }

    private suspend fun createFileRunnable(
        vaultIdentities: List<VaultIdentity>?,
        psiFile: PsiFile
    ): AnsibleVaultRunnable {
        val buffer = ByteArray(14)
        psiFile.virtualFile.inputStream.use {
            it.read(buffer)
        }
        val fileIsEncrypted = String(buffer) == "\$ANSIBLE_VAULT"

        return if (fileIsEncrypted) {
            DecryptFileAnsibleVaultRunnable(psiFile)
        } else {
            if (!vaultIdentities.isNullOrEmpty()) {
                suspendCoroutine { continuation ->
                    AnsibleVaultIdentityPopup(vaultIdentities) {
                        continuation.resume(EncryptFileAnsibleVaultRunnable(psiFile, it, false))
                    }.showCentered()
                }
            } else {
                EncryptFileAnsibleVaultRunnable(psiFile, null, false)
            }
        }
    }
}


