package com.github.timo_reymann.ansible_vault_integration.util;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Util for ansible vault string manipulation
 */
public class AnsibleVaultedStringUtil {
    public static final String PREFIX = "!vault |";

    public static boolean isVaultedString(String input) {
        return input != null && input.trim().startsWith(PREFIX);
    }

    public static String removePrefix(String vaultedString) {
        String[] lines = vaultedString.split("\\n");
        return Arrays.stream(lines)
                // remove first line
                .skip(1)
                // Remove trailing whitespace
                .map(String::trim)
                .collect(Collectors.joining("\n"));
    }

    public static String addPrefix(String vaultedString) {
        String[] rawLines = vaultedString.split("\\n");
        String[] suffixedLines = new String[rawLines.length + 1];
        suffixedLines[0] = PREFIX;
        System.arraycopy(rawLines, 0, suffixedLines, 1, rawLines.length);
        return String.join("\n", suffixedLines);
    }
}
