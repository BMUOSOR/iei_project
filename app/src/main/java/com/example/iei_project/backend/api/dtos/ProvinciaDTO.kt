package com.example.iei_project.backend.api.dtos

import kotlinx.serialization.Serializable

@Serializable
class ProvinciaDTO(
    val codigo: Long? = null,
    val nombre : String
) {
}