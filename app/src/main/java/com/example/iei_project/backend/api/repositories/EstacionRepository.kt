package com.example.iei_project.backend.api.repositories

import com.example.iei_project.backend.api.data.Estacion
import com.example.iei_project.backend.api.service.SupabaseConfig
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EstacionRepository {

    private val supabaseConfig = SupabaseConfig()
    private val supabaseClient = supabaseConfig.supabaseClient()

    // ... (addEstacion and getEstaciones functions remain the same) ...
    suspend fun addEstacion(estacion: Estacion) {
        withContext(Dispatchers.IO) {
            try {
                supabaseClient.postgrest["estaciones"].insert(estacion)
                println("Estación insertada correctamente.")
            } catch (e: Exception) {
                println("Error al insertar la estación: ${e.message}")
            }
        }
    }

    suspend fun getEstaciones(): List<Estacion> {
        return withContext(Dispatchers.IO) {
            try {
                val result = supabaseClient.postgrest["estaciones"].select()
                result.decodeAs<List<Estacion>>()
            } catch (e: Exception) {
                println("Error al obtener las estaciones: ${e.message}")
                emptyList()
            }
        }
    }


    /**
     * Busca y obtiene una estación específica por su nombre.
     * @param nombre El nombre de la estación a buscar.
     * @return Un objeto Estacion si se encuentra, o null si no se encuentra o hay un error.
     */
    suspend fun getEstacionPorNombre(nombre: String): Estacion? {
        return withContext(Dispatchers.IO) {
            try {
                val result = supabaseClient.postgrest["estaciones"].select {
                    filter {
                        eq("nombre", nombre)
                    }
                }
                result.decodeSingleOrNull<Estacion>()
            } catch (e: Exception) {
                println("Error al obtener la estación por nombre '$nombre': ${e.message}")
                null // Return null in case of an error.
            }
        }
    }
}
