package de.timo_reymann.ansible_vault_integration.action.settings

import com.github.timo_reymann.ansible_vault_integration.settings.AnsibleVaultSettingsForm
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.fileChooser.ex.FileChooserDialogImpl
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import org.apache.http.util.TextUtils
import org.jetbrains.annotations.Nls
import java.awt.event.ActionEvent
import javax.swing.JComponent

/**
 * Configurable for ansible-vault configuration
 */
class AnsibleVaultSettingsConfigurable(private val project: Project) : Configurable {
    private val pluginSettingsForm: AnsibleVaultSettingsForm = AnsibleVaultSettingsForm()
    private val pluginSettings: AnsibleVaultSettings = AnsibleVaultSettings.getInstance(project)
    private var ansibleVaultExecutableChooser: FileChooserDescriptor? = null
    private var selectedVaultExecutable: VirtualFile? = null

    /**
     * Initialize file chooser for ansible-vault executable
     */
    private fun initExecutableChooser() {
        ansibleVaultExecutableChooser = FileChooserDescriptor(true, false, false, false, false, false)
        var ansibleVaultExecutable: String? = ""
        if (pluginSettings.state != null) {
            ansibleVaultExecutable = pluginSettings.state!!.vaultExecutable
        }

        if (!TextUtils.isEmpty(ansibleVaultExecutable)) {
            selectedVaultExecutable = VirtualFileManager.getInstance()
                .findFileByUrl(getFileUrl(ansibleVaultExecutable))
        }

        pluginSettingsForm.vaultExecutableChooser.addActionListener { e: ActionEvent? ->
            val chosenFile = FileChooserDialogImpl(ansibleVaultExecutableChooser!!, project)
                .choose(project, selectedVaultExecutable)
            if (chosenFile.size != 1) {
                return@addActionListener
            }
            val file = chosenFile[0] ?: return@addActionListener
            selectedVaultExecutable = file
            pluginSettingsForm.vaultExecutableField.text = file.canonicalPath
        }
    }

    private fun getFileUrl(path: String?): String = "file://$path"

    override fun getDisplayName(): @Nls String = "Ansible Vault"

    override fun getHelpTopic(): String = "Configure Ansible vault"

    override fun createComponent(): JComponent? = pluginSettingsForm.settingsPanel

    override fun isModified(): Boolean = pluginSettingsForm.settingsState != pluginSettings.state

    override fun apply() {
        pluginSettings.loadState(pluginSettingsForm.settingsState)
    }

    override fun reset() {
        if (pluginSettings.state != null) {
            pluginSettingsForm.settingsState = pluginSettings.state
        }
    }

    init {
        initExecutableChooser()
    }
}
