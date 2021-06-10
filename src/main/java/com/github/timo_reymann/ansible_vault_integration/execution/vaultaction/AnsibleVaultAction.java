package com.github.timo_reymann.ansible_vault_integration.execution.vaultaction;

import com.github.timo_reymann.ansible_vault_integration.execution.*;
import com.github.timo_reymann.ansible_vault_integration.execution.commandline.AnsibleCommandLineTransformer;
import com.github.timo_reymann.ansible_vault_integration.execution.commandline.AnsibleVaultCommandLineBuilder;
import com.github.timo_reymann.ansible_vault_integration.execution.commandline.NoOpAnsibleCommandLineTransformer;
import com.github.timo_reymann.ansible_vault_integration.execution.commandline.WslAnsibleCommandLineTransformer;
import com.github.timo_reymann.ansible_vault_integration.settings.AnsibleVaultSettings;
import com.github.timo_reymann.ansible_vault_integration.settings.AnsibleVaultSettingsState;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.wsl.WSLDistribution;
import com.intellij.execution.wsl.WslPath;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public abstract class AnsibleVaultAction {
    private static final String ENVIRONMENT_PREFIX = "IDEA_ANSIBLE_VAULT_";
    private static final String ENVIRONMENT_CONTEXT_FILE = ENVIRONMENT_PREFIX + "CONTEXT_FILE";
    private static final String ENVIRONMENT_CONTEXT_DIRECTORY = ENVIRONMENT_PREFIX + "CONTEXT_DIRECTORY";
    protected final PsiFile contextFile;
    protected final Project project;

    public AnsibleVaultAction(Project project, PsiFile contextFile) {
        this.project = project;
        this.contextFile = contextFile;
    }

    protected File createTempFile(String input) throws IOException {
        File tempFile = FileUtil.createTempFile("vault", "tmp");
        FileUtil.writeToFile(tempFile, input);
        return tempFile;
    }

    protected abstract String getActionName();

    protected abstract String getStdin();

    protected abstract List<String> getParameters();

    public String execute() throws AnsibleVaultWrapperCallFailedException {
        return String.join("\n", executeCommand());
    }

    private List<String> executeCommand() throws
            AnsibleVaultWrapperCallFailedException {
        ProcessHandler processHandler;
        List<String> stdout = new ArrayList<>();
        AtomicLong line = new AtomicLong(0);
        try {
            Path contextPath = contextFile.getVirtualFile().toNioPath();
            processHandler = new OSProcessHandler(getVaultCommandLine(project, contextPath, getActionName(), getParameters(), getStdin()));

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
            // Error waiting for graceful exit
            if (!processHandler.waitFor( AnsibleVaultSettings.getInstance(project).getState().getTimeout() * 1_000L)) {
                processHandler.destroyProcess();
                throw new AnsibleVaultWrapperCallFailedException("Command timed out: <pre>" + String.join("<br />", stdout) + "</pre>");
            }

            if (processHandler.getExitCode() != null && processHandler.getExitCode() != 0) {
                throw new AnsibleVaultWrapperCallFailedException("Exited with code " + processHandler.getExitCode() + ": " + String.join("<br />", stdout));
            }
        } catch (ExecutionException | NullPointerException | IOException e) {
            throw new AnsibleVaultWrapperCallFailedException("Internal error: " + e.getMessage());
        }

        return stdout;
    }


    private boolean runsInWsl(String vaultExecutable) {
        return SystemInfo.isWin10OrNewer && WslPath.parseWindowsUncPath(vaultExecutable) != null;
    }

    private GeneralCommandLine getVaultCommandLine(Project project, Path contextPath, String action, List<String> parameters, String stdin) throws AnsibleVaultWrapperCallFailedException, IOException, ExecutionException {
        AnsibleVaultSettingsState state = AnsibleVaultSettings.getInstance(project).getState();
        String vaultExecutable = state.vaultExecutable()
                .orElseThrow(() -> new AnsibleVaultWrapperCallFailedException("No ansible-vault executable set"));
        List<String> vaultArguments = Arrays.stream(state.vaultArguments()
                .orElse("")
                .split(" "))
                .filter(val -> !val.trim().equals(""))
                .collect(Collectors.toList());

        File stdinFile = createTempFile(stdin);
        boolean isWsl = runsInWsl(vaultExecutable);

        AnsibleCommandLineTransformer ansibleCommandLineTransformer = null;
        if (isWsl) {
            ansibleCommandLineTransformer = new WslAnsibleCommandLineTransformer(
                    WslPath.parseWindowsUncPath(vaultExecutable).getDistribution()
            );
        } else {
            ansibleCommandLineTransformer = new NoOpAnsibleCommandLineTransformer();
        }

        return new AnsibleVaultCommandLineBuilder(vaultExecutable, ansibleCommandLineTransformer)
                .withParameter(action)
                .withFilePathParameter(stdinFile.getAbsolutePath())
                .withEnv(ENVIRONMENT_CONTEXT_DIRECTORY, contextPath.toFile().getParentFile().getName())
                .withFilePathEnv(ENVIRONMENT_CONTEXT_FILE, contextPath.toString())
                .getCommandLine(project)
                .withWorkDirectory(project.getBasePath())
                .withParameters(parameters)
                .withParameters(vaultArguments);
    }
}
