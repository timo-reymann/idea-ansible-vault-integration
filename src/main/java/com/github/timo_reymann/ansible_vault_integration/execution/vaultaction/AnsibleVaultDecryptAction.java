package com.github.timo_reymann.ansible_vault_integration.execution.vaultaction;

import com.github.timo_reymann.ansible_vault_integration.util.AnsibleVaultedStringUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;

import java.util.Collections;
import java.util.List;

public class AnsibleVaultDecryptAction extends AnsibleVaultAction {
    private final String encrypted;

    public AnsibleVaultDecryptAction(Project project, PsiFile contextFile, String encrypted) {
        super(project, contextFile);
        this.encrypted = encrypted;
    }

    @Override
    protected String getActionName() {
        return "decrypt";
    }

    @Override
    protected String getStdin() {
        return AnsibleVaultedStringUtil.removePrefix(encrypted);
    }

    @Override
    protected List<String> getParameters() {
        return Collections.singletonList("--output=-");
    }

}
