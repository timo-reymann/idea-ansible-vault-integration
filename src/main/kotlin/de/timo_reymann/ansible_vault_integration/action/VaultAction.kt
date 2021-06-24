package de.timo_reymann.ansible_vault_integration.action

import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.codeInsight.intention.IntentionAction
import org.jetbrains.annotations.Nls
import com.intellij.psi.PsiElement
import org.jetbrains.yaml.YAMLLanguage
import org.jetbrains.yaml.YAMLTokenTypes
import kotlin.Throws
import com.intellij.util.IncorrectOperationException
import de.timo_reymann.ansible_vault_integration.action.execution.AnsibleVaultTask
import de.timo_reymann.ansible_vault_integration.action.runnable.EncryptAnsibleVaultRunnable
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project

/**
 * Vault Action to provide unvault for yaml files
 */
class VaultAction : PsiElementBaseIntentionAction(), IntentionAction {
    override fun getText(): @Nls(capitalization = Nls.Capitalization.Sentence) String = "Vault ansible secret"

    override fun getFamilyName(): @Nls(capitalization = Nls.Capitalization.Sentence) String = text

    override fun isAvailable(project: Project, editor: Editor, element: PsiElement): Boolean {
        // Only valid in yaml files
        if (element.language != YAMLLanguage.INSTANCE) {
            return false
        }

        // Allow any kind of text in single or dobule quotes or without quotes
        val elementType = element.node.elementType
        return elementType == YAMLTokenTypes.TEXT
                || elementType == YAMLTokenTypes.SCALAR_STRING
                || elementType == YAMLTokenTypes.SCALAR_DSTRING
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
        val task = AnsibleVaultTask(
            project,
            "Encrypt Secret",
            EncryptAnsibleVaultRunnable(project, containingFile, content, element)
        )

        ProgressManager.getInstance()
            .run(task)
    }

    override fun startInWriteAction(): Boolean = false
}
