package com.novacartografia.kanbanprojectmanagement

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.novacartografia.kanbanprojectmanagement.ui.map.MapView
import com.novacartografia.kanbanprojectmanagement.ui.theme.KanbanProjectManagementTheme

class MapActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            KanbanProjectManagementTheme {
                val context = LocalContext.current

                Scaffold(
                    topBar = {
                        TopAppBar(context)
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    MapView(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}