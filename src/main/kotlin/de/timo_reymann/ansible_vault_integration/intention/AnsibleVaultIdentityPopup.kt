package de.timo_reymann.ansible_vault_integration.intention

import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.util.ui.JBUI
import de.timo_reymann.ansible_vault_integration.config.VaultIdentity
import de.timo_reymann.ansible_vault_integration.runnable.EncryptStringAnsibleVaultRunnable
import java.awt.Component
import java.util.function.Consumer
import javax.swing.DefaultListCellRenderer
import javax.swing.Icon
import javax.swing.JList
import javax.swing.ListSelectionModel
import javax.swing.border.Border
import javax.swing.border.EmptyBorder

class AnsibleVaultIdentityPopup(
    private val vaultIdentities: List<VaultIdentity>,
    private val callback: Consumer<VaultIdentity>
) {
    companion object {
        private val cellInsets = JBUI.insets(2, 6)
    }

    private val popup = JBPopupFactory.getInstance().createPopupChooserBuilder(vaultIdentities)
        .setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
        .setAccessibleName("Use vault identity")
        .setTitle("Use Vault Identity")
        .setMovable(false)
        .setResizable(false)
        .setRequestFocus(true)
        .setRenderer(object : DefaultListCellRenderer() {
            override fun getListCellRendererComponent(
                list: JList<*>?,
                value: Any?,
                index: Int,
                isSelected: Boolean,
                cellHasFocus: Boolean
            ): Component {
                val component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
                text = (value as VaultIdentity).name
                return component
            }

            override fun getBorder(): Border = EmptyBorder(cellInsets)
            override fun getIconTextGap(): Int = cellInsets.left
            override fun getIcon(): Icon = AllIcons.FileTypes.Any_type
        })
        .setItemChosenCallback {
            callback.accept(it as VaultIdentity)
        }
        .createPopup()

    fun showInEditor(editor : Editor) {
        popup.showInBestPositionFor(editor)
    }

    fun showCentered() {
        popup.showInFocusCenter()
    }
}
