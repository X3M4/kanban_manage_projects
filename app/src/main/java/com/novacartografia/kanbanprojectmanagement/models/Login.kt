package com.novacartografia.kanbanprojectmanagement.models

data class LoginRequest(
    val username: String,
    val password: String
)


data class LoginResponse(
    val token: String,
    val user: User
)

data class User(
    val id: Int,
    val name: String,
    val email: String,
)

data class TokenResponse(
    val token: String
)