package com.example.iei_project.backend.api.conversors

import android.location.Geocoder
import com.example.iei_project.backend.api.data.Estacion
import com.example.iei_project.backend.api.data.Localidad
import com.example.iei_project.backend.api.data.Provincia
import com.example.iei_project.backend.api.dtos.TipoEstacion
import org.json.JSONArray
import org.json.JSONObject
class JsonConversorCV(private val geocoder: Geocoder) {

    fun wrapList(jsonArray: JSONArray): JSONArray {
        val list = JSONArray()

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            list.put(parse(obj))
        }

        return list
    }
    private fun parse(json: JSONObject): JSONObject {
        val json = JSONObject()
        val tipoEnum = when(json.getString("TIPO ESTACIÓN").lowercase()) {
            "estación fija" -> TipoEstacion.EstacionFija
            "estación móvil" -> TipoEstacion.EstacionMovil
            else -> TipoEstacion.Otro
        }

        val direccion = json.getString("DIRECCIÓN")
        val coords = geocoder.getFromLocationName(direccion, 1)?.firstOrNull()
        val estacion = Estacion(
            nombre = "CV-${json.getInt("Nº ESTACIÓN")}",
            tipo = tipoEnum,
            direccion = direccion,
            codigo_postal = json.getString("C.POSTAL"),
            latitud = coords?.latitude ?: 0.0,
            longitud = coords?.longitude ?: 0.0,
            horario = json.getString("HORARIOS"),
            contacto = json.getString("CORREO"),
            url = "https://itv.gva.es",
            localidad = Localidad(
                nombre = json.getString("MUNICIPIO"),
                provincia = Provincia(
                    nombre = json.getString("PROVINCIA")
                )
            )
        )

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
        json.put("localidad",estacion.localidad)

        return json
    }
}