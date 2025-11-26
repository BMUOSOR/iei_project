package com.example.iei_project.ui.viewmodel

import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iei_project.backend.api.conversors.CsvConversorGAL
import com.example.iei_project.backend.api.conversors.XmlConversorCAT
import com.example.iei_project.backend.api.data.Provincia
import com.example.iei_project.backend.api.dtos.EstacionDTO
import com.example.iei_project.backend.api.dtos.LocalidadDTO
import com.example.iei_project.backend.api.dtos.ProvinciaDTO
import com.example.iei_project.backend.api.extractors.ExtractorEstacion
import com.example.iei_project.backend.api.extractors.ExtractorLocalidad
import com.example.iei_project.backend.api.extractors.ExtractorProvincia
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.request.InsertRequestBuilder
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import java.io.Reader

class EstacionesViewModel() : ViewModel() {

    var supabase = createSupabaseClient(
        "https://drwmjxlwphrvqyqwythj.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImRyd21qeGx3cGhydnF5cXd5dGhqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjI5NDc4OTMsImV4cCI6MjA3ODUyMzg5M30.ZCo2RVpGZQ3lFG1QodsF2TlnieMDaZQ90_ex-20kQvg"
    ) {
        defaultSerializer = KotlinXSerializer(Json {

        })
        install(Postgrest)
        install(Auth)
    };
    private val catExtractor = XmlConversorCAT()
    private val galExtractor = CsvConversorGAL()

    private val extProvincia = ExtractorProvincia()
    private val extLocalidad = ExtractorLocalidad(extProvincia)
    private val extEstacion = ExtractorEstacion(extLocalidad)

    fun cargar(fuenteCV: JSONArray, fuenteCAT: InputStream, fuenteGAL: Reader) {
        viewModelScope.launch {
            val arrCV = fuenteCV
            val arrCAT = catExtractor.parseList(fuenteCAT)
            //val arrGAL =  galExtractor.parse(fuenteGAL)
            Log.d("Subida Array", "Subiendo array...")
            postearArray(arrCAT)

        }
    }

    suspend fun postearArray(arrayPost: JSONArray) {
        for (i in 0..<arrayPost.length()) {
            Log.d(
                "Subida Array",
                "Posteando elemento ${arrayPost.getJSONObject(i)} a la base de datos..."
            )
            val estacionPost = arrayPost.getJSONObject(i)
            //val estacion = extEstacion.extractEstacion(estacionPost)
            //val provincia = extProvincia.extractProvincia(estacionPost.getJSONObject("localidad").getJSONObject("provincia"))
            //Log.d("SubidaArray","$provincia")
            val test = ProvinciaDTO(45,nombre="provinciaManuela")
            val response = supabase
                .from("provincia")
                .insert(test)

            Log.d("SubidaArray", "Insert result: $response")

        }
    }
}