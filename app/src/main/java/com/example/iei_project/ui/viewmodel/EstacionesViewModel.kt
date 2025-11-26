package com.example.iei_project.ui.viewmodel

import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iei_project.backend.api.conversors.CsvConversorGAL
import com.example.iei_project.backend.api.conversors.JsonConversorCV
import com.example.iei_project.backend.api.conversors.XmlConversorCAT
import com.example.iei_project.backend.api.data.Estacion
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
    private val conversorCAT = XmlConversorCAT()
    private val conversorGAL = CsvConversorGAL()

    private val conversorCV = JsonConversorCV()

    private val extProvincia = ExtractorProvincia()
    private val extLocalidad = ExtractorLocalidad(extProvincia)
    val extractorEstacion = ExtractorEstacion(extLocalidad)

    fun cargar(fuenteCV: JSONArray, fuenteCAT: InputStream, fuenteGAL: Reader, geocoder: Geocoder) {

        viewModelScope.launch {

            val arrGAL = conversorGAL.parseList(fuenteGAL)
            Log.d("CARGAR", "Galicia: ${arrGAL}")
            val arrCAT = conversorCAT.parseList(fuenteCAT)
            Log.d("CARGAR", "Catalunya: ${arrCAT}")
            Log.d("CARGAR", "${fuenteCV}")
            val arrCV = conversorCV.parseList(fuenteCV, geocoder)
            Log.d("CARGAR", "Valencia: $arrCV")
            //postearArray(arrGAL)
            postearArray(arrCAT)
            postearArray(arrCV)

        }
    }


    suspend fun postearArray(arrayPost: JSONArray) {
        for (i in 0..<arrayPost.length()) {
            Log.d("postearArray", "Procesando elemento $i del array...")
            try {
                val estacionPost = arrayPost.getJSONObject(i)
                val extEstacion = ExtractorEstacion(extLocalidad)
                val estacion = extEstacion.extractEstacion(estacionPost)

                val existente = supabase.from("estacion")
                    .select(columns = Columns.list("cod_estacion")) {
                        filter { eq("nombre", estacion.nombre) }
                    }.decodeAs<List<Map<String, Estacion>>>()
                if (existente.isNotEmpty()) {
                    Log.d("postearArray", "La estación '${estacion.nombre}' ya existe. Saltando inserción.")

                } else {
                    Log.d(
                        "Subida Array",
                        "Posteando elemento ${arrayPost.getJSONObject(i)} a la base de datos..."
                    )
                    val estacionParaInsertar: Estacion = extEstacion.extractEstacion(estacionPost)


                    supabase.from("estacion").insert(estacionParaInsertar) {
                        select()
                    }

                    Log.d("postearArray", "Estación '${estacion.nombre}' insertada correctamente.")

                }
            } catch (e: Exception) {
                Log.e("postearArray", "Error al procesar el elemento $i del array: ${e.message}", e)
            }
        }
    }


}