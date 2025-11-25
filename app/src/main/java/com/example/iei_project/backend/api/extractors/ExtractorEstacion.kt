package com.example.iei_project.backend.api.extractors

import android.location.Geocoder
import android.util.Log
import com.example.iei_project.backend.api.data.Estacion
import com.example.iei_project.backend.api.data.Localidad
import com.example.iei_project.backend.api.dtos.TipoEstacion
import org.json.JSONArray
import org.json.JSONObject

class ExtractorEstacion(private val geocoder : Geocoder,
    private val extractorLocalidad : ExtractorLocalidad
) {
    /*
    fun extractEstacion(json : JSONObject) : Estacion {
        Log.d("TEST", json.toString())

        val tipoEnum = when(json.getString("tipo").lowercase()) {
            "estación fija" -> TipoEstacion.EstacionFija
            "estación móvil" -> TipoEstacion.EstacionMovil
            else -> TipoEstacion.Otro
        }

        val direccion = json.getString("direccion")
        val coords = geocoder.getFromLocationName(direccion, 1)?.firstOrNull()
        return Estacion(
            nombre = "CV-${json.getString("nombre")}",
            tipo = tipoEnum,
            direccion = direccion,
            codigo_postal = json.getString("codigo_postal"),
            latitud = json.getDouble("latitud"),
            longitud = json.getDouble("longitud"),
            horario = json.getString("horario"),
            contacto = json.getString("contacto"),
            url = json.getString("url"),

        )
    }

    fun extractLista(lista : JSONArray) : List<Estacion> {
        val listaRet = ArrayList<Estacion>()
        for(i in 0 until lista.length()) {
            val element = lista.getJSONObject(i);
            listaRet.add(extractEstacion(element))
        }
        return listaRet
    }

     */
}