/**
 * Urheber dieser Datei ist Sebastian Badur. Die beiliegende Lizenz muss gewahrt bleiben.
 */

package com.github.sebadur.TouchScroll

import java.awt.AWTEvent
import java.awt.Point
import java.awt.event.AWTEventListener
import java.awt.event.MouseEvent
import javax.swing.SwingUtilities

import java.awt.event.MouseWheelEvent.MOUSE_PRESSED
import java.awt.event.MouseWheelEvent.MOUSE_RELEASED
import java.awt.event.MouseWheelEvent.MOUSE_CLICKED
import java.awt.event.MouseWheelEvent.MOUSE_DRAGGED
import java.awt.event.MouseWheelEvent.BUTTON1_DOWN_MASK


/**
 * Dieses Objekt kontrolliert die Eingabe-Verwaltungsschleife (wenn angemeldet). Echte Benutzereingaben durch Berühren
 * des (eventuellen) TouchScreens werden für javax.swing in Mausklicks interpretiert. Hier müssen diese Eingaben neu
 * interpretiert und nötigenfalls abgefangen werden.
 */
object Abfertiger: AWTEventListener {

    /**
     * Die Maske für Eingabeereignisse von Relevanz: Mausklicks und Bewegungen (bei gedrückter Maustaste).
     */
    final val maske = AWTEvent.MOUSE_EVENT_MASK or AWTEvent.MOUSE_MOTION_EVENT_MASK

    /**
     * Reflektion auf das Attribut der Klickzahl eines "Klicks". Für kurz aufeinander folgende Klicks wird nur ein
     * Ereignis an die Komponenten gesendet.
     */
    private val clickCount = Reflektion.attribut(MouseEvent::class.java, "clickCount")

    /**
     * JEDE der Maske entsprechende Eingabe passiert diese Funktion. Hier werden weiter die drei Ereignisse Drücken,
     * Loslassen und Ziehen herausgefiltert. Insbesondere (echte) Mausbewegungen ohne Tastendruck sind hier unwichtig
     * UND müssen schnell verarbeitet werden.
     */
    override fun eventDispatched(awt: AWTEvent) {
        when (awt.id) {
            MOUSE_PRESSED -> drücken(awt as MouseEvent)
            MOUSE_RELEASED -> loslassen(awt as MouseEvent)
            MOUSE_DRAGGED -> ziehen(awt as MouseEvent)
        }
    }

    /**
     * Indiziert, ob das eingehende Ereignis echt ist, oder von diesem Objekt künstlich generiert, um es an die GBO
     * weiterzugeben.
     */
    private var echt = true

    /**
     * Überprüft, ob ein Ereignis abgefangen werden muss. Im Regelfall ist dann noch e.consume() aufzurufen.
     * @return Wahr genau dann, wenn das Ereignis abgefangen werden soll.
     */
    private fun abfangen(e: MouseEvent): Boolean {
        val istEcht = echt
        echt = true
        return Implementation.abfangen(e.component.javaClass) && SwingUtilities.isLeftMouseButton(e) && istEcht
    }

    /**
     * Sendet ein künstliches Ereignis, das im Unterschied zum angegebenen nur einen Klick besitzt.
     * @param e Ähnliches Ereignis.
     */
    fun senden(e: MouseEvent) {
        echt = false
        e.component.dispatchEvent(
                MouseEvent(e.component, MOUSE_PRESSED, e.`when`, BUTTON1_DOWN_MASK, e.x, e.y, 1, false)
        )
    }

    /**
     * Startpunkt einer Eingabe.
     */
    private var start = Point(0, 0)

    /**
     * Zuvor verarbeiteter Punkt einer Eingabe (Ziehen).
     */
    private var zuvor = start

    /**
     * Markiert, ob die Eingabe mittlerweile als Bildlauf erkannt wurde. Wird erst bei erneuter Berührung wieder
     * zurückgesetzt.
     */
    private var scrollen = false
    /**
     * Horizontale Komponente.
     * @see scrollen
     */
    private var xScrollen = false
    /**
     * Vertikale Komponente.
     * @see scrollen
     */
    private var yScrollen = false

    /**
     * Markiert, ob es sich bei der Eingabe um eine Textauswahl handelt. In diesem Fall darf kein Bildlauf durchgeführt
     * werden. Wird erst bei erneuter Berührung zurückgesetzt.
     */
    private var auswahl = false

    /**
     * Beim Berühren des Bildschirms (Initialisierung).
     * @param e Das Ereignis.
     */
    private fun drücken(e: MouseEvent) {
        if (abfangen(e)) {
            start = e.locationOnScreen
            zuvor = start
            scrollen = false
            xScrollen = false
            yScrollen = false
            auswahl = e.clickCount > 1
            /**
             * Die Anzahl der Klicks für eine Auswahl wird einfach um eins erhöht, um zusätzliche Funktionalitäten
             * beizubehalten.
             */
            if (auswahl) clickCount.set(e, e.clickCount - 1)
            else e.consume()
        }
    }

    /**
     * Beim Loslassen des Bildschirms. Spätestens jetzt ist klar, um welche Eingabe es sich gehandelt hat.
     * @param e Das Ereignis.
     */
    private fun loslassen(e: MouseEvent) {
        if (abfangen(e) && !auswahl) {
            if (scrollen) e.consume() // In diesem Fall nicht klicken
            else Implementation.klicken(e)
        }
    }

    /**
     * Beim Ziehen (mit Halten des Fingers).
     * @param e Das Ereignis.
     */
    private fun ziehen(e: MouseEvent) {
        if (abfangen(e) && !auswahl) {
            e.consume()

            /**
             * Der Wert, ab dem eine Abweichung vom Startpunkt der Berührung als erwünschter Bildlauf interpretiert
             * wird. Für die horizontale Position zusätzlich der Wert, ab dem der Bildlauf überhaupt erst stattfindet
             * (weil horizontales Scrollen seltener erwünscht ist und unter Umständen nervig sein kann).
             */
            yScrollen = yScrollen || Math.abs(start.y - e.yOnScreen) > 15
            xScrollen = xScrollen || Math.abs(start.x - e.xOnScreen) > 25
            scrollen = yScrollen || xScrollen

            val dx: Int
            if (xScrollen) dx = zuvor.x - e.xOnScreen
            else dx = 0

            Implementation.bildlauf(e.component, dx, zuvor.y - e.yOnScreen)
            zuvor = e.locationOnScreen
        }
    }

}
