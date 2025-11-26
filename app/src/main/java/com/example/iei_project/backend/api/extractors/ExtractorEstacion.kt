package com.example.iei_project.backend.api.extractors

import android.location.Address
import android.location.Geocoder
import android.util.Log
import com.example.iei_project.backend.api.data.Estacion
import com.example.iei_project.backend.api.data.Localidad
import com.example.iei_project.backend.api.data.Provincia
import com.example.iei_project.backend.api.dtos.EstacionDTO
import com.example.iei_project.backend.api.dtos.TipoEstacion
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

class ExtractorEstacion(
    private val extractorLocalidad : ExtractorLocalidad
) {

    fun extractEstacion(json : JSONObject) : Estacion {

        return Estacion(
            nombre = json.getString("nombre"),
            tipo = TipoEstacion.EstacionFija,
            direccion = json.getString("direccion"),
            codigo_postal = json.getString("codigo_postal"),
            latitud = json.getDouble("latitud"),
            longitud = json.getDouble("longitud"),
            descripcion = json.getString("descripcion"),
            horario = json.getString("horario"),
            contacto = json.getString("correo"),
            url = json.getString("url"),
            localidad = extractorLocalidad.extractLocalidad(json.getJSONObject("localidad"))

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




}