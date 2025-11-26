package com.example.iei_project

import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.iei_project.backend.api.conversors.JsonConversorCV
import com.example.iei_project.ui.theme.Iei_projectTheme
import com.example.iei_project.ui.viewmodel.EstacionesViewModel
import org.json.JSONArray
import java.io.File
import java.io.InputStream

class MainActivity : ComponentActivity() {

    val geocoder = Geocoder(this)
    private val viewModel : EstacionesViewModel by viewModels()


    fun Context.readAsset(name: String): String =
        assets.open(name).bufferedReader().use { it.readText() }

    fun Context.readAssetStream(name: String): InputStream =
        assets.open(name)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("MainActivity", "Cargando los datos...")
        viewModel.cargar(JSONArray(readAsset("estaciones.json")),readAssetStream("ITV-CAT.xml"),readAssetStream("Estacions_ITV.csv").bufferedReader(Charsets.ISO_8859_1),geocoder)



        // -------- Compose UI --------
        enableEdgeToEdge()
        setContent {
            Iei_projectTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting("Android", Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Iei_projectTheme {
        Greeting("Android")
    }
}