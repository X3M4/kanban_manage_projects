package com.novacartografia.kanbanprojectmanagement.repository

import com.novacartografia.kanbanprojectmanagement.api.RetrofitClient
import com.novacartografia.kanbanprojectmanagement.models.EmployeeNeeded
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EmployeeNeedRepository {
    private val apiService = RetrofitClient.instance

    //Employee Needed Repository
    suspend fun getEmployeeNeeded(): List<EmployeeNeeded>{
        return withContext(Dispatchers.IO){
            apiService.getEmployeeNeeds()
        }
    }

    suspend fun getEmployeeNeeded(id: Int): EmployeeNeeded {
        return withContext(Dispatchers.IO){
            apiService.getEmployeeNeed(id)
        }
    }

    suspend fun createEmployeeNeeded(employeeNeeded: EmployeeNeeded): EmployeeNeeded {
        return withContext(Dispatchers.IO){
            apiService.createEmployeeNeed(employeeNeeded)
        }
    }

}