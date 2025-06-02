package com.novacartografia.kanbanprojectmanagement

import android.content.Context
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(context: Context) {
    CenterAlignedTopAppBar(
        title = {
            Text(text = "Gestión de Proyectos")
        },
        navigationIcon = {
            // Botón para volver a la actividad principal
            IconButton(onClick = {
                val intent = Intent(context, BaseActivity::class.java)
                // Limpiar la pila de actividades
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Inicio"
                )
            }
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

            IconButton(onClick = {
                val intent = Intent(context, ProjectMovementLineActivity::class.java)
                context.startActivity(intent)
            }) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Mapa"
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