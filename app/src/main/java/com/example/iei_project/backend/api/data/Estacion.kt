package com.example.iei_project.backend.api.data

import com.example.iei_project.backend.api.dtos.TipoEstacion
import kotlinx.serialization.Serializable

@Serializable
data class Estacion (
    val cod_estacion: Long? = null,
    val nombre: String,
    val tipo: TipoEstacion,
    val direccion: String,
    val codigo_postal: String,
    val latitud: Double,
    val longitud: Double,
    val descripcion: String = "",
    val horario: String,
    val contacto: String,
    val url: String,
    val localidad: Localidad
)