package de.timo_reymann.ansible_vault_integration.execution

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task.Backgroundable
import com.intellij.openapi.project.Project
import de.timo_reymann.ansible_vault_integration.runnable.AnsibleVaultRunnable
import de.timo_reymann.ansible_vault_integration.runnable.VaultRunnableMode
import de.timo_reymann.ansible_vault_integration.runnable.VaultRunnableType
import org.jetbrains.annotations.Nls

/**
 * Background task abstraction for executing ansible vault operations
 */
class AnsibleVaultTaskRunner(
    project: Project?,
    private val initialTitle: @Nls(capitalization = Nls.Capitalization.Sentence) String,
    private val tasks: List<AnsibleVaultRunnable>
) : Backgroundable(project, initialTitle) {
    override fun run(indicator: ProgressIndicator) {
        indicator.isIndeterminate = true
        indicator.text = this.initialTitle

        val succeeded = mutableListOf<AnsibleVaultRunnable>()
        val failed = mutableListOf<Pair<AnsibleVaultRunnable, Exception>>()
        tasks.forEach {
            try {
                runTask(indicator, it)
                succeeded += it
            } catch (e: Exception) {
                failed += Pair(it, e)
            }
        }
        var notification: Notification? = null
        val actionsExecuted = toHtmList(
            "Successful actions",
            succeeded.map { it.type.stringify(it.fileName) }
        )
        val actionsFailed = toHtmList(
            "Failed actions",
            failed.map { "${it.first.type.stringify(it.first.fileName)}: <code>${it.second.message ?: "Unexpected error"}</code>" }
        )

        if (failed.size > 1 || succeeded.size > 1) { // for multiple selected
            when {
                failed.size == 0 -> { // no failed tasks
                    notification = Notification(
                        NOTIFICATION_GROUP_ID,
                        "Successfully processed ${succeeded.size} file${if (succeeded.size > 1) "s" else ""}",
                        actionsExecuted,
                        NotificationType.INFORMATION
                    )
                }

                failed.size > 0 && succeeded.size > 0 -> { // partially failed
                    notification = Notification(
                        NOTIFICATION_GROUP_ID,
                        "Processed ${succeeded.size + failed.size} file${if ((succeeded.size + failed.size) > 1) "s" else ""} with ${failed.size} errors",
                        "$actionsExecuted\n<br />$actionsFailed",
                        NotificationType.WARNING
                    )
                }

                else -> { // all failed
                    notification = Notification(
                        NOTIFICATION_GROUP_ID,
                        "Failed to process ${failed.size} file${if ((succeeded.size + failed.size) > 1) "s" else ""}",
                        actionsFailed,
                        NotificationType.WARNING
                    )
                }
            }
        } else { // for a single file
            when {
                failed.size == 1 -> {
                    notification = Notification(
                        NOTIFICATION_GROUP_ID,
                        "${failed[0].first.type} failed for ${failed[0].first.fileName}",
                        failed[0].second.message ?: "Unknown error",
                        NotificationType.ERROR
                    )
                }

                succeeded.size == 1 -> {
                    val isInline = succeeded[0].mode == VaultRunnableMode.INLINE

                    notification = Notification(
                        NOTIFICATION_GROUP_ID,
                        "${succeeded[0].type} succeeded for ${succeeded[0].fileName}",
                        if (isInline && succeeded[0].type == VaultRunnableType.DECRYPT) "Copied decrypted value to clipboard" else "",
                        NotificationType.INFORMATION
                    )
                }
            }
        }

        if (notification != null) {
            Notifications.Bus.notify(notification)
        }
    }

    private fun toHtmList(title: String, items: List<String>): String {
        return """
            <p>Check the entire message for a more detailed report.</p>
            <br />
            <strong>${title}</strong>
            <ul>
                ${items.joinToString("\n") { "<li>${it}</li>" }}
            </ul>
        """.trimIndent()
    }

    private fun runTask(indicator: ProgressIndicator, task: AnsibleVaultRunnable) {
        val action = if (task.type == VaultRunnableType.DECRYPT) "Unvault" else "Vault"
        indicator.text = "$action ${task.fileName} using ansible-vault"
        task.run()
    }

    companion object {
        private val log = Logger.getInstance(AnsibleVaultTaskRunner::class.java)

        val NOTIFICATION_GROUP_ID: String = AnsibleVaultTaskRunner::class.java.canonicalName
        const val NOTIFICATION_ERROR_ID = "Error"
        const val NOTIFICATION_SUCCESS_ID = "Success"
    }
}
