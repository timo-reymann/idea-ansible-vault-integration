package de.timo_reymann.ansible_vault_integration.action.vaultaction

import de.timo_reymann.ansible_vault_integration.action.execution.AnsibleVaultWrapperCallFailedException
import de.timo_reymann.ansible_vault_integration.action.util.AnsibleVaultedStringUtil
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

open class AnsibleVaultEncryptAction(
    project: Project,
    contextFile: PsiFile,
    override val stdin: String
) : AnsibleVaultAction(project, contextFile) {
    override val actionName: String
        get() = "encrypt"

    override val parameters: List<String>
        get() = listOf("--output=-")

    @Throws(AnsibleVaultWrapperCallFailedException::class)
    override fun execute(): String = AnsibleVaultedStringUtil.addPrefix(super.execute())
}
