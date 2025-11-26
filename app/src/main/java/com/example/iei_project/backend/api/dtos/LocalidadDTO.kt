package com.example.iei_project.backend.api.dtos

import com.example.iei_project.backend.api.data.Provincia
import kotlinx.serialization.Serializable

@Serializable
class LocalidadDTO(
    val codigo: Long? = null,
    val nombre: String,
    val provincia: Long? = null
)