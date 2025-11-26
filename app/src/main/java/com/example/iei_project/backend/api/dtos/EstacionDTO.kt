package com.example.iei_project.backend.api.dtos

import kotlinx.serialization.Serializable

@Serializable
class EstacionDTO (
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
    val localidad: Long? = null
)
