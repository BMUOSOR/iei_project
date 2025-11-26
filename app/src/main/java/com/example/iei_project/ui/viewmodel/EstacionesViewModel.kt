package com.example.iei_project.ui.viewmodel

import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iei_project.backend.api.conversors.CsvConversorGAL
import com.example.iei_project.backend.api.conversors.JsonConversorCV
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
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.request.InsertRequestBuilder
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import org.json.JSONArray
import org.json.JSONObject
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeDriverService
import java.io.InputStream
import java.io.Reader

class EstacionesViewModel(
) : ViewModel() {


    var supabase = createSupabaseClient(
        "https://drwmjxlwphrvqyqwythj.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImRyd21qeGx3cGhydnF5cXd5dGhqIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc2Mjk0Nzg5MywiZXhwIjoyMDc4NTIzODkzfQ.ZBeBqrM_KwDguMblVGQDeGIi_rsboDN6sxudrtyVug4"
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


    fun cargar(fuenteCV: JSONArray, fuenteCAT: InputStream, fuenteGAL: Reader, geocoder: Geocoder) {
        viewModelScope.launch {
            val extractorEstacion = ExtractorEstacion(extLocalidad,geocoder)
            val arrGAL = galExtractor.parse(fuenteGAL)
            Log.d("CARGAR", "Galicia: ${arrGAL}")
            val arrCAT = catExtractor.parseList(fuenteCAT)
            Log.d("CARGAR", "Catalunya: ${arrCAT}")
            val arrCV = extractorEstacion.extractLista(fuenteCV)
            Log.d("CARGAR", "Valencia: ${arrCV.toString()}")

        }
    }


    suspend fun postearArray(arrayPost: JSONArray) {
        for (i in 0..<arrayPost.length()) {
            Log.d(
                "Subida Array",
                "Posteando elemento ${arrayPost.getJSONObject(i)} a la base de datos..."
            )
            /*
            val estacionPost = arrayPost.getJSONObject(i)
            val estacion = extEstacion.extractEstacion(estacionPost)
            val provincia = extProvincia.extractProvincia(estacionPost.getJSONObject("localidad").getJSONObject("provincia"))

            //PRUEBA DE FILTRAR POR FK
/*
            val columns = Columns.raw("""
                localidad (
                    provincia (
                        codigo,
                        nombre
                    )
                )
            """.trimIndent())
            val provinciaExt = supabase.from("estacion").select(columns) {
                filter{
                    eq("localidad.provincia.nombre",estacion.localidad.provincia.nombre)
                }
            }.decodeSingle<Provincia>()
            */
            Log.d("SubidaArray","$provincia")
            val response = supabase
                .from("provincia")
                .update(provincia)

            Log.d("SubidaArray", "Insert result: $response")


             */
        }
    }


}