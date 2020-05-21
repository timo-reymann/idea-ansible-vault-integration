package com.github.timo_reymann.ansible_vault_integration.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Settings for ansible vault execution
 */
@State(name = "com.github.timo_reymann.ansible_vault_integration.settings.AnsibleVaultSettings", storages = {@Storage("ansibleVaultSettings.xml")})
public class AnsibleVaultSettings implements PersistentStateComponent<AnsibleVaultSettingsState> {
    private AnsibleVaultSettingsState state;

    @Nullable
    @Override
    public AnsibleVaultSettingsState getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull AnsibleVaultSettingsState state) {
        this.state = state;
        if (this.state.getVaultExecutable() == null || this.state.getVaultExecutable().isEmpty()) {
            this.setDefaultExecutable();
        }
    }

    public static AnsibleVaultSettings getInstance(Project project) {
        return ServiceManager.getService(project, AnsibleVaultSettings.class);
    }

    @Override
    public void noStateLoaded() {
        this.setDefaultExecutable();
    }

    public void setDefaultExecutable() {
        if (this.state == null) {
            this.state = new AnsibleVaultSettingsState();
        }

        this.state.setVaultExecutable(AnsibleVaultSettingsState.DEFAULT_VAULT_EXECUTABLE);
    }
}
