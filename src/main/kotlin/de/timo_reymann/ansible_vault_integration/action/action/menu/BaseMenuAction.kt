package de.timo_reymann.ansible_vault_integration.action.action.menu

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.progress.ProgressManager
import com.intellij.psi.PsiFile
import de.timo_reymann.ansible_vault_integration.action.execution.AnsibleVaultTask
import de.timo_reymann.ansible_vault_integration.action.runnable.AnsibleVaultRunnable
import de.timo_reymann.ansible_vault_integration.action.runnable.EncryptFileAnsibleVaultRunnable
import org.jetbrains.yaml.psi.YAMLFile
import setVisible

abstract class BaseMenuAction : AnAction()  {
    override fun update(e: AnActionEvent) {
        val file = e.getData(LangDataKeys.PSI_FILE) ?: return
        if (file !is YAMLFile) {
            e.setVisible(false)
            return
        }

        e.setVisible(true)
    }

    protected fun runTask(psiFile: PsiFile, title : String, runnable: AnsibleVaultRunnable) {
        val task = AnsibleVaultTask(
            psiFile.project,
            title,
            runnable
        )

        ProgressManager.getInstance()
            .run(task)
    }
}
