package de.timo_reymann.ansible_vault_integration.commandline

class NoOpAnsibleCommandLineTransformer : AnsibleCommandLineTransformer {
    override fun transformFileName(input: String?): String? = input
}
