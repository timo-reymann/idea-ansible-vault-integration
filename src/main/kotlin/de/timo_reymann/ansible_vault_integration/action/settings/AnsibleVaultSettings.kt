package de.timo_reymann.ansible_vault_integration.action.settings

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project

/**
 * Settings for ansible vault execution
 */
@State(
    name = "com.github.timo_reymann.ansible_vault_integration.settings.AnsibleVaultSettings",
    storages = [Storage("ansibleVaultSettings.xml")]
)
open class AnsibleVaultSettings : PersistentStateComponent<AnsibleVaultSettings>, BaseState() {
    var vaultExecutable: String by nonNullString(DEFAULT_VAULT_EXECUTABLE)
    var vaultArguments: String by nonNullString()
    var timeout: Int by property(DEFAULT_TIMEOUT_SECONDS)

    override fun getState(): AnsibleVaultSettings = this

    override fun loadState(state: AnsibleVaultSettings) {
        copyFrom(state)
    }

    override fun noStateLoaded() {
        setDefaultExecutable()
    }

    private fun nonNullString(initialValue: String = "") = property(initialValue) { it == initialValue }

    private fun setDefaultExecutable() {
        vaultExecutable = DEFAULT_VAULT_EXECUTABLE
    }

    companion object {
        fun getInstance(project: Project) = project.service<AnsibleVaultSettings>()
        const val DEFAULT_TIMEOUT_SECONDS = 3
        const val DEFAULT_VAULT_EXECUTABLE = "/usr/bin/ansible-vault"
    }
}
