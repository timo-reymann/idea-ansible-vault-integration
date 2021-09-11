package de.timo_reymann.ansible_vault_integration.intention

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import de.timo_reymann.ansible_vault_integration.execution.AnsibleVaultTask
import de.timo_reymann.ansible_vault_integration.runnable.AnsibleVaultRunnable
import org.jetbrains.annotations.Nls
import org.jetbrains.yaml.YAMLLanguage

abstract class BaseIntentionAction(private val uiText: String) : PsiElementBaseIntentionAction(), IntentionAction {
    override fun startInWriteAction(): Boolean = false
    override fun getFamilyName(): @Nls(capitalization = Nls.Capitalization.Sentence) String = text
    override fun getText(): @Nls(capitalization = Nls.Capitalization.Sentence) String = uiText

    // Only valid in yaml files
    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean =
        element.language == YAMLLanguage.INSTANCE

    protected fun runTask(project: Project, runnable: AnsibleVaultRunnable) {
            ProgressManager.getInstance()
                .run(AnsibleVaultTask(project, text, runnable))
    }
}
