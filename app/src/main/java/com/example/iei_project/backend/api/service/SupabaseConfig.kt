package com.example.iei_project.backend.api.service

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

class SupabaseConfig(

) {

    fun supabaseClient(): SupabaseClient {
        val BASE_URL = "https://drwmjxlwphrvqyqwythj.supabase.co/rest/v1/"
        val API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImRyd21qeGx3cGhydnF5cXd5dGhqIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc2Mjk0Nzg5MywiZXhwIjoyMDc4NTIzODkzfQ.ZBeBqrM_KwDguMblVGQDeGIi_rsboDN6sxudrtyVug4"
        return createSupabaseClient(
            supabaseUrl = BASE_URL,
            supabaseKey = API_KEY
        ) {
            // Lambda builder opcional, puedes configurar servicios extra si quieres
            install(Postgrest)
            install(Auth)

        }
    }


}

