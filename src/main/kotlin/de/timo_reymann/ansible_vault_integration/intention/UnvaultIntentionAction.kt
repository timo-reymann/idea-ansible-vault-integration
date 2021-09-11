package de.timo_reymann.ansible_vault_integration.intention

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.util.IncorrectOperationException
import de.timo_reymann.ansible_vault_integration.runnable.DecryptStringAnsibleVaultRunnable
import de.timo_reymann.ansible_vault_integration.util.AnsibleVaultedStringUtil
import org.jetbrains.yaml.YAMLLanguage
import org.jetbrains.yaml.YAMLTokenTypes

/**
 * Unvault Action to provide unvault for yaml files
 */
class UnvaultIntentionAction : BaseIntentionAction("Unvault ansible secret") {
    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        if (!super.isAvailable(project, editor, element)) {
            return false
        }
        return when {
            element.language != YAMLLanguage.INSTANCE -> false
            else -> AnsibleVaultedStringUtil.isVaultedString(extractText(element))
        }
    }

    /***
     * Extract text from [PsiElement]
     *
     * @param element Element to extract from
     * @return Content or null, if it contains no content
     */
    private fun extractText(element: PsiElement): String? {
        val elementType = element.node.elementType

        // Is inside vaulted string
        if (YAMLTokenTypes.TAG == elementType || YAMLTokenTypes.SCALAR_LIST == elementType || YAMLTokenTypes.SCALAR_EOL == elementType) {
            return element.parent.node.text
        }

        // is in tag for vault string potentially
        return when {
            YAMLTokenTypes.SCALAR_KEY == elementType && element.nextSibling != null && element.nextSibling.nextSibling != null -> {
                // <tag>:<space><text>
                element.nextSibling.nextSibling.nextSibling.node.text
            }
            else -> {
                // <tag>:<space><text>
                null
            }
        }
    }

    @Throws(IncorrectOperationException::class)
    override fun invoke(project: Project, editor: Editor, element: PsiElement) {
        val raw = extractText(element) ?: return
        val containingFile = element.containingFile

        runTask(project, DecryptStringAnsibleVaultRunnable(project, containingFile, raw))
    }
}
