package com.github.timo_reymann.ansible_vault_integration.settings;

import com.intellij.ui.DocumentAdapter;
import de.timo_reymann.ansible_vault_integration.action.settings.AnsibleVaultSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;

/**
 * Form for project settings
 */
public class AnsibleVaultSettingsForm {
    private JPanel settingsPanel;
    private JTextField vaultExecutableField;
    private JButton vaultExecutableChooser;
    private JTextField vaultArgumentsField;
    private JTextField executionTimeoutField;

    public JPanel getSettingsPanel() {
        return settingsPanel;
    }

    public AnsibleVaultSettings getSettingsState() {
        Integer timeout = getParsedTimeout();
        AnsibleVaultSettings settings = new AnsibleVaultSettings();
        settings.setVaultArguments(vaultArgumentsField.getText());
        settings.setTimeout(timeout);
        settings.setVaultExecutable(vaultExecutableField.getText());
        return settings;
    }

    @Nullable
    private Integer getParsedTimeout() {
        Integer timeout;
        try {
            timeout = Integer.parseInt(executionTimeoutField.getText());
        } catch (Exception e) {
            timeout = null;
        }
        return timeout;
    }

    public void setSettingsState(AnsibleVaultSettings settingsState) {
        vaultExecutableField.setText(settingsState.getVaultExecutable());
        vaultArgumentsField.setText(settingsState.getVaultArguments());
        executionTimeoutField.setText(String.valueOf(settingsState.getTimeout()));
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

    private void createUIComponents() {
        executionTimeoutField = new JTextField();
        executionTimeoutField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                boolean isEmpty = executionTimeoutField.getText().equals("");
                boolean hasError = !isEmpty && getParsedTimeout() == null;

                String outline = hasError ? "error" : null;
                executionTimeoutField.putClientProperty("JComponent.outline", outline);
                executionTimeoutField.setToolTipText(hasError ? "Invalid timeout" : "");
            }
        });
    }
}
