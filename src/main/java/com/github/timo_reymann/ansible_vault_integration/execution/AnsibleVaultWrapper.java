package com.github.timo_reymann.ansible_vault_integration.execution;

import com.github.timo_reymann.ansible_vault_integration.settings.AnsibleVaultSettings;
import com.github.timo_reymann.ansible_vault_integration.settings.AnsibleVaultSettingsState;
import com.github.timo_reymann.ansible_vault_integration.util.AnsibleVaultedStringUtil;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.io.FileUtil;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Wrapper for ansible-vault command
 */
public class AnsibleVaultWrapper {
    public static String decrypt(Project project, String vaulted) throws Exception {
        return performAction(project, "decrypt", AnsibleVaultedStringUtil.removePrefix(vaulted));
    }

    public static String encrypt(Project project, String raw) throws Exception {
        return AnsibleVaultedStringUtil.addPrefix(performAction(project,"encrypt", raw));
    }

    public static String performAction(Project project, String action, String input) throws Exception {
        File tempFile = FileUtil.createTempFile("vault", "tmp");
        FileUtil.writeToFile(tempFile, input);

        Exception exception = null;
        List<String> stdout = null;
        try {
            stdout = execute(project, Arrays.asList(action, tempFile.getAbsolutePath(), "--output=-"));
        } catch (Exception e) {
            exception = e;
        } finally {
            if (!tempFile.delete()) {
                PluginManager.getLogger().warn("Could not delete file " + tempFile.getAbsolutePath());
            }
        }

        if (exception != null) {
            throw exception;
        }

        return String.join("\n", stdout);
    }

    protected static List<String> execute(Project project, List<String> parameters) throws
            AnsibleVaultWrapperCallFailedException {
        AnsibleVaultSettingsState state = AnsibleVaultSettings.getInstance(project).getState();
        String vaultExecutable = state.vaultExecutable()
                .orElseThrow(() -> new AnsibleVaultWrapperCallFailedException("No ansible-vault executable set"));
        String[] vaultArguments = state.vaultArguments()
                .orElse("")
                .split(" ");
        List<String> effectiveArgs = new ArrayList<>(parameters);
        if (vaultArguments.length > 0 && !vaultArguments[0].trim().equals("")) {
            effectiveArgs.addAll(Arrays.asList(vaultArguments));
        }

        ProcessHandler processHandler = null;
        List<String> stdout = new ArrayList<>();
        AtomicLong line = new AtomicLong(0);
        try {
            processHandler = new OSProcessHandler(new GeneralCommandLine(vaultExecutable)
                    .withParameters(effectiveArgs)
                    .withWorkDirectory(project.getBasePath())
            );

            // output
            processHandler.addProcessListener(new ProcessAdapter() {
                @Override
                public synchronized void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
                    String stdoutLine = event.getText().trim();

                    // Empty line or line break
                    if (stdoutLine.equals("") || stdoutLine.startsWith("WARNING:")) {
                        return;
                    }

                    // First line is command output, remove it from stdout
                    if (line.incrementAndGet() == 1) {
                        return;
                    }

                    stdout.add(stdoutLine);
                }
            });

            processHandler.startNotify();
            // Error waitng for graceful exit
            if (!processHandler.waitFor(3_000)) {
                processHandler.destroyProcess();
                throw new AnsibleVaultWrapperCallFailedException("Command timed out: <pre>" + String.join("<br />", stdout) + "</pre>");
            }

            if (processHandler.getExitCode() != null && processHandler.getExitCode() != 0) {
                throw new AnsibleVaultWrapperCallFailedException("Exited with code " + processHandler.getExitCode() + ": " + String.join("<br />", stdout));
            }
        } catch (ExecutionException | NullPointerException e) {
            throw new AnsibleVaultWrapperCallFailedException("Internal error: " + e.getMessage());
        }

        return stdout;
    }
}
