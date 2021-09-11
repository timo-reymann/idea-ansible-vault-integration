package de.timo_reymann.ansible_vault_integration.config

import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor

data class VaultIdentity(
    val name: String,
    val vaultFile: String
)

data class AnsibleConfigurationFile(
    val vaultIdentities: List<VaultIdentity>?,
    val checksum: String?,
    val priority: Int?
)

// credits to mfulton26: https://stackoverflow.com/a/44570679/6709262
inline infix fun <reified T : Any> T.merge(other: T): T {
    val propertiesByName = T::class.declaredMemberProperties.associateBy { it.name }
    val primaryConstructor = T::class.primaryConstructor
        ?: throw IllegalArgumentException("merge type must have a primary constructor")
    val args = primaryConstructor.parameters.associateWith { parameter ->
        val property = propertiesByName[parameter.name]
            ?: throw IllegalStateException("no declared member property found with name '${parameter.name}'")
        (property.get(this) ?: property.get(other))
    }
    return primaryConstructor.callBy(args)
}

fun mergeConfigs(configs: List<AnsibleConfigurationFile>): AnsibleConfigurationFile {
    var final = AnsibleConfigurationFile(null, null, null)

    configs.sortedByDescending { it.priority }
        .forEach { final = final merge it }

    return final.copy(checksum = null,priority = null)
}
