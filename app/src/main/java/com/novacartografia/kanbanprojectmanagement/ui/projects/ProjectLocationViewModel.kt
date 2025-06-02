package com.novacartografia.kanbanprojectmanagement.ui.projects

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novacartografia.kanbanprojectmanagement.models.ProjectLocation
import com.novacartografia.kanbanprojectmanagement.repository.ProjectLocationRepository
import kotlinx.coroutines.launch

class ProjectLocationViewModel : ViewModel() {
    private val repository = ProjectLocationRepository()

    private val _projectLocations = MutableLiveData<List<ProjectLocation>>()
    val projectLocations: MutableLiveData<List<ProjectLocation>> = _projectLocations

    private val _projectLocation = MutableLiveData<ProjectLocation>()
    val projectLocation: LiveData<ProjectLocation> = _projectLocation

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun getProjectLocations() {
        viewModelScope.launch {
            try {
                _loading.value = true
                Log.d("ProjectLocationViewModel", "Obteniendo ubicaciones de proyectos...")

                try {
                    val locations = repository.getProjectLocations()
                    _projectLocations.value = locations
                    Log.d("ProjectLocationViewModel", "Ubicaciones obtenidas: ${locations.size}")
                } catch (e: Exception) {
                    Log.e("ProjectLocationViewModel", "Error al obtener ubicaciones: ${e.message}")
                    // Establecer lista vacía para que la UI no se bloquee
                    _projectLocations.value = emptyList()
                    _error.value = "Error al cargar ubicaciones: ${e.message}"
                }

                _loading.value = false
            } catch (e: Exception) {
                _loading.value = false
                _error.value = "Error inesperado: ${e.message}"
            }
        }
    }

    fun getProjectLocation(id: Int) {
        viewModelScope.launch {
            try {
                _loading.value = true
                try {
                    val location = repository.getProjectLocation(id)
                    _projectLocation.value = location
                } catch (e: Exception) {
                    Log.e("ProjectLocationViewModel", "Error al obtener ubicación: ${e.message}")
                    _error.value = e.message ?: "Error desconocido al obtener la ubicación"
                }
                _loading.value = false
            } catch (e: Exception) {
                _loading.value = false
                _error.value = "Error inesperado: ${e.message}"
            }
        }
    }

    fun createProjectLocation(projectLocation: ProjectLocation) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val response = repository.createProjectLocation(projectLocation)
                if (response.isSuccessful) {
                    _projectLocation.value = response.body()
                } else {
                    throw Exception("Error ${response.code()}: ${response.message()}")
                }
                _loading.value = false
            } catch (e: Exception) {
                _loading.value = false
                _error.value = e.message ?: "Error desconocido al crear la ubicación"
            }
        }
    }

    fun updateProjectLocation(id: Int, projectLocation: ProjectLocation) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val response = repository.updateProjectLocation(id, projectLocation)
                if (response.isSuccessful) {
                    _projectLocation.value = response.body()
                } else {
                    throw Exception("Error ${response.code()}: ${response.message()}")
                }
                _loading.value = false
            } catch (e: Exception) {
                _loading.value = false
                _error.value = e.message ?: "Error desconocido al actualizar la ubicación"
            }
        }
    }

    fun deleteProjectLocation(id: Int) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val response = repository.deleteProjectLocation(id)
                if (!response.isSuccessful) {
                    throw Exception("Error ${response.code()}: ${response.message()}")
                }
                // Actualizar la lista después de eliminar
                getProjectLocations()
                _loading.value = false
            } catch (e: Exception) {
                _loading.value = false
                _error.value = e.message ?: "Error desconocido al eliminar la ubicación"
            }
        }
    }
}