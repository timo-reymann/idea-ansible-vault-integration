package com.github.timo_reymann.ansible_vault_integration.settings;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.ex.FileChooserDialogImpl;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.apache.http.util.TextUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Configurable for ansible-vault configuration
 */
public class AnsibleVaultSettingsConfigurable implements Configurable {
    private final AnsibleVaultSettingsForm pluginSettingsForm;
    private final AnsibleVaultSettings pluginSettings;
    private FileChooserDescriptor ansibleVaultExecutableChooser;
    private final Project project;
    private VirtualFile selectedVaultExecutable;

    public AnsibleVaultSettingsConfigurable(Project project) {
        this.pluginSettingsForm = new AnsibleVaultSettingsForm();
        this.pluginSettings = AnsibleVaultSettings.getInstance(project);
        this.project = project;
        this.initExecutableChooser();
    }

    /**
     * Initialize file chooser for ansible-vault executable
     */
    private void initExecutableChooser() {
        this.ansibleVaultExecutableChooser = new FileChooserDescriptor(true, false, false, false, false, false);
        String ansibleVaultExecutable = "";
        if (pluginSettings.getState() != null) {
            ansibleVaultExecutable = pluginSettings.getState().getVaultExecutable();
        }

        if (!TextUtils.isEmpty(ansibleVaultExecutable)) {
            selectedVaultExecutable = VirtualFileManager.getInstance()
                    .findFileByUrl(getFileUrl(ansibleVaultExecutable));
        }

        pluginSettingsForm.getVaultExecutableChooser().addActionListener(e -> {
            VirtualFile[] chosenFile = new FileChooserDialogImpl(ansibleVaultExecutableChooser, project)
                    .choose(project, selectedVaultExecutable);
            if (chosenFile.length != 1) {
                return;
            }

            VirtualFile file = chosenFile[0];
            if (file == null) {
                return;
            }

            selectedVaultExecutable = file;
            pluginSettingsForm.getVaultExecutableField().setText(file.getCanonicalPath());
        });
    }

    private String getFileUrl(String path) {
        return "file://" + path;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Ansible Vault";
    }

    @Override
    public String getHelpTopic() {
        return "Configure Ansible vault";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return pluginSettingsForm.getSettingsPanel();
    }

    @Override
    public boolean isModified() {
        return !pluginSettingsForm.getSettingsState().equals(pluginSettings.getState());
    }

    @Override
    public void apply() {
        pluginSettings.loadState(pluginSettingsForm.getSettingsState());
    }

    @Override
    public void reset() {
        if (pluginSettings.getState() != null) {
            pluginSettingsForm.setSettingsState(pluginSettings.getState());
        }
    }

    @Override
    public void disposeUIResources() {
    }
}
