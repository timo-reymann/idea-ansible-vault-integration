package de.timo_reymann.ansible_vault_integration.execution.action

import de.timo_reymann.ansible_vault_integration.execution.AnsibleVaultWrapperCallFailedException
import de.timo_reymann.ansible_vault_integration.util.AnsibleVaultedStringUtil
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import de.timo_reymann.ansible_vault_integration.config.VaultIdentity

open class AnsibleVaultEncryptAction(
    project: Project,
    contextFile: PsiFile,
    override val stdin: ByteArray,
    private val vaultIdentity: VaultIdentity?,
    private val addPrefix: Boolean = true
) : AnsibleVaultAction(project, contextFile) {
    override val actionName: String
        get() = "encrypt"

    override val parameters: List<String>
        get() {
            val args = mutableListOf("--output=-")

            if (vaultIdentity != null) {
                args.add("--encrypt-vault-id")
                args.add(vaultIdentity.name)
            }

            return args
        }

    @Throws(AnsibleVaultWrapperCallFailedException::class)
    override fun execute(): String {
        val result = super.execute()

        return when {
            addPrefix -> AnsibleVaultedStringUtil.addPrefix(result)
            else -> result
        }

    }
}
