package de.timo_reymann.ansible_vault_integration.action.commandline

class NoOpAnsibleCommandLineTransformer : AnsibleCommandLineTransformer {
    override fun transformFileName(input: String?): String? = input
}
