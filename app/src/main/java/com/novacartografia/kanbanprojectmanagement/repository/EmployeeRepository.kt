package com.novacartografia.kanbanprojectmanagement.repository

import com.novacartografia.kanbanprojectmanagement.api.RetrofitClient
import com.novacartografia.kanbanprojectmanagement.models.Employee
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EmployeeRepository {
    private val apiService = RetrofitClient.instance
    //Employee Repository
    suspend fun getEmployees(): List<Employee> {
        return withContext(Dispatchers.IO) {
            val response = apiService.getEmployees()
            if (response.isSuccessful && response.body() != null) {
                response.body()!!
            } else {
                // Log del error para depuración
                android.util.Log.e("API_ERROR", "Error: ${response.errorBody()?.string()}")
                throw Exception("Error obteniendo empleados: ${response.message()}")
            }
        }
    }

    suspend fun getEmployee(id: Int): Employee{
        return withContext(Dispatchers.IO){
            apiService.getEmployee(id)
        }
    }

    suspend fun createEmployee(employee: Employee): Employee{
        return withContext(Dispatchers.IO){
            apiService.createEmployee(employee)
        }
    }

    suspend fun updateEmployee(id: Int, employee: Employee): Employee{
        return withContext(Dispatchers.IO){
            apiService.updateEmployee(id, employee)
        }
    }

    suspend fun deleteEmployee(id: Int): Employee{
        return withContext(Dispatchers.IO){
            apiService.deleteEmployee(id)
        }
    }


}