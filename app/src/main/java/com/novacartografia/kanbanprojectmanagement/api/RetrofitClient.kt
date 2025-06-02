package com.novacartografia.kanbanprojectmanagement.api

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.novacartografia.kanbanprojectmanagement.utils.PreferenceHelper


object RetrofitClient {
    private const val BASE_URL = "http://192.168.0.128:8000/"

    // Cliente HTTP con interceptores
    private val httpClient by lazy {
        OkHttpClient.Builder()
            // Interceptor para CSRF
            .addInterceptor { chain ->
                val original = chain.request()
                // Aquí deberías obtener el token CSRF desde una cookie previa
                val csrfToken = PreferenceHelper.getCsrfToken() ?: ""

                val request = original.newBuilder()
                    .header("X-CSRFToken", csrfToken)
                    .build()
                chain.proceed(request)
            }
            // Interceptor para autenticación con token
            .addInterceptor(AuthInterceptor())
            // Interceptor para logging (útil para debugging)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor{ chain ->
                val request = chain.request()
                val response = chain.proceed(request)
                val body = response.peekBody(Long.MAX_VALUE).string()
                android.util.Log.d("API_RESPONSE", "URL: ${request.url}, BODY: $body")
                response
            }
            .build()

    }

    // Configuración común de Gson lenient
    private val lenientGson = GsonBuilder()
        .setLenient()
        .create()

    // Instancia de Retrofit
    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create(lenientGson))  // <-- Aquí usamos el gson lenient
            .build()
            .create(ApiService::class.java)
    }

    // Para el servicio de autenticación (sin interceptor de autenticación)
    val authService: AuthApiService by lazy {
        val authClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val csrfToken = PreferenceHelper.getCsrfToken() ?: ""
                val request = original.newBuilder()
                    .header("X-CSRFToken", csrfToken)
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(authClient)
            .addConverterFactory(GsonConverterFactory.create(lenientGson))
            .build()
            .create(AuthApiService::class.java)
    }
}