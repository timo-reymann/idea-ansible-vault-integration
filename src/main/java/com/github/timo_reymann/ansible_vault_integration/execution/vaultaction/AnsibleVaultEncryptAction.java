package com.github.timo_reymann.ansible_vault_integration.execution.vaultaction;

import com.github.timo_reymann.ansible_vault_integration.execution.AnsibleVaultWrapperCallFailedException;
import com.github.timo_reymann.ansible_vault_integration.util.AnsibleVaultedStringUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;

import java.util.Collections;
import java.util.List;

public class AnsibleVaultEncryptAction extends AnsibleVaultAction {
    private final String plain;

    public AnsibleVaultEncryptAction(Project project, PsiFile contextFile, String plain) {
        super(project, contextFile);
        this.plain = plain;
    }

    @Override
    protected String getActionName() {
        return "encrypt";
    }

    @Override
    protected String getStdin() {
        return plain;
    }

    @Override
    protected List<String> getParameters() {
        return Collections.singletonList("--output=-");
    }

    @Override
    public String execute() throws AnsibleVaultWrapperCallFailedException {
        return AnsibleVaultedStringUtil.addPrefix(super.execute());
    }
}
