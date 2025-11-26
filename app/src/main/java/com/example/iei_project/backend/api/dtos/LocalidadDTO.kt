package com.example.iei_project.backend.api.dtos

import kotlinx.serialization.Serializable

@Serializable
class LocalidadDTO(
    val codigo: Long?,
    val nombre: String,
    val provincia: Long
)