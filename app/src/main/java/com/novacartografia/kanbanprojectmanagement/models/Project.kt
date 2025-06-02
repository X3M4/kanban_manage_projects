package com.novacartografia.kanbanprojectmanagement.models

data class Project(
    val id: Int,
    val name: String,
    val type: String,
    val description: String,
    val manager: String?,
    val state: String?,
    val academic_training: String?,
    val twenty_hours: Boolean?,
    val sixty_hours: Boolean?,
    val confine: Boolean?,
    val height: Boolean?,
    val mining: Boolean?,
    val railway_carriage: Boolean?,
    val railway_mounting: Boolean?,
    val building: Boolean?,
    val office_work: Boolean?,
    val scanner: Boolean?,
    val leveling: Boolean?,
    val static: Boolean?,
    val trace: Boolean?,
)