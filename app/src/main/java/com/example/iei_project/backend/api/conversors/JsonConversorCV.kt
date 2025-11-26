package com.example.iei_project.backend.api.conversors

import android.location.Address
import android.location.Geocoder
import android.util.Log

import com.example.iei_project.backend.api.dtos.TipoEstacion
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

class JsonConversorCV() {

    fun parseList(jsonArray: JSONArray, geocoder: Geocoder): JSONArray {
        val list = JSONArray()

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            list.put(parse(obj, geocoder))
        }

        return list
    }
    private fun parse(json: JSONObject, geocoder: Geocoder): JSONObject {

        val jsonRet = JSONObject()
        val tipoEnum = when(json.getString("TIPO ESTACIÓN").lowercase()) {
            "estación fija" -> TipoEstacion.EstacionFija
            "estación móvil" -> TipoEstacion.EstacionMovil
            else -> TipoEstacion.Otro
        }

        val provincia = JSONObject().apply {
            put("nombre",json.getString("PROVINCIA"))
        }
        val localidad = JSONObject().apply {
            put("nombre",json.getString("MUNICIPIO"))
            put("provincia",provincia)
        }

        val nombre = "CV-${json.getString("Nº ESTACIÓN")}"

        val direccion = json.getString("DIRECCIÓN")

        var coords = getCoords(direccion, geocoder)
        if(coords==null) {
            coords = Address(Locale.getDefault())
            coords.longitude = 0.0
            coords.latitude = 0.0
            Log.e("CARGA", "Las coordenadas de la dirección: $direccion no se han encontrado. Instanciandolas a (0,0)")
        }

        val latitud = coords.latitude

        val longitud = coords.longitude

        val horario = json.getString("HORARIOS")

        val contacto = json.getString("CORREO")

        val url = json.getString("CORREO")

        jsonRet.apply {
            put("nombre",nombre)
            put("tipo",tipoEnum)
            put("direccion",direccion)
            put("codigo_postal",json.getString("C.POSTAL"))
            put("latitud",latitud)
            put("longitud",longitud)
            put("descripcion","")
            put("horario",horario)
            put("contacto",contacto)
            put("url",url)
        }

        return jsonRet
    }

    fun getCoords(direccion: String, geocoder: Geocoder): Address? {
        val coords = geocoder.getFromLocationName(direccion, 1)
        return coords?.firstOrNull()
    }


}