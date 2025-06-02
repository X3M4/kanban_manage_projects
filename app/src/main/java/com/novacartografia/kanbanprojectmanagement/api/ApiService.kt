package com.novacartografia.kanbanprojectmanagement.api

import com.novacartografia.kanbanprojectmanagement.models.Employee
import com.novacartografia.kanbanprojectmanagement.models.EmployeeNeeded
import com.novacartografia.kanbanprojectmanagement.models.LoginRequest
import com.novacartografia.kanbanprojectmanagement.models.LoginResponse
import com.novacartografia.kanbanprojectmanagement.models.Project
import com.novacartografia.kanbanprojectmanagement.models.ProjectLocation
import com.novacartografia.kanbanprojectmanagement.models.ProjectMovementLine
import com.novacartografia.kanbanprojectmanagement.models.TokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface  ApiService{
    @GET("api/employees/")
    suspend fun getEmployees(): Response<List<Employee>>
    @GET("api/employees/{id}/")
    suspend fun getEmployee(id: Int): Employee
    @POST("API/employees/add/")
    suspend fun createEmployee(employee: Employee): Employee
    @PUT("api/employees/{id}/edit")
    suspend fun updateEmployee(employee: Int, employee1: Employee): Employee
    @DELETE("employees/{id}/delete")
    suspend fun deleteEmployee(id: Int): Employee

    @GET("api/projects/")
    suspend fun getProjects(): List<Project>
    @GET("api/projects/{id}/")
    suspend fun getProject(id: Int): Project
    @POST("api/projects/add/")
    suspend fun createProject(project: Project): Project
    @PUT("projects/{id}/update")
    suspend fun updateProject(id: Int): Project
    @DELETE("api/projects/{id}/delete")
    suspend fun deleteProject(id: Int): Project

    @GET("employee_needed/")
    suspend fun getEmployeeNeeds(): List<EmployeeNeeded>
    @GET("employee_needed/{id}/")
    suspend fun getEmployeeNeed(id: Int): EmployeeNeeded
    @POST("api/employee_needed/add/")
    suspend fun createEmployeeNeed(employeeNeeded: EmployeeNeeded): EmployeeNeeded
    @PUT("employee_needed/{id}/update")
    suspend fun updateEmployeeNeed(id: Int, employeeNeeded: EmployeeNeeded): EmployeeNeeded
    @DELETE("employee_needed/{id}/delete")
    suspend fun deleteEmployeeNeed(id: Int): EmployeeNeeded

    @GET("api/project_movement_lines/")
    suspend fun getProjectMovementLine(): Response<List<ProjectMovementLine>>
    @GET("api/project_movement_lines/{id}/")
    suspend fun getProjectMovementLine(id: Int): ProjectMovementLine
    @POST("api/project_movement_lines/add/")
    suspend fun createProjectMovementLine(projectMovementLine: ProjectMovementLine): ProjectMovementLine
    @PUT("api/project_movement_lines/{id}/update")
    suspend fun updateProjectMovementLine(id: Int, projectMovementLine: ProjectMovementLine): ProjectMovementLine
    @DELETE("api/project_movement_lines/{id}/delete")
    suspend fun deleteProjectMovementLine(id: Int): ProjectMovementLine

    @GET("api/maps/")
    suspend fun getProjectLocations(): Response<List<ProjectLocation>>
    @GET("api/maps/{id}/")
    suspend fun getProjectLocation(@Path("id") id: Int): Response<ProjectLocation>
    @POST("api/maps/add-location/{id}/")
    suspend fun createProjectLocation(@Body projectLocation: ProjectLocation): Response<ProjectLocation>
    @PUT("api/maps/edit-location/{id}/")
    suspend fun updateProjectLocation(@Path("id") id: Int, @Body projectLocation: ProjectLocation): Response<ProjectLocation>
    @DELETE("api/maps/{id}/")
    suspend fun deleteProjectLocation(@Path("id") id: Int): Response<Void>


}

interface AuthApiService {
    @POST("api/token/")
    @Headers("Content-Type: application/json")
    suspend fun login(@Body loginRequest: LoginRequest): Response<TokenResponse>

}



