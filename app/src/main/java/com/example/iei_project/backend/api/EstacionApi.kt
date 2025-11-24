package com.example.iei_project.backend.api

import com.example.iei_project.backend.api.data.Estacion
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface EstacionApi {

    @POST("Estacion")
    suspend fun insertEstacion(
        @Body estacion: Estacion
    ): Response<List<Estacion>>
}