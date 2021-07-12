package de.timo_reymann.ansible_vault_integration.action.execution.action

import de.timo_reymann.ansible_vault_integration.action.execution.AnsibleVaultWrapperCallFailedException
import de.timo_reymann.ansible_vault_integration.action.util.AnsibleVaultedStringUtil
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

open class AnsibleVaultEncryptAction(
    project: Project,
    contextFile: PsiFile,
    override val stdin: String,
    private val addPrefix: Boolean = true
) : AnsibleVaultAction(project, contextFile) {
    override val actionName: String
        get() = "encrypt"

    override val parameters: List<String>
        get() = listOf("--output=-")

    @Throws(AnsibleVaultWrapperCallFailedException::class)
    override fun execute(): String {
        val result = super.execute()

        return when {
            addPrefix -> AnsibleVaultedStringUtil.addPrefix(result)
            else -> result
        }

    }
}
