/**
 * Urheber dieser Datei ist Sebastian Badur. Die beiliegende Lizenz muss gewahrt bleiben.
 */

package com.github.sebadur.TouchScroll

import java.awt.Component
import java.awt.event.MouseEvent
import javax.swing.JTree

import com.intellij.ui.components.JBScrollPane
import java.awt.event.KeyEvent


/**
 * Programmspezifische Implementation der Ausführung von Bildläufen.
 */
object Implementation {

    /**
     * Ein JButton kann keinen Bildlauf ausführen. Statt dessen kann durch Ausschluss dieser Klasse ein flüssigeres
     * Bedienerlebnis resultieren.
     */
    val JButton = javax.swing.JButton::class.java

    /**
     * Zuvor benutzte Komponente. Kann null sein.
     */
    var kZuvor: Component? = null

    /**
     * Das zur zuvor benutzten Komponente gehörige JB-Scroll-Objekt. Nur sinnvoll, wenn kZuvor != null.
     */
    var pZuvor = JBScrollPane()

    /**
     * Überprüft einen Druck auf Relevanz.
     * @param kl Klasse der Komponente, welche von der Eingabe betroffen ist.
     * @return Wahr, wenn die Eingabe abgefangen werden sollte.
     */
    fun abfangen(kl: Class<Component>): Boolean {
        return !(kl == JButton || kl.simpleName.equals("MyScrollBar"))
    }

    /**
     * Führt einen pixelgenauen Bildlauf auf der gegebenen Komponente aus.
     * @param k Die betroffene Komponente.
     * @param dx Versatz in Pixeln entlang der Horizontalen.
     * @param dy Versatz in Pixeln entlang der Vertikalen.
     */
    fun bildlauf(k: Component, dx: Int, dy: Int) {
        if (k != kZuvor) {
            var pane = k
            while (pane !is JBScrollPane) {
                if (pane.parent == null) return
                pane = pane.parent
            }
            // Beschleunigt die Ausführung beim Scrollen, wenn die Komponente nicht gewechselt wird (Normalfall)
            kZuvor = k
            pZuvor = pane
        }
        pZuvor.horizontalScrollBar.value += dx
        pZuvor.verticalScrollBar.value += dy
    }

    /**
     * Führt, wenn notwendig, eine programmspezifische Aktion in Abhängigkeit von der Komponente aus.
     * @param e Das beim Entfernen des Fingers gesendete Ereignis (mit der betroffenen Komponente).
     */
    fun klicken(e: MouseEvent) {
        // Reiter in der Projektansicht ausklappen und Dateien sofort anzeigen:
        if (e.component is JTree) {
            Abfertiger.senden(e)
            // Ein kleiner Trick, um die Auswahl unbedingt zu aktivieren, ohne zu klassenspezifisch zu werden:
            e.component.dispatchEvent(
                    KeyEvent(e.component, KeyEvent.KEY_PRESSED, e.`when`, 0, KeyEvent.VK_ENTER, '\n', KeyEvent.KEY_LOCATION_STANDARD)
                    //KeyEvent(e.component, MOUSE_PRESSED, e.`when`, BUTTON1_DOWN_MASK, e.x, e.y, klickzahl, false)
            )
        } else Abfertiger.senden(e) // Normaler Klick
    }

}
