package com.novacartografia.kanbanprojectmanagement.models

import android.os.strictmode.CredentialProtectedWhileLockedViolation

data class Employee(
    val id: Int,
    val name: String,
    val job: String,
    val project_id: Int?,
    val street: String?,
    val city: String?,
    val state: String,
    val latitude: Double?,
    val longitude: Double?,
    val academic_training: String?,
    val driver_license: Boolean?,
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
    val locked: Boolean?,
)