package com.example.iei_project.backend.api.repository

import android.util.Log
import com.example.iei_project.backend.api.RetrofitApi
import com.example.iei_project.backend.api.data.Estacion
import com.example.iei_project.backend.api.data.Localidad
import com.example.iei_project.backend.api.data.Provincia
import com.example.iei_project.backend.api.dtos.LocalidadDTO
import com.example.iei_project.backend.api.dtos.ProvinciaDTO
import com.example.iei_project.backend.api.service.SupabaseService
import org.json.JSONObject
import retrofit2.Response

class EstacionRepository {

    private val api = SupabaseService.retrofit.create(RetrofitApi::class.java)

    suspend fun subirEstacion(estacion: JSONObject){
        api.insertEstacion(estacion)
    }

    suspend fun subirLocalidad(localidad: JSONObject) : List<LocalidadDTO> {
        val response = api.insertLocalidad(localidad)
        return response
    }

    suspend fun subirProvincia(provincia: JSONObject) : List<ProvinciaDTO> {
        Log.d("Subida provincia", "subiendo provincia: $provincia")
        val response = api.insertProvincia(provincia)
        Log.d("Subida provincia", "${response}")
        return response
    }

    suspend fun getLocalidad(localidad : String) : JSONObject {
        val localidad =api.getIdLocalidadByNombre(nombre = "eq.$localidad")
        val jsonObject = JSONObject()
        jsonObject.apply {
            put("codigo",localidad.codigo)
            put("nombre",localidad.nombre)
            put("provincia", localidad.provincia)
        }
        return jsonObject
    }

    suspend fun getProvincia(localidad: Localidad) : JSONObject {
        val provincia = api.getProvinciaById(codigo = "eq.${localidad.provincia}")
        val jsonObject = JSONObject()
        jsonObject.apply {
            put("codigo", provincia.codigo)
            put("nombre", provincia.nombre)
        }
        return jsonObject
    }

}