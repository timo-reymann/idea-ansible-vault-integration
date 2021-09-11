package de.timo_reymann.ansible_vault_integration.execution.action

import de.timo_reymann.ansible_vault_integration.util.AnsibleVaultedStringUtil
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

open class AnsibleVaultDecryptAction(
    project: Project,
    contextFile: PsiFile,
    private val encrypted: String
) : AnsibleVaultAction(project, contextFile) {
    override val actionName: String
        get() = "decrypt"

    override val stdin: String
        get() = AnsibleVaultedStringUtil.removePrefix(encrypted)

    override val parameters: List<String>
        get() = listOf("--output=-")
}
