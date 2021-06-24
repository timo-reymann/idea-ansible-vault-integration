package de.timo_reymann.ansible_vault_integration.action

import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.codeInsight.intention.IntentionAction
import org.jetbrains.annotations.Nls
import com.intellij.psi.PsiElement
import org.jetbrains.yaml.YAMLLanguage
import de.timo_reymann.ansible_vault_integration.action.util.AnsibleVaultedStringUtil
import org.jetbrains.yaml.YAMLTokenTypes
import kotlin.Throws
import com.intellij.util.IncorrectOperationException
import de.timo_reymann.ansible_vault_integration.action.execution.AnsibleVaultTask
import de.timo_reymann.ansible_vault_integration.action.runnable.DecryptAnsibleVaultRunnable
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project

/**
 * Unvault Action to provide unvault for yaml files
 */
class UnvaultAction : PsiElementBaseIntentionAction(), IntentionAction {
    override fun getText(): @Nls(capitalization = Nls.Capitalization.Sentence) String = "Unvault ansible secret"

    override fun getFamilyName(): @Nls(capitalization = Nls.Capitalization.Sentence) String = text

    override fun isAvailable(project: Project, editor: Editor, element: PsiElement): Boolean {
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
        val task = AnsibleVaultTask(
            project,
            "Decrypt secret",
            DecryptAnsibleVaultRunnable(project, containingFile, raw)
        )

        ProgressManager.getInstance()
            .run(task)
    }

    override fun startInWriteAction(): Boolean = false
}
