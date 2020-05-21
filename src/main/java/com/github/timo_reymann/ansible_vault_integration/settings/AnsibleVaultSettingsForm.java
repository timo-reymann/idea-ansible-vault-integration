package com.github.timo_reymann.ansible_vault_integration.settings;

import javax.swing.*;

/**
 * Form for project settings
 */
public class AnsibleVaultSettingsForm {
    private JPanel settingsPanel;
    private JTextField vaultExecutableField;
    private JButton vaultExecutableChooser;
    private JTextField vaultArgumentsField;

    public JPanel getSettingsPanel() {
        return settingsPanel;
    }

    public AnsibleVaultSettingsState getSettingsState() {
        return new AnsibleVaultSettingsState(vaultExecutableField.getText(), vaultArgumentsField.getText());
    }

    public void setSettingsState(AnsibleVaultSettingsState settingsState) {
        vaultExecutableField.setText(settingsState.getVaultExecutable());
        vaultArgumentsField.setText(settingsState.getVaultArguments());
    }

    public JTextField getVaultExecutableField() {
        return vaultExecutableField;
    }

    public JButton getVaultExecutableChooser() {
        return vaultExecutableChooser;
    }

    public JTextField getVaultArgumentsField() {
        return vaultArgumentsField;
    }
}
