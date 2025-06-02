package com.novacartografia.kanbanprojectmanagement

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.novacartografia.kanbanprojectmanagement.ui.theme.KanbanProjectManagementTheme

@Composable
fun BaseView(modifier: Modifier) {
    val context = LocalContext.current

    KanbanProjectManagementTheme {
        Scaffold(
            topBar = {
                ProjectsTopBar(context)
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            // Contenido principal
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Bienvenido a Kanban Project",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Seleccione una opción en la barra superior para comenzar",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        NavigationCard(
                            icon = Icons.Default.Person,
                            title = "Gestión de Personal",
                            description = "Administra la información de empleados",
                            onClick = {
                                val intent = Intent(context, EmployeeListActivity::class.java)
                                context.startActivity(intent)
                            }
                        )

                        NavigationCard(
                            icon = Icons.AutoMirrored.Filled.List, // O cualquier otro icono adecuado
                            title = "Gestión de Proyectos",
                            description = "Administra los proyectos",
                            onClick = {
                                val intent = Intent(context, ProjectListActivity::class.java)
                                context.startActivity(intent)
                            }
                        )

                        NavigationCard(
                            icon = Icons.Default.LocationOn,
                            title = "Mapa",
                            description = "Visualiza ubicaciones",
                            onClick = {
                                val intent = Intent(context, MapActivity::class.java)
                                context.startActivity(intent)
                            }
                        )

                        NavigationCard(
                            icon = Icons.Default.Info,
                            title = "Movimientos",
                            description = "Visualiza ubicaciones",
                            onClick = {
                                val intent = Intent(context, ProjectMovementLineActivity::class.java)
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NavigationCard(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .height(180.dp)
            .padding(8.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier
                    .size(48.dp)
                    .padding(8.dp)
            )

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarWithNavigation(context: android.content.Context) {
    CenterAlignedTopAppBar(
        title = {
            Text(text = "Kanban Project")
        },
        actions = {
            // Botón para EmployeeListActivity
            IconButton(onClick = {
                val intent = Intent(context, EmployeeListActivity::class.java)
                context.startActivity(intent)
            }) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Empleados"
                )
            }

            // Botón para MapActivity
            IconButton(onClick = {
                val intent = Intent(context, MapActivity::class.java)
                context.startActivity(intent)
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