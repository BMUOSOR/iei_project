package com.example.iei_project.backend.api
import com.example.iei_project.backend.api.data.Estacion
import com.example.iei_project.backend.api.data.Localidad
import com.example.iei_project.backend.api.data.Provincia
import com.example.iei_project.backend.api.dtos.EstacionDTO
import com.example.iei_project.backend.api.dtos.LocalidadDTO
import com.example.iei_project.backend.api.dtos.ProvinciaDTO
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface RetrofitApi {

    // Insertar una estación en la tabla "Estacion"
    @Headers(
        "Content-Type: application/json",
        "Prefer: return=representation" // Supabase devuelve el objeto insertado
    )
    @POST("Estacion")
    suspend fun insertEstacion(
        @Body estacion: JSONObject
    ): List<EstacionDTO>

    @POST("Localidad")
    suspend fun insertLocalidad(
        @Body localidad : JSONObject
    ): List<LocalidadDTO>

    @POST("Provincia")
    suspend fun insertProvincia(
        @Body provincia: JSONObject
    ): List<ProvinciaDTO>

    @GET("Provincia")
    suspend fun getProvinciaById(
        @Query("select") select: String = "*",
        @Query("codigo") codigo: String      // aquí pasaremos "eq.{valor}"
    ): ProvinciaDTO

    @GET("Localidad")
    suspend fun getIdLocalidadByNombre(
        @Query("select") select : String = "*",
        @Query("nombre") nombre : String
    ) : LocalidadDTO
}