package de.timo_reymann.ansible_vault_integration.config

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.SystemInfo
import org.apache.commons.io.FileUtils
import java.io.File

/**
 * Search for all possible configurations as listed in
 * https://docs.ansible.com/ansible/latest/reference_appendices/config.html#the-configuration-file
 */
class AnsibleConfigurationFileFinder(private val project: Project) {
    /**
     * Return all possible folders for ansible config files, where the order is important and will be mapped to priority
     */
    private fun getPossibleFolders(): MutableList<File> {
        val locations = mutableListOf<File>()
        if (SystemInfo.isLinux || SystemInfo.isMac || SystemInfo.isUnix) {
            locations.add(File("/etc/ansible/"))
        }
        locations.add(FileUtils.getUserDirectory())
        locations.add(File(project.basePath!!))
        return locations
    }

    fun getAllProcessableConfigs(): List<File> = getPossibleFolders()
        .map { File(it, "ansible.cfg") }
        .filter { it.isFile && it.exists() }
}
