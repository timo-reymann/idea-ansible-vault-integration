package de.timo_reymann.ansible_vault_integration.action.action.menu

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.progress.ProgressManager
import com.intellij.psi.PsiFile
import de.timo_reymann.ansible_vault_integration.action.execution.AnsibleVaultTask
import de.timo_reymann.ansible_vault_integration.action.runnable.AnsibleVaultRunnable
import de.timo_reymann.ansible_vault_integration.action.runnable.DecryptFileAnsibleVaultRunnable
import de.timo_reymann.ansible_vault_integration.action.runnable.EncryptFileAnsibleVaultRunnable
import org.jetbrains.yaml.psi.YAMLFile
import setVisible

open class VaultFileMenuAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val psiFile = e.getData(LangDataKeys.PSI_FILE) ?: return
        val content = psiFile.text ?: return
        val isVaulted = content.startsWith("\$ANSIBLE_VAULT")
        val task: AnsibleVaultRunnable = if (isVaulted) {
            DecryptFileAnsibleVaultRunnable(psiFile.project, psiFile, content)
        } else {
            EncryptFileAnsibleVaultRunnable(psiFile.project, psiFile, content, false)
        }

        runTask(
            psiFile,
            "${if (isVaulted) "Decrypt" else "Encrypt"} file with ansible-vault",
            task
        )
    }

    override fun update(e: AnActionEvent) {
        val file = e.getData(LangDataKeys.PSI_FILE) ?: return
        if (file !is YAMLFile) {
            e.setVisible(false)
            return
        }

        e.setVisible(true)
    }

    private fun runTask(psiFile: PsiFile, title : String, runnable: AnsibleVaultRunnable) {
        val task = AnsibleVaultTask(
            psiFile.project,
            title,
            runnable
        )

        ProgressManager.getInstance()
            .run(task)
    }
}


