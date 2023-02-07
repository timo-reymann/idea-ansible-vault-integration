package de.timo_reymann.ansible_vault_integration.runnable.file

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiFile
import de.timo_reymann.ansible_vault_integration.config.VaultIdentity
import de.timo_reymann.ansible_vault_integration.execution.action.AnsibleVaultEncryptAction
import de.timo_reymann.ansible_vault_integration.runnable.AnsibleVaultRunnable
import de.timo_reymann.ansible_vault_integration.runnable.VaultRunnableMode
import de.timo_reymann.ansible_vault_integration.runnable.VaultRunnableType

class EncryptFileAnsibleVaultRunnable(
    private val containingFile: PsiFile,
    private val vaultIdentity: VaultIdentity?,
    private val addPrefix: Boolean = true
) : AnsibleVaultRunnable {
    @Throws(Exception::class)
    override fun run() {
        val encrypted = AnsibleVaultEncryptAction(
            containingFile.project,
            containingFile,
            containingFile.virtualFile.contentsToByteArray(),
            vaultIdentity,
            addPrefix
        ).execute()

        WriteCommandAction.runWriteCommandAction(containingFile.project) {
            containingFile.virtualFile.setBinaryContent(encrypted.toByteArray())
        }
    }

    override val fileName: String
        get() = containingFile.name
    override val type: VaultRunnableType
        get() = VaultRunnableType.ENCRYPT
    override val mode: VaultRunnableMode
        get() = VaultRunnableMode.FILE
}
