package com.novacartografia.kanbanprojectmanagement.ui.employees

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novacartografia.kanbanprojectmanagement.models.Employee
import com.novacartografia.kanbanprojectmanagement.repository.EmployeeRepository
import kotlinx.coroutines.launch

class EmployeeViewModel : ViewModel(){
    private val repository = EmployeeRepository()

    private val _employees = MutableLiveData<List<Employee>>()
    val employees: LiveData<List<Employee>> = _employees

    private val _loading = MutableLiveData<Boolean>()
    val loading : LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String>()
    val error : LiveData<String> = _error

    private val _newEmployee = MutableLiveData<Employee>()
    val newEmployee : LiveData<Employee> = _newEmployee

    private val _employee = MutableLiveData<Employee>()
    val employee : LiveData<Employee> = _employee

    private val _searrchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String> = _searrchQuery

    //Funciones para crear, obtener y listar empleados
    fun getEmployees(){
        viewModelScope.launch {
            try {
                _loading.value  = true
                val employeeList = repository.getEmployees()
                _employees.value = employeeList
                _loading.value = false
            }catch (
                e: Exception){
                _loading.value = false
                _error.value = e.message ?: "Unknown error"
            }
        }
    }

    fun createEmployee(employee: Employee){
        viewModelScope.launch {
            try {
                _loading.value = true
                val newEmployee = repository.createEmployee(employee)
                _newEmployee.value = newEmployee
                _loading.value = false
            }catch (e: Exception){
                _loading.value = false
                _error.value = e.message ?: "Unknown error"
            }
        }
    }

    fun getEmployee(id: Int){
        viewModelScope.launch {
            try {
                _loading.value = true
                val employee = repository.getEmployee(id)
                _employee.value = employee
                _loading.value = false
            }catch (e: Exception){
                _loading.value = false
                _error.value = e.message ?: "Unknown error"
            }
        }
    }

    fun updateEmployee(id: Int, employee: Employee) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val updatedEmployee = repository.updateEmployee(id, employee)
                _employee.value = updatedEmployee
                _loading.value = false
            } catch (e: Exception) {
                _loading.value = false
                _error.value = e.message ?: "Unknown error"
            }
        }
    }

    fun deleteEmployee(id: Int) {
        viewModelScope.launch {
            try {
                _loading.value = true
                repository.deleteEmployee(id)
                // Actualizar la lista después de eliminar
                getEmployees()
                _loading.value = false
            } catch (e: Exception) {
                _loading.value = false
                _error.value = e.message ?: "Unknown error"
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searrchQuery.value = query
    }


}