package com.novacartografia.kanbanprojectmanagement.ui.movements

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.novacartografia.kanbanprojectmanagement.models.Employee
import com.novacartografia.kanbanprojectmanagement.models.Project
import com.novacartografia.kanbanprojectmanagement.models.ProjectMovementLine
import com.novacartografia.kanbanprojectmanagement.ui.employees.EmployeeViewModel
import com.novacartografia.kanbanprojectmanagement.ui.projects.ProjectLocationViewModel
import com.novacartografia.kanbanprojectmanagement.ui.projects.ProjectViewModel
import com.novacartografia.kanbanprojectmanagement.ui.theme.Blue500
import com.novacartografia.kanbanprojectmanagement.ui.theme.Gray100
import com.novacartografia.kanbanprojectmanagement.ui.theme.Gray300
import com.novacartografia.kanbanprojectmanagement.ui.theme.Green300
import com.novacartografia.kanbanprojectmanagement.ui.theme.Lime500
import com.novacartografia.kanbanprojectmanagement.ui.theme.Lime600
import com.novacartografia.kanbanprojectmanagement.ui.theme.Orange300
import com.novacartografia.kanbanprojectmanagement.ui.theme.Orange500
import com.novacartografia.kanbanprojectmanagement.ui.theme.White
import com.novacartografia.kanbanprojectmanagement.ui.theme.Yellow300
import com.novacartografia.kanbanprojectmanagement.ui.theme.Yellow500
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectMovementLineView(

    modifier: Modifier = Modifier,
    onMovementClick: (ProjectMovementLine) -> Unit,
    onAddMovementClick: () -> Unit,
    viewModel: ProjectMovementLineViewModel = viewModel(),
    employeesVM: EmployeeViewModel = viewModel(),
    projectVM: ProjectViewModel = viewModel(),
) {
    val movements by viewModel.movements.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    val employeeList = employeesVM.employees.observeAsState(initial = emptyList())
    val projectList = projectVM.projects.observeAsState(initial = emptyList())

    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        employeesVM.getEmployees()
        projectVM.getProjects()
    }

    val filteredMovements = if (searchQuery.isEmpty()) {
        movements
    } else {
        movements.filter { movement ->
            // Normalizar la consulta eliminando acentos
            val query = searchQuery.lowercase().removeAccents()
            val project = projectList.value.find { it.id == movement.project_id }
            val previousProject = projectList.value.find { it.id == movement.previous_project_id }
            val employee = employeeList.value.find { it.id == movement.employee_id }

            // Normalizar los textos para buscar
            val projectName = project?.name?.lowercase()?.removeAccents() ?: ""
            val prevProjectName = previousProject?.name?.lowercase()?.removeAccents() ?: ""
            val employeeName = employee?.name?.lowercase()?.removeAccents() ?: ""

            // Para búsqueda de fechas
            val dateMatches = try {
                val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
                isoFormat.timeZone = TimeZone.getTimeZone("UTC")
                val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                val date = isoFormat.parse(movement.date)
                val formattedDate = date?.let { displayFormat.format(it) } ?: ""

                formattedDate.lowercase().contains(query)
            } catch (e: Exception) {
                movement.date.lowercase().contains(query)
            }

            // Filtrar utilizando textos normalizados
            movement.id.toString().contains(query) ||
                    employeeName.contains(query) ||
                    prevProjectName.contains(query) ||
                    projectName.contains(query) ||
                    dateMatches
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Movimientos de Proyectos") },
                actions = {
                    IconButton(onClick = { viewModel.loadMovements() }) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Recargar"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddMovementClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Añadir movimiento"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(
                    top = 8.dp,
                    bottom = paddingValues.calculateBottomPadding(),
                    start = paddingValues.calculateBottomPadding(),
                    end = paddingValues.calculateEndPadding(layoutDirection = LayoutDirection.Ltr),
                )
        ) {
            // Campo de búsqueda
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                placeholder = { Text("Buscar movimientos...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar"
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Limpiar"
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = White,
                    unfocusedContainerColor = Gray300
                )
            )

            // Mostrar error si existe
            if (error != null) {
                Text(
                    text = error ?: "",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }

            // Mostrar indicador de carga
            if (loading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (filteredMovements.isEmpty()) {
                // Mostrar mensaje si no hay movimientos
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay movimientos disponibles",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Gray300
                    )
                }
            } else {
                // Lista de movimientos
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredMovements) { movement ->
                        MovementCard(
                            movement = movement,
                            employeeList = employeeList.value,
                            projectList = projectList.value,
                            onClick = { onMovementClick(movement) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MovementCard(
    movement: ProjectMovementLine,
    employeeList: List<Employee>,
    projectList: List<Project>,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    val formattedDate = try {
        val date = dateFormat.parse(movement.date)
        date?.let { displayFormat.format(it) } ?: movement.date
    } catch (e: Exception) {
        movement.date
    }

    val employee = employeeList.find { it.id == movement.employee_id }


    // Usar los nombres de campos correctos
    val fromProject = projectList.find { it.id == movement.previous_project_id }
    val toProject = projectList.find { it.id == movement.project_id }

    val colorByJob = when (employee?.job) {
        "TOPÓGRAFO/A DE CAMPO" -> Lime500
        "Auxiliar de topografía" -> Yellow500
        "Jefe de Proyectos" -> Orange300
        "Jefe Departamento Proyectos" -> Orange500
        "Piloto de Seguridad de Circulación (PSC)" -> Blue500
        else -> Lime600
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(width = 3.dp, color = colorByJob),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            // Título con empleado

            Text(
                text = "Movimiento: ${employee?.name ?: "Empleado #${movement.employee_id}"}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Fecha
            Text(
                text = "Fecha: $formattedDate",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Cambio de proyecto
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Proyecto anterior
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(8.dp)
                ) {
                    if (fromProject != null) {
                        Text(
                            text = fromProject.name,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }else{
                        Text(
                            text = "Sin Proyecto",
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Flecha
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Hacia",
                    tint = MaterialTheme.colorScheme.primary
                )

                // Proyecto nuevo
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(8.dp)
                ) {
                    if (toProject != null) {
                        Text(
                            text = toProject.name,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }else{
                        Text(
                            text = "Sin Proyecto",
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchMovementField(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        placeholder = { Text("Buscar movimientos...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar"
            )
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onSearchQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Limpiar búsqueda"
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(24.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = White,
            unfocusedContainerColor = Gray300,
        )
    )
}

@Composable
fun MovementCard(
    movement: ProjectMovementLine,
    onClick: () -> Unit,
    projectList: List<Project>,
) {
    val lastProject = projectList.find { it.id == movement.previous_project_id }
    val newProject = projectList.find { it.id == movement.project_id }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Formatear fecha para mejor visualización
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val originalFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formattedDate = try {
                    val date = originalFormat.parse(movement.date)
                    dateFormat.format(date)
                } catch (e: Exception) {
                    movement.date
                }



                Text(
                    text = "Movimiento #${movement.id}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Visualización del movimiento
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Proyecto Anterior",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        if (lastProject != null) {
                            Text(
                                text = "#${movement.previous_project_name}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Movimiento",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Nuevo Proyecto",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        if (newProject != null) {
                            Text(
                                text = "#${movement.project_name}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Información del empleado
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = movement.employee_id.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.tertiaryContainer)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "#${movement.employee_id}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyMovementsView(onAddClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "No hay movimientos",
            modifier = Modifier.padding(bottom = 16.dp),
            tint = MaterialTheme.colorScheme.outline
        )

        Text(
            text = "No hay movimientos registrados",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Registra movimientos de personal entre proyectos",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        FloatingActionButton(
            onClick = onAddClick,
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Añadir"
                )
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                Text("Crear Movimiento")
            }
        }
    }
}

@Composable
fun NoSearchResultsView(query: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "No hay resultados",
            modifier = Modifier.padding(bottom = 16.dp),
            tint = MaterialTheme.colorScheme.outline
        )

        Text(
            text = "No se encontraron resultados para \"$query\"",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Prueba con otra búsqueda o términos diferentes",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ErrorCard(errorMessage: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Error",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

fun String.removeAccents(): String {
    val normalizedString = java.text.Normalizer.normalize(this, java.text.Normalizer.Form.NFD)
    return normalizedString.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
}