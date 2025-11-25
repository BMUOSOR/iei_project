package com.example.iei_project.backend.api.extractors

import com.example.iei_project.backend.api.data.Localidad
import org.json.JSONObject

class ExtractorLocalidad(
    private val extractorProvincia: ExtractorProvincia
) {
    fun extractLocalidad(json : JSONObject) : Localidad {
        return Localidad(json.getString("nombre"),extractorProvincia.extractProvincia(json.getJSONObject("provincia")))
    }
}