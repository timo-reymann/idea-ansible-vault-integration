package de.timo_reymann.ansible_vault_integration.action.action.intention

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.util.IncorrectOperationException
import de.timo_reymann.ansible_vault_integration.action.runnable.EncryptStringAnsibleVaultRunnable
import org.jetbrains.yaml.YAMLTokenTypes
import org.jetbrains.yaml.psi.YAMLKeyValue

/**
 * Vault Action to provide unvault for yaml files
 */
class VaultIntentionAction : BaseIntentionAction("Vault ansible secret") {
    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        if (!super.isAvailable(project, editor, element)) {
            return false
        }

        // Allow any kind of text in single or double quotes or without quotes
        val elementType = element.node.elementType
        return (elementType == YAMLTokenTypes.TEXT
                || elementType == YAMLTokenTypes.SCALAR_STRING
                || elementType == YAMLTokenTypes.SCALAR_DSTRING) &&
                element.parent?.parent is YAMLKeyValue
    }

    private fun extractValue(element: PsiElement): String {
        val elementType = element.node.elementType
        val text = element.node.text

        return when (elementType) {
            YAMLTokenTypes.TEXT -> text // plain text -> no modification required
            else -> text.substring(1, text.length - 1)         // remove quotes
        }
    }

    @Throws(IncorrectOperationException::class)
    override fun invoke(project: Project, editor: Editor, element: PsiElement) {
        val content = extractValue(element)
        val containingFile = element.containingFile
        runTask(project, EncryptStringAnsibleVaultRunnable(project, containingFile, content, element))
    }

}
