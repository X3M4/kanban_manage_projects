package com.novacartografia.kanbanprojectmanagement.ui.projects

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.novacartografia.kanbanprojectmanagement.models.Project
import androidx.lifecycle.viewModelScope
import com.novacartografia.kanbanprojectmanagement.repository.ProjectRepository
import kotlinx.coroutines.launch

class ProjectViewModel: ViewModel(){
    private val repository = ProjectRepository()

    private val _projects = MutableLiveData<List<Project>>()
    val projects: MutableLiveData<List<Project>> = _projects

    private val _loading = MutableLiveData<Boolean>()
    val loading: MutableLiveData<Boolean> = _loading

    private val _error = MutableLiveData<String>()
    val error: MutableLiveData<String> = _error

    private val _newProject = MutableLiveData<Project>()
    val newProject: MutableLiveData<Project> = _newProject

    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: MutableLiveData<String> = _searchQuery

    //Funciones para crear, obtener y listar proyectos

    fun getProjects() {
        // Implementar la lógica para obtener la lista de proyectos
        viewModelScope.launch {
            try {
                _loading.value = true
                val projectList = repository.getProjects()
                _projects.value = projectList
                _loading.value = false
            } catch (e: Exception) {
                _loading.value = false
                _error.value = e.message ?: "Unknown error"
            }
        }
    }

    fun createProject(project: Project) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val createdProject = repository.createProject(project)
                _newProject.value = createdProject
                _loading.value = false
            } catch (e: Exception) {
                _loading.value = false
                _error.value = e.message ?: "Unknown error"
            }
        }
    }

    fun updateProject(project: Project) {
        viewModelScope.launch {
            try {
                _loading.value = true
                // Simular la actualización de un proyecto existente
                val updatedProject = project // Reemplazar con la llamada real al repositorio
                _newProject.value = updatedProject
                _loading.value = false
            } catch (e: Exception) {
                _loading.value = false
                _error.value = e.message ?: "Unknown error"
            }
        }
    }

    fun deleteProject(projectId: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                // Simular la eliminación de un proyecto
                // Reemplazar con la llamada real al repositorio
                _loading.value = false
            } catch (e: Exception) {
                _loading.value = false
                _error.value = e.message ?: "Unknown error"
            }
        }
    }

    fun searchProjects(query: String) {
        // Implementar la lógica para buscar proyectos por nombre
    }
}