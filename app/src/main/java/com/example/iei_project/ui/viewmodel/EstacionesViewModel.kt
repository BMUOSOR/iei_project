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
import com.example.iei_project.backend.api.data.Localidad
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
            postearArray(arrGAL)
            postearArray(arrCAT)
            postearArray(arrCV)

        }
    }


    private suspend fun postearArray(arrayPost: JSONArray) {
        Log.d("postearArray", "--- Iniciando subida para  (${arrayPost.length()} elementos) ---")
        for (i in 0..<arrayPost.length()) {
            try {
                val estacionJson = arrayPost.getJSONObject(i)
                val estacion: Estacion = extractorEstacion.extractEstacion(estacionJson)

                val existente = supabase.from("estacion")
                    .select(Columns.list("nombre")) {
                        filter {
                            eq("nombre", estacion.nombre)
                        }
                    }.decodeSingleOrNull<Estacion>()

                if (existente != null) {
                    Log.d("postearArray", "La estación '${estacion.nombre}' ya existe. Saltando inserción.")
                    continue
                }

                Log.d("provincia de estacion", "${estacion.localidad.provincia.nombre}")
                val provinciaId = getOrCreateProvincia(estacion.localidad.provincia)


                val localidadId = getOrCreateLocalidad(estacion.localidad, provinciaId)


                val estacionDTO = EstacionDTO(estacion.cod_estacion,
                    estacion.nombre,
                    estacion.tipo,
                    estacion.direccion,
                    estacion.codigo_postal,
                    estacion.latitud,
                    estacion.longitud,
                    estacion.descripcion,
                    estacion.horario,
                    estacion.contacto,
                    estacion.url,
                    localidadId
                )


                supabase.from("estacion").insert(estacionDTO)
                Log.i("postearArray", "Estación '${estacion.nombre}' insertada correctamente.")

            } catch (e: Exception) {
                Log.e("postearArray", "Error al procesar el elemento $i: ${e.message}", e)
            }
        }
        Log.d("postearArray", "--- Subida finalizada ---")
    }

    /**
     * Busca una provincia por nombre. Si no existe, la crea. Devuelve su ID.
     */
    private suspend fun getOrCreateProvincia(provincia: Provincia): Long {
        val resultado = supabase.from("provincia").select {
            filter {
                eq("nombre", provincia.nombre)
            }
        }.decodeSingleOrNull<Provincia>()

        return if (resultado?.codigo != null) {
            resultado.codigo
        } else {
            Log.d("Relaciones", "Creando nueva provincia: ${provincia.nombre}")

            val nuevaProvincia = supabase.from("provincia").insert(provincia) {
                select()
            }.decodeSingle<Provincia>()
            nuevaProvincia.codigo!!
        }
    }

    /**
     * Busca una localidad por nombre y provincia_id. Si no existe, la crea. Devuelve su ID.
     */
    private suspend fun getOrCreateLocalidad(localidad: Localidad, provinciaId: Long): Long {
        val resultado = supabase.from("localidad").select {
            filter {
                eq("nombre", localidad.nombre)
            }
            filter {
                eq("provincia", provinciaId)
            }
        }.decodeSingleOrNull<Localidad>()

        return if (resultado?.codigo != null) {
            resultado.codigo
        } else {
            Log.d("Relaciones", "Creando nueva localidad: ${localidad.nombre} en provincia ID: $provinciaId")
            val localidadDTO = LocalidadDTO(localidad.codigo, localidad.nombre, provinciaId)
            Log.d("Relaciones", "Localidad DTO: ${localidadDTO.nombre} - ${localidadDTO.provincia}")
            val nuevaLocalidad = supabase.from("localidad").insert(localidadDTO) {
                select()
            }.decodeSingle<Localidad>()
            nuevaLocalidad.codigo!!
        }
    }


}