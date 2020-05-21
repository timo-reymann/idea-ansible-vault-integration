package com.github.timo_reymann.ansible_vault_integration.execution;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Background task abstraction for executing ansible vault operations
 */
public class AnsibleVaultTask extends Task.Backgroundable {
    private static final Logger log = Logger.getInstance(AnsibleVaultTask.class);
    public static String NOTIFICATION_GROUP_ID = AnsibleVaultTask.class.getCanonicalName();
    public static String NOTIFICATION_ERROR_ID = "Error";
    public static String NOTIFICATION_SUCCESS_ID = "Success";

    private final AnsibleVaultRunnable task;

    public AnsibleVaultTask(@Nullable Project project, @Nls(capitalization = Nls.Capitalization.Title) @NotNull String title, AnsibleVaultRunnable task) {
        super(project, title);
        this.task = task;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        indicator.setIndeterminate(true);
        indicator.setText(this.myTitle);
        Notification notification = null;

        try {
            this.task.run();
            notification = new Notification(
                    NOTIFICATION_GROUP_ID,
                    NOTIFICATION_SUCCESS_ID,
                    task.getSuccessMessage(),
                    NotificationType.INFORMATION
            );
        } catch (Exception e) {
            log.error(e);
            notification = new Notification(
                    NOTIFICATION_GROUP_ID,
                    NOTIFICATION_ERROR_ID,
                    e.getMessage(),
                    NotificationType.ERROR
            );
            indicator.cancel();
        }

        Notifications.Bus.notify(notification);
    }
}
