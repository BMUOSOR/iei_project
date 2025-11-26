package com.example.iei_project.backend.api.data

import kotlinx.serialization.Serializable

@Serializable
data class Localidad (
    val nombre: String,
    val provincia: Provincia
)