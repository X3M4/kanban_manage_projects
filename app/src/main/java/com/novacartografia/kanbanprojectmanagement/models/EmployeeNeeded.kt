package com.novacartografia.kanbanprojectmanagement.models

data class EmployeeNeeded(
    val id: Int,
    val project_id: Int,
    val type: String,
    val quantity: Int,
    val start_date: String,
    val end_date: String,
    val fulfilled: Boolean,
)