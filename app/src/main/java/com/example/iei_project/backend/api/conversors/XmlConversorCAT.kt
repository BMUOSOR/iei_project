package com.example.iei_project.backend.api.conversors

import android.util.Xml
import org.json.JSONArray
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
class XmlConversorCAT {
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

        return JSONObject().apply {
            put("nombre", "CAT-$estaci")
            put("tipo", "EstacionFija") // o tu enum según convenga
            put("direccion", direccion)
            put("codigo_postal", cp)
            put("latitud", lat.toDouble() / 1_000_000)
            put("longitud", lon.toDouble() / 1_000_000)
            put("descripcion", "")
            put("horario", horario)
            put("contacto", contacto)
            put("url", web)
            put("localidad", JSONObject().apply {
                put("nombre", municipio)
                put("provnicia", JSONObject().apply {
                    put("nombre", "Catalunya")
                })
            })
        }
    }

}
