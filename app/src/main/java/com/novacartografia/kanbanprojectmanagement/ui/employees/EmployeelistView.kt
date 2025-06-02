package com.novacartografia.kanbanprojectmanagement.ui.employees

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.novacartografia.kanbanprojectmanagement.models.Employee
import com.novacartografia.kanbanprojectmanagement.ui.theme.Blue100
import com.novacartografia.kanbanprojectmanagement.ui.theme.Blue500
import com.novacartografia.kanbanprojectmanagement.ui.theme.Gray100
import com.novacartografia.kanbanprojectmanagement.ui.theme.Gray300
import com.novacartografia.kanbanprojectmanagement.ui.theme.Gray400
import com.novacartografia.kanbanprojectmanagement.ui.theme.Gray500
import com.novacartografia.kanbanprojectmanagement.ui.theme.Gray700
import com.novacartografia.kanbanprojectmanagement.ui.theme.Green100
import com.novacartografia.kanbanprojectmanagement.ui.theme.Lime100
import com.novacartografia.kanbanprojectmanagement.ui.theme.Lime500
import com.novacartografia.kanbanprojectmanagement.ui.theme.Lime600
import com.novacartografia.kanbanprojectmanagement.ui.theme.Orange100
import com.novacartografia.kanbanprojectmanagement.ui.theme.Orange300
import com.novacartografia.kanbanprojectmanagement.ui.theme.Orange500
import com.novacartografia.kanbanprojectmanagement.ui.theme.Purple100
import com.novacartografia.kanbanprojectmanagement.ui.theme.Purple200
import com.novacartografia.kanbanprojectmanagement.ui.theme.Red100
import com.novacartografia.kanbanprojectmanagement.ui.theme.Red200
import com.novacartografia.kanbanprojectmanagement.ui.theme.Red300
import com.novacartografia.kanbanprojectmanagement.ui.theme.Red50
import com.novacartografia.kanbanprojectmanagement.ui.theme.Red500
import com.novacartografia.kanbanprojectmanagement.ui.theme.Red800
import com.novacartografia.kanbanprojectmanagement.ui.theme.Rose100
import com.novacartografia.kanbanprojectmanagement.ui.theme.Rose200
import com.novacartografia.kanbanprojectmanagement.ui.theme.Teal200
import com.novacartografia.kanbanprojectmanagement.ui.theme.White
import com.novacartografia.kanbanprojectmanagement.ui.theme.Yellow100
import com.novacartografia.kanbanprojectmanagement.ui.theme.Yellow300
import com.novacartografia.kanbanprojectmanagement.ui.theme.Yellow500

@Composable
fun EmployeeListView(
    modifier: Modifier,
    employeesViewModel: EmployeeViewModel = viewModel()
) {
    val employees = employeesViewModel.employees.observeAsState(initial = emptyList())
    val loading = employeesViewModel.loading.observeAsState(initial = false)
    val error = employeesViewModel.error.observeAsState()
    val searchQuery = employeesViewModel.searchQuery.observeAsState("")

    // Filtrar empleados según texto de búsqueda
    val filteredEmployees = if (searchQuery.value.isBlank()) {
        employees.value
    } else {
        employees.value.filter { employee ->
            employee.name.contains(searchQuery.value, ignoreCase = true) ||
                    employee.job.contains(searchQuery.value, ignoreCase = true)
        }
    }

    LaunchedEffect(key1 = Unit) {
        employeesViewModel.getEmployees()
    }

    Column(modifier = modifier.padding(horizontal = 8.dp)) {

        // Añadir campo de búsqueda
        SearchField(
            searchQuery = searchQuery.value,
            onSearchQueryChange = { employeesViewModel.setSearchQuery(it) }
        )

        if (loading.value) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(32.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else if (!error.value.isNullOrEmpty()) {
            ErrorCard(error.value ?: "Error desconocido")
        } else {
            if (filteredEmployees.isEmpty()) {
                if (searchQuery.value.isBlank()) {
                    EmptyStateMessage()
                } else {
                    NoSearchResultsMessage(searchQuery.value)
                }
            } else {
                LazyColumn {
                    items(filteredEmployees) { employee ->
                        EmployeeItem(employee = employee)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchField(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text("Buscar empleados...") },
        leadingIcon = {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Search,
                contentDescription = "Buscar"
            )
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onSearchQueryChange("") }) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Clear,
                        contentDescription = "Limpiar búsqueda"
                    )
                }
            }
        },
        singleLine = true,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = White,
            unfocusedContainerColor = Gray300,
            errorContainerColor = Red500
        )
    )
}

@Composable
private fun NoSearchResultsMessage(query: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Search,
                contentDescription = "No hay resultados",
                modifier = Modifier.size(48.dp),
                tint = Gray400
            )
            Text(
                text = "No se encontraron resultados para \"$query\"",
                style = MaterialTheme.typography.titleLarge,
                color = Gray500,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Prueba con otra búsqueda",
                style = MaterialTheme.typography.bodyMedium,
                color = Gray400,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun ErrorCard(errorMessage: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Red50
        ),
        border = BorderStroke(1.dp, Red300)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Aquí necesitas un Icon en lugar del objeto Icons
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Warning,
                contentDescription = "Advertencia",
                tint = Red800
            )
            Text(
                text = "Error: $errorMessage",
                color = Red800,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
private fun EmptyStateMessage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Puedes añadir un ícono aquí
            Text(
                text = "No hay empleados disponibles",
                style = MaterialTheme.typography.titleLarge,
                color = Gray500
            )
            Text(
                text = "Los empleados aparecerán aquí cuando estén disponibles",
                style = MaterialTheme.typography.bodyMedium,
                color = Gray400,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun EmployeeItem(employee: Employee) {
    val colorByJob = when (employee.job) {
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
            .padding(vertical = 8.dp, horizontal = 4.dp)
            .animateContentSize(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Gray100
        ),
        border = BorderStroke(width = 3.dp, color = colorByJob),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Cabecera de la tarjeta
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorByJob.copy(alpha = 0.1f))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = employee.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.weight(0.5f))

                Text(
                    text = employee.project_id.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Gray700,
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = colorByJob),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                ) {

                    Text(
                        text = employee.job,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }

            // Contenido de la tarjeta
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
            ) {
                // Primera columna: Información básica
                BasicInfoColumn(
                    employee = employee,
                    modifier = Modifier.weight(1f)
                )

                // Segunda columna: Permisos
                PermissionsColumn(
                    employee = employee,
                    modifier = Modifier.weight(1f)
                )

                // Tercera columna: Especialidades
                SpecialtiesColumn(
                    employee = employee,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun BasicInfoColumn(employee: Employee, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(end = 8.dp)
    ) {
        InfoSection(title = "Información Personal") {
            InfoItem(label = "Estado:", value = employee.state)
            employee.city?.let {
                InfoItem(label = "Ciudad:", value = it)
            }
            employee.academic_training?.let {
                InfoItem(label = "Formación:", value = it)
            }
        }
    }
}

@Composable
private fun InfoSection(
    title: String,
    content: @Composable () -> Unit
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    content()
}

@Composable
private fun InfoItem(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = Gray700
        )
        Text(
            text = " $value",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun PermissionsColumn(employee: Employee, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(horizontal = 8.dp)
    ) {
        InfoSection(title = "Permisos") {
            // Utilizar FlowRow con parámetros correctos
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                employee.twenty_hours?.let {
                    if (it) SkillItem(skillName = "20 Horas", skillValue = it, color = Green100)
                }
                employee.sixty_hours?.let {
                    if (it) SkillItem(skillName = "60 Horas", skillValue = it, color = Lime100)
                }
                employee.driver_license?.let {
                    if (it) SkillItem(skillName = "Carnet", skillValue = it, color = Red100)
                }
                employee.confine?.let {
                    if (it) SkillItem(skillName = "Confine", skillValue = it, color = Yellow100)
                }
                employee.height?.let {
                    if (it) SkillItem(skillName = "Alturas", skillValue = it, color = Blue100)
                }
            }
        }
    }
}

@Composable
private fun SpecialtiesColumn(employee: Employee, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(start = 8.dp)
    ) {
        InfoSection(title = "Especialidades") {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                employee.mining?.let {
                    if (it) SkillItem(skillName = "Minas", skillValue = it, color = Purple100)
                }
                employee.railway_carriage?.let {
                    if (it) SkillItem(skillName = "Carro de Vía", skillValue = it, color = Purple200)
                }
                employee.railway_mounting?.let {
                    if (it) SkillItem(skillName = "Montaje Vías", skillValue = it, color = Rose100)
                }
                employee.building?.let {
                    if (it) SkillItem(skillName = "Edificación", skillValue = it, color = Orange100)
                }
                employee.office_work?.let {
                    if (it) SkillItem(skillName = "Oficina", skillValue = it, color = Red200)
                }
                employee.scanner?.let {
                    if (it) SkillItem(skillName = "Escáner", skillValue = it, color = Teal200)
                }
                employee.leveling?.let {
                    if (it) SkillItem(skillName = "Nivelación", skillValue = it, color = Rose200)
                }
                employee.static?.let {
                    if (it) SkillItem(skillName = "Estática", skillValue = it, color = Yellow300)
                }
                employee.trace?.let {
                    if (it) SkillItem(skillName = "Trazado", skillValue = it, color = Teal200)
                }
            }
        }
    }
}

@Composable
fun SkillItem(
    skillName: String,
    skillValue: Boolean,
    color: Color,
) {
    if (skillValue) {
        Card(
            modifier = Modifier
                .wrapContentWidth()
                .padding(bottom = 4.dp, end = 4.dp),
            elevation = CardDefaults.cardElevation(0.dp),
            colors = CardDefaults.cardColors(
                containerColor = color.copy(alpha = 0.9f)
            ),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            border = BorderStroke(0.5.dp, color.copy(alpha = 0.7f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Círculo decorativo
                androidx.compose.foundation.Canvas(
                    modifier = Modifier.size(6.dp)
                ) {
                    drawCircle(color = Color.White)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = skillName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black.copy(alpha = 0.8f)
                )
            }
        }
    }
}