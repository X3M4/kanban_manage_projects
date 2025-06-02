package com.novacartografia.kanbanprojectmanagement.repository

import com.novacartografia.kanbanprojectmanagement.utils.Resource
import com.novacartografia.kanbanprojectmanagement.api.AuthApiService
import com.novacartografia.kanbanprojectmanagement.models.LoginRequest
import com.novacartografia.kanbanprojectmanagement.models.LoginResponse
import com.novacartografia.kanbanprojectmanagement.models.TokenResponse
import com.novacartografia.kanbanprojectmanagement.utils.PreferenceHelper
import retrofit2.Response

class AuthRepository(private val authApiService: AuthApiService) {


    suspend fun login(username: String, password: String): Resource<TokenResponse> {
        return try {
            val request = LoginRequest(username, password)
            val response = authApiService.login(request)

            if (response.isSuccessful) {
                response.body()?.let { tokenResponse ->
                    PreferenceHelper.saveToken(tokenResponse.token)
                    Resource.Success(tokenResponse)
                } ?: Resource.Error("Respuesta vacía del servidor")
            } else {
                Resource.Error("Error de autenticación: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("Error en la solicitud: ${e.localizedMessage ?: e.toString()}")
        }
    }
}