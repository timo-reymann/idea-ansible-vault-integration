package com.github.timo_reymann.ansible_vault_integration.execution.runnable;

import com.github.timo_reymann.ansible_vault_integration.execution.vaultaction.AnsibleVaultDecryptAction;
import com.intellij.ide.ClipboardSynchronizer;
import com.intellij.ide.CopyPasteManagerEx;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.ui.TextTransferable;
import org.jetbrains.annotations.NotNull;

public class DecryptAnsibleVaultRunnable implements AnsibleVaultRunnable {
    private final Project project;
    private final PsiFile containingFile;
    private final String raw;

    public DecryptAnsibleVaultRunnable(Project project, PsiFile containingFile, String raw) {
        this.project = project;
        this.containingFile = containingFile;
        this.raw = raw;
    }

    @Override
    public void run() throws Exception {
        String decrypted = new AnsibleVaultDecryptAction(project, containingFile, raw)
                .execute();
        ClipboardSynchronizer.getInstance()
                .setContent(new TextTransferable(decrypted, decrypted), CopyPasteManagerEx.getInstanceEx());
    }

    @NotNull
    @Override
    public String getSuccessMessage() {
        return "Decrypted secret has been copied to your clipboard";
    }
}
