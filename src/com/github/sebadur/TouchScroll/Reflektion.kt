/**
 * Urheber dieser Datei ist Sebastian Badur. Die beiliegende Lizenz muss gewahrt bleiben.
 */

package com.github.sebadur.TouchScroll

import java.lang.reflect.Field


/**
 * Java-Klassenreflektion.
 * Nur für die hartnäckigen Fälle...
 */
object Reflektion {

    /**
     * Stellt ein privates Attribut zur Verfügung.
     * @param klasse Das Klasse, in welchem das Attribut deklariert wurde.
     * @param name Name des Attributs.
     * @return Das beschreibbare Attribut, welches über ((klasse) instanz).name erreichbar wäre.
     */
    fun attribut(klasse: Class<*>, name: String): Field {
        val att = klasse.getDeclaredField(name)
        att.isAccessible = true
        return att
    }

}
