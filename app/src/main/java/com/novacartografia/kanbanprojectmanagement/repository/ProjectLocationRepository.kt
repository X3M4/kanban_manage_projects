package com.novacartografia.kanbanprojectmanagement.repository

import android.util.Log
import com.novacartografia.kanbanprojectmanagement.api.RetrofitClient
import com.novacartografia.kanbanprojectmanagement.models.ProjectLocation
import retrofit2.Response

class ProjectLocationRepository {

    private val apiService = RetrofitClient.instance

    suspend fun getProjectLocations(): List<ProjectLocation> {
        try {
            val response = apiService.getProjectLocations()
            Log.d("ProjectLocationRepository", "Respuesta de API: ${response.code()}")
            if (response.isSuccessful) {
                val locations = response.body() ?: emptyList()
                Log.d("ProjectLocationRepository", "Ubicaciones obtenidas: ${locations.size}")
                return locations
            } else {
                Log.e("ProjectLocationRepository", "Error HTTP: ${response.code()} - ${response.message()}")
                throw Exception("Error ${response.code()}: ${response.message() ?: "Sin mensaje"}")
            }
        } catch (e: Exception) {
            Log.e("ProjectLocationRepository", "Excepción al obtener ubicaciones: ${e.message}", e)
            throw e
        }
    }

    suspend fun getProjectLocation(id: Int): ProjectLocation {
        try {
            val response = apiService.getProjectLocation(id)
            if (response.isSuccessful) {
                return response.body() ?: throw Exception("No se encontró la ubicación del proyecto")
            } else {
                Log.e("ProjectLocationRepository", "Error HTTP: ${response.code()} - ${response.message()}")
                throw Exception("Error ${response.code()}: ${response.message() ?: "Sin mensaje"}")
            }
        } catch (e: Exception) {
            Log.e("ProjectLocationRepository", "Excepción al obtener ubicación: ${e.message}", e)
            throw e
        }
    }

    suspend fun createProjectLocation(projectLocation: ProjectLocation): Response<ProjectLocation> {
        return apiService.createProjectLocation(projectLocation)
    }

    suspend fun updateProjectLocation(id: Int, projectLocation: ProjectLocation): Response<ProjectLocation> {
        return apiService.updateProjectLocation(id, projectLocation)
    }

    suspend fun deleteProjectLocation(id: Int): Response<Void> {
        return apiService.deleteProjectLocation(id)
    }
}