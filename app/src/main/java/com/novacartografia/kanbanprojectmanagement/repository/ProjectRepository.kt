package com.novacartografia.kanbanprojectmanagement.repository

import com.novacartografia.kanbanprojectmanagement.api.RetrofitClient
import com.novacartografia.kanbanprojectmanagement.models.Project
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProjectRepository {
    private val apiService = RetrofitClient.instance

    //Project Repository
    suspend fun getProjects(): List<Project>{
        return withContext(Dispatchers.IO){
            apiService.getProjects()
        }
    }

    suspend fun getProject(id: Int): Project {
        return withContext(Dispatchers.IO){
            apiService.getProject(id)
        }
    }

    suspend fun createProject(project: Project): Project {
        return withContext(Dispatchers.IO){
            apiService.createProject(project)
        }
    }

    suspend fun updateProject(id: Int): Project {
        return withContext(Dispatchers.IO){
            apiService.updateProject(id)
        }
    }

    suspend fun deleteProject(id: Int): Project {
        return withContext(Dispatchers.IO){
            apiService.deleteProject(id)
        }
    }
}