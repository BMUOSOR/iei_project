package com.example.iei_project.backend.api.conversors

import android.util.Xml
import com.example.iei_project.backend.api.data.Localidad
import com.example.iei_project.backend.api.data.Provincia
import org.json.JSONArray
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
class XmlConversorCAT {
    private val locBarcelona = listOf(
        "Barcelona",
        "Badalona",
        "Cornellà de Llobregat",
        "Igualada",
        "Sant Fruitós de Bages",
        "Puigmadrona (Barcelona)",   // Estación B10, municipi = "Barcelona"
        "Olèrdola",
        "Santa Perpètua de Mogoda",
        "Granollers",
        "Viladecans",
        "Sabadell",
        "Sant Cugat del Vallès",
        "Sant Andreu de la Barca",
        "Motors (Barcelona)"        // Estación B15, municipi = "Barcelona"
    )

    private val locLleida = listOf(
        "Lleida",
        "Borges Blanques, les",
        "Vielha",
        "Pont de Suert, el",
        "Puigcerdà",
        "Artesa de Segre"
    )

    private val locTarragona = listOf(
        "Bellvei",
        "Montblanc"
    )
    private val locGirona = listOf(
        "Figueres",
        "Celrà",
        "Ripoll"
    )


    fun parseList(input: InputStream): JSONArray {
        val parser = Xml.newPullParser()
        parser.setInput(input, null)

        val jsonArray = JSONArray()

        var event = parser.eventType
        while (event != XmlPullParser.END_DOCUMENT) {
            if (event == XmlPullParser.START_TAG && parser.name == "row") {
                jsonArray.put(parseSingleRow(parser))
            }
            event = parser.next()
        }

        return jsonArray
    }

    private fun parseSingleRow(parser: XmlPullParser): JSONObject {
        var estaci = ""
        var direccion = ""
        var cp = ""
        var municipio = ""
        var lat = ""
        var lon = ""
        var horario = ""
        var contacto = ""
        var web = ""

        var event = parser.eventType
        while (!(event == XmlPullParser.END_TAG && parser.name == "row")) {
            if (event == XmlPullParser.START_TAG) {
                when (parser.name) {
                    "estaci" -> estaci = parser.nextText()
                    "adre_a" -> direccion = parser.nextText()
                    "cp" -> cp = parser.nextText()
                    "municipi" -> municipio = parser.nextText()
                    "lat" -> lat = parser.nextText()
                    "long" -> lon = parser.nextText()
                    "horari_de_servei" -> horario = parser.nextText()
                    "correu_electr_nic" -> contacto = parser.nextText()
                    "web" -> web = parser.getAttributeValue(null, "url")
                }
            }
            event = parser.next()
        }

        val jsonLocalidad = JSONObject()
        val jsonProvincia = JSONObject()
        jsonProvincia.apply {
            put("nombre",asignarProvincia(municipio))
        }
        jsonLocalidad.apply {
            put("nombre",municipio)
            put("provincia",jsonProvincia)
        }
        return JSONObject().apply {
            put("nombre", "CAT$estaci")
            put("tipo", "EstacionFija") // o tu enum según convenga
            put("direccion", direccion)
            put("codigo_postal", cp)
            put("latitud", lat.toDouble() / 1_000_000)
            put("longitud", lon.toDouble() / 1_000_000)
            put("descripcion", "")
            put("horario", horario)
            put("contacto", contacto)
            put("url", web)
            put("localidad",jsonLocalidad)

        }
    }

    fun asignarProvincia(localidad : String) : String {
        if (locBarcelona.contains(localidad)) {
            return "Barcelona"
        }
        if(locGirona.contains(localidad)) {
            return "Girona"
        }
        if(locLleida.contains(localidad)) {
            return "Lleida"
        }
        if(locTarragona.contains(localidad))
            return "Tarragona"
        return "Barcelona"
    }
}
