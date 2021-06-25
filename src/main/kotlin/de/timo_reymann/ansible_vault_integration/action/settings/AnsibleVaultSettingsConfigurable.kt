package de.timo_reymann.ansible_vault_integration.action.settings

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.JBTextField
import com.intellij.ui.layout.panel
import org.jetbrains.annotations.Nls
import java.io.File
import javax.swing.JComponent

/**
 * Configurable for ansible-vault configuration
 */
open class AnsibleVaultSettingsConfigurable(project: Project) : Configurable {
    private lateinit var executionTimeoutField: JBTextField
    private lateinit var argumentsField: JBTextField
    private lateinit var executableField: TextFieldWithBrowseButton
    private val pluginSettings: AnsibleVaultSettings = AnsibleVaultSettings.getInstance(project)

    private val panel = panel {
        row("Executable") {
            textFieldWithBrowseButton(
                prop = pluginSettings::vaultExecutable,
                browseDialogTitle = "Select ansible vault executable",
                fileChooserDescriptor = FileChooserDescriptorFactory.createSingleFileOrExecutableAppDescriptor()
            ).comment("Path to ansible vault executable or wrapper script")
                .also { executableField = it.component }
        }

        row("Command line arguments") {
            textField(pluginSettings::vaultArguments)
                .comment("Arguments to suffix to execution")
                .focused()
                .also { argumentsField = it.component }
        }

        row("Execution Timeout") {
            textField(
                { pluginSettings.timeout.toString() },
                { pluginSettings.timeout = it.tryParseInt() ?: return@textField }
            )
                .comment("Amount in seconds to wait before stopping execution forcefully")
                .also { executionTimeoutField = it.component }
        }

        noteRow("""
        You have a complex setup with different secrets for different maturities? - 
        <a href="https://plugins.jetbrains.com/plugin/14353-ansible-vault-integration/tutorials/vault-file-as-script">I got you covered!</a>
        """.trimIndent())
    }

    override fun getDisplayName(): @Nls String = "Ansible Vault"
    override fun getHelpTopic(): String = "Configure Ansible vault"
    override fun createComponent(): JComponent = panel
    override fun isModified(): Boolean = panel.isModified()
    override fun apply() {
        if (!File(executableField.text).exists()) {
            throw ConfigurationException("Invalid vault executable")
        }

        val timeout = executionTimeoutField.text.tryParseInt()
        if (timeout == null || timeout < 1) {
            throw ConfigurationException("Invalid execution timeout, must be greater than 1")
        }

        panel.apply()
    }

    override fun reset() = panel.reset()
}

private fun String.tryParseInt(): Int? = try {
    Integer.parseInt(this)
} catch (e: Exception) {
    null
}
