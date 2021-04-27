package com.github.timo_reymann.ansible_vault_integration.action;

import com.github.timo_reymann.ansible_vault_integration.execution.AnsibleVaultRunnable;
import com.github.timo_reymann.ansible_vault_integration.execution.AnsibleVaultTask;
import com.github.timo_reymann.ansible_vault_integration.execution.AnsibleVaultWrapper;
import com.github.timo_reymann.ansible_vault_integration.util.AnsibleVaultedStringUtil;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.ide.ClipboardSynchronizer;
import com.intellij.ide.CopyPasteManagerEx;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.ui.TextTransferable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLLanguage;

import static org.jetbrains.yaml.YAMLTokenTypes.*;

/**
 * Unvault Action to provide unvault for yaml files
 */
public class UnvaultAction extends PsiElementBaseIntentionAction implements IntentionAction {

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getText() {
        return "Unvault ansible secret";
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

        // Verify there is content and it is a vault string
        return AnsibleVaultedStringUtil.isVaultedString(extractText(element));
    }

    /***
     * Extract text from {@link PsiElement}
     *
     * @param element Element to extract from
     * @return Content or null, if it contains no content
     */
    public String extractText(PsiElement element) {
        IElementType elementType = element.getNode().getElementType();

        // Is inside vaulted string
        if (TAG.equals(elementType) || SCALAR_LIST.equals(elementType) || SCALAR_EOL.equals(elementType)) {
            return element.getParent().getNode().getText();
        }

        // is in tag for vault string potentially
        if (SCALAR_KEY.equals(elementType) && element.getNextSibling() != null && element.getNextSibling().getNextSibling() != null) {
            // <tag>:<space><text>
            return element.getNextSibling().getNextSibling().getNextSibling().getNode().getText();
        }

        return null;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        String raw = extractText(element);
        final PsiFile containingFile = element.getContainingFile();
        AnsibleVaultTask task = new AnsibleVaultTask(project, "Decrypt Secret", new AnsibleVaultRunnable() {
            @Override
            public void run() throws Exception {
                String decrypted = AnsibleVaultWrapper.decrypt(project, containingFile, raw);
                ClipboardSynchronizer.getInstance().setContent(new TextTransferable(decrypted, decrypted), CopyPasteManagerEx.getInstanceEx());
            }

            @NotNull
            @Override
            public String getSuccessMessage() {
                return "Decrpyted secret has been copied to your clipboard";
            }
        });

        ProgressManager.getInstance().run(task);
    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }
}
