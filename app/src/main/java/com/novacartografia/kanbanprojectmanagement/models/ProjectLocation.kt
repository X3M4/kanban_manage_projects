package com.novacartografia.kanbanprojectmanagement.models

import com.google.gson.annotations.SerializedName
import java.util.Date

data class ProjectLocation(
    val id: Int,
    @SerializedName("project") val projectId: Int,
    val latitude: Double,
    val longitude: Double,
    val address: String?,
    val city: String?,
    val province: String?,
    @SerializedName("country") val country: String = "España",
    @SerializedName("created_at") val createdAt: Date,
    @SerializedName("updated_at") val updatedAt: Date
) {
    // Constructor vacío para Gson
    constructor() : this(
        0, 0, 0.0, 0.0, null, null, null, "España",
        Date(), Date()
    )

    override fun toString(): String {
        return "Ubicación del proyecto $projectId"
    }
}