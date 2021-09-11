package de.timo_reymann.ansible_vault_integration.config

import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import de.timo_reymann.ansible_vault_integration.settings.AnsibleVaultSettings

class AnsibleConfigurationService(private val project: Project) {
    private val ansibleConfigurationFileFinder = AnsibleConfigurationFileFinder(project)
    private val configCache = mutableMapOf<String, AnsibleConfigurationFile>()

    private fun getConfigs(): List<AnsibleConfigurationFile> {
        return ansibleConfigurationFileFinder.getAllProcessableConfigs()
            .mapIndexed { index, config -> AnsibleConfigurationFileParser(config, priority = index) }
            .mapNotNull {
                try {
                    val checksum = it.calculateChecksum()
                    if (configCache[checksum] == null) {
                        configCache[checksum] = it.parse()
                    }

                    configCache[checksum]
                } catch (e: Exception) {
                    logger<AnsibleConfigurationService>().warn("Failed to parse config file $it, ignoring.", e)
                    null
                }
            }
    }

    fun getAggregatedConfig() : AnsibleConfigurationFile {
        return mergeConfigs(getConfigs())
    }

    companion object {
        fun getInstance(project: Project) = project.service<AnsibleConfigurationService>()
    }
}
