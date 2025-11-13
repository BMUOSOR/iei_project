package com.example.iei_project.backend.api.dtos

class EstacionDTO (
    val cod_Estacion : Long,
    val nombre : String,
    val tipo : TipoEstacion,
    val direccion: String,
    val codigo_postal: Short,
    val longitud : Double,
    val latitud : Double,
    val descripcion : String? = "",
    val horario : String,
    val contacto : String,
    val url : String
)
