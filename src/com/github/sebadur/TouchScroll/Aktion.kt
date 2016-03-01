/**
 * Urheber dieser Datei ist Sebastian Badur. Die beiliegende Lizenz muss gewahrt bleiben.
 */

package com.github.sebadur.TouchScroll

import java.awt.Toolkit

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.ide.ui.LafManager
import com.intellij.ide.ui.UISettings


/**
 * Registriert das Plugin als Aktion in IntelliJ, damit es über das Kontextmenü an- oder abgewählt werden kann.
 */
class Aktion : AnAction() {

    /**
     * Das offene Tor zur Kontrolle über die GBO-Verwaltung.
     */
    private val toolkit = Toolkit.getDefaultToolkit()

    /*private val opt = UISettings.getInstance()
    private val laf = LafManager.getInstance()*/

    /**
     * Steht für den Status dieses Plugins.
     */
    private var aktiv = false

    /**
     * Aufruf des Eintrags im Kontextmenü.
     */
    override fun actionPerformed(e: AnActionEvent) {
        if (aktiv) { // Deaktivieren:
            toolkit.removeAWTEventListener(Abfertiger)
            /*opt.OVERRIDE_NONIDEA_LAF_FONTS = false
            opt.FONT_SIZE = 14*/
            aktiv = false
        } else { // Aktivieren:
            toolkit.addAWTEventListener(Abfertiger, Abfertiger.maske)
            /*opt.OVERRIDE_NONIDEA_LAF_FONTS = true
            opt.FONT_SIZE = 15*/
            aktiv = true
        }
        // laf.updateUI()
        // // Editor.updateUI()
    }

}
