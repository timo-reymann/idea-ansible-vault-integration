import com.intellij.openapi.actionSystem.AnActionEvent

internal fun AnActionEvent.setVisible(visiblity: Boolean) {
    this.presentation.isEnabledAndVisible = visiblity
}
