package com.novacartografia.kanbanprojectmanagement.models

import com.google.gson.annotations.SerializedName

data class ProjectMovementLine(
    val id: Int,

    @SerializedName("project")
    val project_id: Int = 0,

    @SerializedName("employee")
    val employee_id: Int = 0,

    val date: String,

    @SerializedName("previous_project")
    val previous_project_id: Int = 0,

    @SerializedName("project_name")
    val project_name: String?,

    @SerializedName("previous_project_name")
    val previous_project_name: String?
)