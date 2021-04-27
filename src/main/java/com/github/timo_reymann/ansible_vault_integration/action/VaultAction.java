package com.github.timo_reymann.ansible_vault_integration.action;

import com.github.timo_reymann.ansible_vault_integration.execution.AnsibleVaultRunnable;
import com.github.timo_reymann.ansible_vault_integration.execution.AnsibleVaultTask;
import com.github.timo_reymann.ansible_vault_integration.execution.AnsibleVaultWrapper;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLElementGenerator;
import org.jetbrains.yaml.YAMLLanguage;
import org.jetbrains.yaml.psi.YAMLValue;

import static org.jetbrains.yaml.YAMLTokenTypes.*;

/**
 * Vault Action to provide unvault for yaml files
 */
public class VaultAction extends PsiElementBaseIntentionAction implements IntentionAction {

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getText() {
        return "Vault ansible secret";
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return getText();
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        // Only valid in yaml files
        if (!element.getLanguage().equals(YAMLLanguage.INSTANCE)) {
            return false;
        }

        // Allow any kind of text in single or dobule quotes or without quotes
        IElementType elementType = element.getNode().getElementType();
        return elementType.equals(TEXT) || elementType.equals(SCALAR_STRING) || elementType.equals(SCALAR_DSTRING);
    }

    private String extractValue(PsiElement element) {
        IElementType elementType = element.getNode().getElementType();
        String text = element.getNode().getText();

        // plain text -> no modification required
        if (elementType.equals(TEXT)) {
            return text;
        }

        // remove quotes
        return text.substring(1, text.length() - 1);
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        String content = extractValue(element);
        final PsiFile containingFile = element.getContainingFile();
        AnsibleVaultTask task = new AnsibleVaultTask(project, "Decrypt Secret", new AnsibleVaultRunnable() {
            @Override
            public void run() throws Exception {
                String encrypted = AnsibleVaultWrapper.encrypt(project, containingFile, content);
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
        });

        ProgressManager.getInstance().run(task);
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }
}
