package com.example.iei_project.backend.api.repository

import com.example.iei_project.backend.api.EstacionApi
import com.example.iei_project.backend.api.data.Estacion
import com.example.iei_project.backend.api.service.SupabaseService

class EstacionRepository {

    private val api = SupabaseService.retrofit.create(EstacionApi::class.java)

    suspend fun subirEstacion(estacion: Estacion): Boolean {
        val response = api.insertEstacion(estacion)
        return response.isSuccessful
    }
}