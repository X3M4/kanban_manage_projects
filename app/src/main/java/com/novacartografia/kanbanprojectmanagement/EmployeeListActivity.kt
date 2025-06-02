package com.novacartografia.kanbanprojectmanagement

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.novacartografia.kanbanprojectmanagement.ui.employees.EmployeeListView
import com.novacartografia.kanbanprojectmanagement.ui.employees.EmployeeViewModel
import com.novacartografia.kanbanprojectmanagement.ui.theme.KanbanProjectManagementTheme
import com.novacartografia.kanbanprojectmanagement.BaseView

class EmployeeListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            KanbanProjectManagementTheme {
                val context = LocalContext.current
                val employeesVM = viewModel<EmployeeViewModel>()

                Scaffold(
                    topBar = {
                        TopAppBar(context)
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    EmployeeListView(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        employeesVM
                    )
                }
            }
        }
    }
}
