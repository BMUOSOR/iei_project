package com.example.iei_project.backend.api.conversors

import android.util.Log
import com.example.iei_project.backend.api.data.Estacion
import com.example.iei_project.backend.api.data.Localidad
import com.example.iei_project.backend.api.data.Provincia
import com.example.iei_project.backend.api.dtos.TipoEstacion
import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import org.json.JSONArray
import org.json.JSONObject
import java.io.Reader
import java.nio.charset.StandardCharsets

class CsvConversorGAL() {

    fun parseList(reader: Reader): JSONArray {


        val csv = CSVReaderBuilder(reader)
            .withCSVParser(CSVParserBuilder().withSeparator(';').build())
            .build()

        val rows = csv.readAll().drop(1)
        val result = JSONArray()

        for (row in rows) {
            val nombre = row[0]
            val direccion = row[1]
            val concello = row[2]
            val cp = row[3]
            val provinciaRaw = row[4]
            val bytesProvincia = provinciaRaw.toByteArray(StandardCharsets.UTF_8)
            val provincia = String(bytesProvincia, StandardCharsets.UTF_8)
            val horario = row[6]
            val url = row[7]
            val correo = row[8]
            val coords = row[9]

            val (lat, lon) = gmapsToDecimal(coords)
            val estacion = Estacion(
                nombre = "GAL$nombre",
                tipo = TipoEstacion.EstacionFija,
                direccion = direccion,
                codigo_postal = cp,
                latitud = lat,
                longitud = lon,
                horario = horario,
                contacto = correo,
                url = url,
                localidad = Localidad(
                    nombre = concello,
                    provincia = Provincia(
                        nombre = provincia
                    )
                )
            )
            val json = JSONObject()
            json.put("nombre",estacion.nombre)
            json.put("tipo",estacion.tipo)
            json.put("direccion", estacion.direccion)
            json.put("codigo_postal",estacion.codigo_postal)
            json.put("latitud",estacion.latitud)
            json.put("longitud",estacion.longitud)
            json.put("descripcion",estacion.descripcion)
            json.put("horario",estacion.horario)
            json.put("contacto",estacion.contacto)
            json.put("url",estacion.url)
            val jsonProvincia = JSONObject()
            jsonProvincia.put("nombre", provincia)
            val jsonLocalidad = JSONObject()
            jsonLocalidad.apply {
                put("nombre",concello)
                put("provincia",jsonProvincia)
            }
            json.put("localidad",estacion.localidad)
            json.put("localidad",jsonLocalidad)
            result.put(json)
        }

        return result
    }
    fun gmapsToDecimal(raw: String): Pair<Double, Double> {

        fun parseCoordinate(coord: String): Double {
            // Reemplazar cualquier carácter raro de grado por °
            val cleaned = coord.replace("�".toRegex(), "°").trim()
            val isNegative = cleaned.startsWith("-")

            val deg = cleaned.substringBefore("°").replace("-", "").trim().toDouble()
            val min = cleaned.substringAfter("°").substringBefore("'").trim().toDouble()

            val decimal = deg + (min / 60.0)
            return if (isNegative) -decimal else decimal
        }

        val parts = raw.split(",")
        if (parts.size != 2) throw IllegalArgumentException("Entrada debe tener latitud y longitud separadas por coma")

        val lat = parseCoordinate(parts[0])
        val lon = parseCoordinate(parts[1])

        return Pair(lat, lon)
    }
}
