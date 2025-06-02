package com.novacartografia.kanbanprojectmanagement.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object PreferenceHelper {
    private const val PREF_NAME = "KanbanAppPreferences"
    private const val KEY_TOKEN = "auth_token"
    private const val KEY_CSRF_TOKEN = "csrf_token"  // Añadir constante para CSRF

    private lateinit var preferences: SharedPreferences

    fun init(context: Context){
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveToken(token: String){
        preferences.edit {
            putString(KEY_TOKEN, token)
        }
    }

    fun getToken(): String? {
        return preferences.getString(KEY_TOKEN, "") ?: ""
    }

    fun clearToken() {
        preferences.edit {
            remove(KEY_TOKEN)
        }
    }

    // Métodos corregidos para CSRF token
    fun saveCsrfToken(token: String) {
        preferences.edit {
            putString(KEY_CSRF_TOKEN, token)
        }
    }

    fun getCsrfToken(): String? {
        return preferences.getString(KEY_CSRF_TOKEN, null)
    }

    // Método para limpiar todos los tokens
    fun clearAllTokens() {
        preferences.edit {
            remove(KEY_TOKEN)
            remove(KEY_CSRF_TOKEN)
        }
    }
}