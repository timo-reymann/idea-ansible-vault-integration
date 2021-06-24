package com.github.timo_reymann.ansible_vault_integration.settings;

import java.util.Objects;
import java.util.Optional;

/**
 * State for ansible vault settings
 */
public class AnsibleVaultSettingsState {
    public static Integer DEFAULT_TIMEOUT_SECONDS = 3;
    public static String DEFAULT_VAULT_EXECUTABLE = "/usr/bin/ansible-vault";

    private String vaultExecutable;
    private String vaultArguments;
    private Integer timeout;

    public AnsibleVaultSettingsState() {
    }

    public AnsibleVaultSettingsState(String vaultExecutable, String vaultArguments, Integer timeout) {
        this.vaultExecutable = vaultExecutable;
        this.vaultArguments = vaultArguments;
        this.timeout = timeout;
    }

    public void setVaultExecutable(String vaultExecutable) {
        this.vaultExecutable = vaultExecutable;
    }

    public void setVaultArguments(String vaultArguments) {
        this.vaultArguments = vaultArguments;
    }

    public String getVaultExecutable() {
        return vaultExecutable;
    }

    public Optional<String> vaultExecutable() {
        return Optional.ofNullable(vaultExecutable);
    }

    public String getVaultArguments() {
        return vaultArguments;
    }

    public Optional<String> vaultArguments() {
        return Optional.ofNullable(vaultArguments);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnsibleVaultSettingsState that = (AnsibleVaultSettingsState) o;
        return Objects.equals(vaultExecutable, that.vaultExecutable) && Objects.equals(vaultArguments, that.vaultArguments) && Objects.equals(timeout, that.timeout);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vaultExecutable, vaultArguments, timeout);
    }

    public int getTimeout() {
        return timeout == null ? DEFAULT_TIMEOUT_SECONDS : timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }
}
