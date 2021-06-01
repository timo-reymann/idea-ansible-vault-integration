package com.github.timo_reymann.ansible_vault_integration.execution.runnable;

import com.github.timo_reymann.ansible_vault_integration.execution.vaultaction.AnsibleVaultEncryptAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLElementGenerator;
import org.jetbrains.yaml.psi.YAMLValue;

public  class EncryptAnsibleVaultRunnable implements AnsibleVaultRunnable {
    private final Project project;
    private final PsiFile containingFile;
    private final String content;
    private final PsiElement element;

    public EncryptAnsibleVaultRunnable(Project project, PsiFile containingFile, String content, PsiElement element) {
        this.project = project;
        this.containingFile = containingFile;
        this.content = content;
        this.element = element;
    }

    @Override
    public void run() throws Exception {
        String encrypted = new AnsibleVaultEncryptAction(project, containingFile, content)
                .execute();
        WriteCommandAction.runWriteCommandAction(project, () -> {
            YAMLValue generatedReplacement = new YAMLElementGenerator(project).createYamlKeyValue(element.getParent().getText(), encrypted).getValue();

            // Just in case
            if (element.getContext() == null || generatedReplacement == null) {
                return;
            }

            element.getContext().replace(generatedReplacement);
        });
    }

    @NotNull
    @Override
    public String getSuccessMessage() {
        return "String vaulted and replaced";
    }
}