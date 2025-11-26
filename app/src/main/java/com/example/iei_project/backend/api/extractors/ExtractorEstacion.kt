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
    private val extractorLocalidad : ExtractorLocalidad,
    private val geocoder: Geocoder
) {

    fun extractEstacion(json : JSONObject) : Estacion {

        val tipoEnum = when(json.getString("TIPO ESTACIÓN").lowercase()) {
            "estación fija" -> TipoEstacion.EstacionFija
            "estación móvil" -> TipoEstacion.EstacionMovil
            else -> TipoEstacion.Otro
        }

        val provincia = Provincia(nombre = json.getString("PROVINCIA"))
        val localidad = Localidad(nombre = json.getString("MUNICIPIO"), provincia = provincia)

        val direccion = json.getString("DIRECCIÓN")
        var coords = extractCoords(direccion)
        if(coords==null) {
            coords = Address(Locale.getDefault())
            coords.longitude = 0.0
            coords.latitude = 0.0
            Log.e("CARGA", "Las coordenadas de la dirección: $direccion no se han encontrado. Instanciandolas a (0,0)")
        }
        return Estacion(
            nombre = "CV-${json.getString("Nº ESTACIÓN")}",
            tipo = tipoEnum,
            direccion = direccion,
            codigo_postal = json.getString("C.POSTAL"),
            latitud = coords!!.latitude,
            longitud = coords.longitude,
            horario = json.getString("HORARIOS"),
            contacto = json.getString("CORREO"),
            url = json.getString("CORREO"),
            localidad = localidad

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

    fun extractCoords(direccion: String): Address? {
        val coords = geocoder.getFromLocationName(direccion, 1)
        return coords?.firstOrNull()
    }


}