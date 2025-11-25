package com.example.iei_project.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iei_project.backend.api.conversors.CsvConversorGAL
import com.example.iei_project.backend.api.conversors.XmlConversorCAT
import com.example.iei_project.backend.api.extractors.ExtractorLocalidad
import com.example.iei_project.backend.api.extractors.ExtractorProvincia
import com.example.iei_project.backend.api.repository.EstacionRepository
import com.example.iei_project.backend.api.service.SupabaseService
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import java.io.Reader

class EstacionesViewModel(
    private val repo: EstacionRepository = EstacionRepository(),
) : ViewModel() {

    private val catExtractor = XmlConversorCAT()
    private val galExtractor = CsvConversorGAL()

    private val extProvincia = ExtractorProvincia()
    private val extLocalidad = ExtractorLocalidad(extProvincia)

    fun cargar(fuenteCV : JSONArray, fuenteCAT : InputStream, fuenteGAL : Reader ) {
        viewModelScope.launch {
            val arrCV = fuenteCV
            val arrCAT = catExtractor.parseList(fuenteCAT)
            val arrGAL =  galExtractor.parse(fuenteGAL)
            Log.d("Subida Array", "Subiendo array...")
            postearArray(arrCAT)

        }
    }

    suspend fun postearArray(arrayPost : JSONArray) {
        for(i in 0 ..< arrayPost.length()) {
            Log.d("Subida Array", "Posteando elemento ${arrayPost.getJSONObject(i)} a la base de datos...")
            val estacionPost = arrayPost.getJSONObject(i)
            val localidadJSON = estacionPost.getJSONObject("localidad")
            val provinciaJSON = localidadJSON.getJSONObject("provincia")
            val provincia = extProvincia.extractProvincia(provinciaJSON)
            val localidad = extLocalidad.extractLocalidad(localidadJSON)
            val localidadDTO = repo.subirLocalidad(localidadJSON)
            val provinciaDTO = repo.subirProvincia(provincia)
            estacionPost.remove("localidad")
            estacionPost.put("localidad",localidadDTO.get(0).codigo)
            Log.d("Subida Array", "El objeto json estación se ha quedado como: $estacionPost")
            repo.subirEstacion(estacionPost)
        }
    }
}