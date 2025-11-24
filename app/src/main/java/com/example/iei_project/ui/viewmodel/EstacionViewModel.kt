package com.example.iei_project.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iei_project.backend.api.data.Estacion
import com.example.iei_project.backend.api.repository.EstacionRepository
import kotlinx.coroutines.launch
import java.io.InputStream

class EstacionesViewModel(
    private val repo: EstacionRepository
) : ViewModel() {

    fun subir(estacion: Estacion) {
        viewModelScope.launch {
            val ok = repo.subirEstacion(estacion)
            if (ok) {
                Log.d("SUPABASE", "Insertado correctamente")
            } else {
                Log.e("SUPABASE", "Error al insertar")
            }
        }
    }



}