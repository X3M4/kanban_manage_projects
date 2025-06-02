package com.novacartografia.kanbanprojectmanagement.repository

import android.util.Log
import com.novacartografia.kanbanprojectmanagement.api.RetrofitClient
import com.novacartografia.kanbanprojectmanagement.models.ProjectMovementLine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProjectMovementLineRepository {
    private val apiService = RetrofitClient.instance

    // En ProjectMovementLineRepository.kt
    suspend fun getProjectMovementLine(): List<ProjectMovementLine> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getProjectMovementLine()

                // Log de la respuesta bruta
                //Log.d("MovementRepository", "Respuesta: ${response.raw().toString()}")

                val result = response.body() ?: emptyList()

                /* Log detallado
                result.forEach { movement ->
                    Log.d("MovementRepository", "Movimiento JSON: ${movement}")
                } */

                result
            } catch (e: Exception) {
                Log.e("MovementRepository", "Error: ${e.message}", e)
                emptyList()
            }
        }
    }

    suspend fun getProjectMovementLine(id: Int): ProjectMovementLine {
        return withContext(Dispatchers.IO){
            apiService.getProjectMovementLine(id)
        }
    }

    suspend fun createProjectMovementLine(projectMovementLine: ProjectMovementLine): ProjectMovementLine {
        return withContext(Dispatchers.IO){
            apiService.createProjectMovementLine(projectMovementLine)
        }
    }
}