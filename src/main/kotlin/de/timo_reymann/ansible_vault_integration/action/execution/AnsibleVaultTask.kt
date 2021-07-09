package de.timo_reymann.ansible_vault_integration.action.execution

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task.Backgroundable
import com.intellij.openapi.project.Project
import de.timo_reymann.ansible_vault_integration.action.runnable.AnsibleVaultRunnable
import org.jetbrains.annotations.Nls

/**
 * Background task abstraction for executing ansible vault operations
 */
class AnsibleVaultTask(
    project: Project?,
    title: @Nls(capitalization = Nls.Capitalization.Sentence) String,
    private val task: AnsibleVaultRunnable
) : Backgroundable(project, title) {
    override fun run(indicator: ProgressIndicator) {
        indicator.isIndeterminate = true
        indicator.text = this.title
        var notification: Notification
        try {
            task.run()
            notification = Notification(
                NOTIFICATION_GROUP_ID,
                NOTIFICATION_SUCCESS_ID,
                task.successMessage,
                NotificationType.INFORMATION
            )
        } catch (e: Exception) {
            log.warn(e)
            notification = Notification(
                NOTIFICATION_GROUP_ID,
                NOTIFICATION_ERROR_ID,
                e.message!!,
                NotificationType.ERROR
            )
            indicator.cancel()
        }
        Notifications.Bus.notify(notification)
    }

    companion object {
        private val log = Logger.getInstance(AnsibleVaultTask::class.java)

        val NOTIFICATION_GROUP_ID = AnsibleVaultTask::class.java.canonicalName
        const val NOTIFICATION_ERROR_ID = "Error"
        const val NOTIFICATION_SUCCESS_ID = "Success"
    }
}
