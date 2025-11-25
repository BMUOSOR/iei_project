package com.example.iei_project.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iei_project.backend.api.conversors.CsvConversorGAL
import com.example.iei_project.backend.api.conversors.XmlConversorCAT
import com.example.iei_project.backend.api.dtos.EstacionDTO
import com.example.iei_project.backend.api.dtos.LocalidadDTO
import com.example.iei_project.backend.api.dtos.ProvinciaDTO
import com.example.iei_project.backend.api.extractors.ExtractorLocalidad
import com.example.iei_project.backend.api.extractors.ExtractorProvincia
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import java.io.Reader

class EstacionesViewModel() : ViewModel() {

    var supabase = createSupabaseClient("https://drwmjxlwphrvqyqwythj.supabase.co/rest/v1/", supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImRyd21qeGx3cGhydnF5cXd5dGhqIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc2Mjk0Nzg5MywiZXhwIjoyMDc4NTIzODkzfQ.ZBeBqrM_KwDguMblVGQDeGIi_rsboDN6sxudrtyVug4") {
        defaultSerializer = KotlinXSerializer(Json {

        })
    };
    private val catExtractor = XmlConversorCAT()
    private val galExtractor = CsvConversorGAL()

    private val extProvincia = ExtractorProvincia()
    private val extLocalidad = ExtractorLocalidad(extProvincia)

    fun cargar(fuenteCV : JSONArray, fuenteCAT : InputStream, fuenteGAL : Reader ) {
        viewModelScope.launch {
            val arrCV = fuenteCV
            val arrCAT = catExtractor.parseList(fuenteCAT)
            val arrGAL =  galExtractor.parse(fuenteGAL)
            Log.d("Subida Array", "Subiendo array...")
            postearArray(arrCAT)

        }
    }

    suspend fun postearArray(arrayPost : JSONArray) {
        for(i in 0 ..< arrayPost.length()) {
            Log.d("Subida Array", "Posteando elemento ${arrayPost.getJSONObject(i)} a la base de datos...")
            val estacionPost = arrayPost.getJSONObject(i)
            val localidadJSON = estacionPost.getJSONObject("localidad")
            val provinciaJSON = localidadJSON.getJSONObject("provincia")
            val provinciaDTO = supabase.from("Provincia").insert(provinciaJSON).decodeSingle<ProvinciaDTO>()
            val localidadDTO = supabase.from("Localidad").insert(localidadJSON)
            supabase.from("Localidad").update( {
                set("provincia",provinciaDTO.codigo)
            })
            estacionPost.remove("localidad")
            supabase.from("Estacion").insert(estacionPost)
            supabase.from("Estacion").update( {
                set("localidad",localidadDTO.decodeSingle<LocalidadDTO>().codigo)
            })
        }
    }
}