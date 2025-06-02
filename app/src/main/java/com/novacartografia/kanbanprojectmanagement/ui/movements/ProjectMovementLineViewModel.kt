package com.novacartografia.kanbanprojectmanagement.ui.movements

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novacartografia.kanbanprojectmanagement.models.ProjectMovementLine
import com.novacartografia.kanbanprojectmanagement.repository.EmployeeRepository
import com.novacartografia.kanbanprojectmanagement.repository.ProjectMovementLineRepository
import com.novacartografia.kanbanprojectmanagement.repository.ProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ProjectMovementLineViewModel : ViewModel() {

    private val repository = ProjectMovementLineRepository()
    private val projectRepository = ProjectRepository()
    private val employeeRepository = EmployeeRepository()

    // Estados observables
    private val _movements = MutableStateFlow<List<ProjectMovementLine>>(emptyList())
    val movements: StateFlow<List<ProjectMovementLine>> = _movements.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Inicialización
    init {
        loadMovements()
    }

    // Cargar todos los movimientos
    fun loadMovements() {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                val result = repository.getProjectMovementLine()

                /* Log detallado de los datos recibidos
                result.forEach { movement ->
                    Log.d("MovementViewModel", "Movimiento: ID=${movement.id}, " +
                            "EmpleadoID=${movement.employee_id}, " +
                            "ProyectoID=${movement.project_id}, " +
                            "Proyecto=${movement.project_name}, " +
                            "ProyectoAnterior=${movement.previous_project_name}")
                } */

                _movements.value = result
                //Log.d("MovementViewModel", "Movimientos cargados: ${result.size}")
            } catch (e: Exception) {
                _error.value = "Error al cargar los movimientos: ${e.message}"
                //Log.e("MovementViewModel", "Error al cargar movimientos", e)
            } finally {
                _loading.value = false
            }
        }
    }

    // Obtener un movimiento por ID
    fun getMovement(id: Int, onResult: (ProjectMovementLine?) -> Unit) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                val result = repository.getProjectMovementLine(id)
                onResult(result)

                //Log.d("MovementViewModel", "Movimiento obtenido: $result")
            } catch (e: Exception) {
                _error.value = "Error al obtener el movimiento: ${e.message}"
                //Log.e("MovementViewModel", "Error al obtener movimiento", e)
                onResult(null)
            } finally {
                _loading.value = false
            }
        }
    }

    // Crear un nuevo movimiento
    fun createMovement(
        projectId: Int,
        employeeId: Int,
        previousProjectId: Int,
        previousProjectName: String,
        currentProjectName: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                // Formatear fecha actual
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val currentDate = dateFormat.format(Date())

                // Crear objeto de movimiento con los nombres de campos correctos
                val movement = ProjectMovementLine(
                    id = 0, // Se asignará en el servidor
                    project_id = projectId,
                    employee_id = employeeId,
                    date = currentDate,
                    previous_project_id = previousProjectId,
                    previous_project_name = previousProjectName, // Actualizado
                    project_name = currentProjectName           // Actualizado
                )

                // Resto del código...
            }catch (error: Exception) {
                _error.value = "Error al crear el movimiento: ${error.message}"
                //Log.e("MovementViewModel", "Error al crear movimiento", error)
                onError(_error.value ?: "Error desconocido")
            } finally {
                _loading.value = false
            }
        }
    }

    // Filtrar movimientos por proyecto
    fun getMovementsByProject(projectId: Int): List<ProjectMovementLine> {
        return movements.value.filter { it.project_id == projectId }
    }

    // Filtrar movimientos por empleado
    fun getMovementsByEmployee(employeeId: Int): List<ProjectMovementLine> {
        return movements.value.filter { it.employee_id == employeeId }
    }

    // Obtener movimientos recientes (últimos 30 días)
    fun getRecentMovements(): List<ProjectMovementLine> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -30)
        val thirtyDaysAgo = calendar.time

        return movements.value.filter {
            try {
                val movementDate = dateFormat.parse(it.date)
                movementDate?.after(thirtyDaysAgo) ?: false
            } catch (e: Exception) {
                false
            }
        }
    }

    // Limpiar el error
    fun clearError() {
        _error.value = null
    }
}