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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.novacartografia.kanbanprojectmanagement.models.Project
import com.novacartografia.kanbanprojectmanagement.ui.projects.ProjectListView
import com.novacartografia.kanbanprojectmanagement.ui.projects.ProjectViewModel
import com.novacartografia.kanbanprojectmanagement.ui.theme.KanbanProjectManagementTheme

class ProjectListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KanbanProjectManagementTheme {
                val context = LocalContext.current
                val projectVM = viewModel<ProjectViewModel>()

                Scaffold(
                    topBar = {
                        TopAppBar(context)
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    ProjectListView(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        projectViewModel = projectVM,
                        onProjectClick = { project ->
                            // Implementar cuando se cree ProjectDetailActivity
                        },
                        onAddProjectClick = {
                            // Implementar cuando se cree CreateProjectActivity
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsTopBar(context: Context) {  // Cambiado de ProjectTopAppBar a ProjectsTopBar
    CenterAlignedTopAppBar(
        title = {
            Text(text = "Gestión de Proyectos")
        },
        navigationIcon = {
            IconButton(onClick = {
                try {
                    val intent = Intent(context, BaseActivity::class.java)
                    context.startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Inicio"
                )
            }
        },
        actions = {
            IconButton(onClick = {
                try {
                    val intent = Intent(context, EmployeeListActivity::class.java)
                    context.startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Empleados"
                )
            }

            IconButton(onClick = {
                try {
                    val intent = Intent(context, MapActivity::class.java)
                    context.startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Mapa"
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}