package com.novacartografia.kanbanprojectmanagement

import android.app.Application
import com.novacartografia.kanbanprojectmanagement.utils.PreferenceHelper

class KanbanApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Inicializar PreferenceHelper
        PreferenceHelper.init(this)
    }
}