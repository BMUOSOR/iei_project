package com.example.iei_project.backend.api.extractors

import com.example.iei_project.backend.api.data.Provincia
import org.json.JSONObject

class ExtractorProvincia {
    fun extractProvincia(json: JSONObject) : Provincia {
        return Provincia(nombre = json.getString("nombre"))
    }
}