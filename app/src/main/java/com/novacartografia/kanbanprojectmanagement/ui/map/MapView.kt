package com.novacartografia.kanbanprojectmanagement.ui.map

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.util.Log
import android.view.ContextThemeWrapper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.LocationOn

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.novacartografia.kanbanprojectmanagement.R
import com.novacartografia.kanbanprojectmanagement.ui.employees.EmployeeViewModel
import androidx.core.graphics.createBitmap
import com.google.gson.JsonPrimitive
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPolylineAnnotationManager
import com.novacartografia.kanbanprojectmanagement.models.Employee
import com.novacartografia.kanbanprojectmanagement.models.Project
import com.novacartografia.kanbanprojectmanagement.ui.projects.ProjectListView
import com.novacartografia.kanbanprojectmanagement.ui.projects.ProjectLocationViewModel
import com.novacartografia.kanbanprojectmanagement.ui.projects.ProjectViewModel
import com.novacartografia.kanbanprojectmanagement.ui.theme.Blue500
import com.novacartografia.kanbanprojectmanagement.ui.theme.Gray300
import com.novacartografia.kanbanprojectmanagement.ui.theme.Gray500
import com.novacartografia.kanbanprojectmanagement.ui.theme.Green500
import com.novacartografia.kanbanprojectmanagement.ui.theme.Green700
import com.novacartografia.kanbanprojectmanagement.ui.theme.Lime500
import com.novacartografia.kanbanprojectmanagement.ui.theme.Orange500
import com.novacartografia.kanbanprojectmanagement.ui.theme.Purple500
import com.novacartografia.kanbanprojectmanagement.ui.theme.Red100
import com.novacartografia.kanbanprojectmanagement.ui.theme.Red400
import com.novacartografia.kanbanprojectmanagement.ui.theme.Red500
import com.novacartografia.kanbanprojectmanagement.ui.theme.Teal500
import com.novacartografia.kanbanprojectmanagement.ui.theme.White

@SuppressLint("ViewModelConstructorInComposable", "Lifecycle")
@Composable
fun MapView(
    modifier: Modifier,
    employeesVM: EmployeeViewModel = viewModel(),
    projectVM: ProjectViewModel = viewModel(),
    projectLocationsVM: ProjectLocationViewModel = viewModel()
) {
    val context = LocalContext.current
    var mapView by remember { mutableStateOf<MapView?>(null) }
    val employeeList = employeesVM.employees.observeAsState(initial = emptyList())
    val projectList = projectVM.projects.observeAsState(initial = emptyList())
    val projectLocationList = projectLocationsVM.projectLocations.observeAsState(initial = emptyList())

    // Observar estados de carga de los ViewModels
    val employeeLoading = employeesVM.loading.observeAsState(initial = true)
    val projectLoading = projectVM.loading.observeAsState(initial = true)
    val locationLoading = projectLocationsVM.loading.observeAsState(initial = true)

    // Estado local de carga
    val loading = remember { mutableStateOf(true) }

    var selectedEmployee by remember { mutableStateOf<Employee?>(null) }
    var showEmployeeInfo by remember { mutableStateOf(false) }

    var selectedProject by remember { mutableStateOf<Project?>(null) }
    var showProjectInfo by remember { mutableStateOf(false) }


    LaunchedEffect(employeeLoading.value, projectLoading.value, locationLoading.value) {
        loading.value = employeeLoading.value || projectLoading.value || locationLoading.value
    }

    // Cargar datos al iniciar
    LaunchedEffect(Unit) {
        employeesVM.getEmployees()
        projectVM.getProjects()
        projectLocationsVM.getProjectLocations()
    }

    // Depurar información
    LaunchedEffect(projectLocationList.value) {
        //Log.d("MapView", "Ubicaciones de proyectos cargadas: ${projectLocationList.value.size}")
        projectLocationList.value.forEach { location ->
            //Log.d("MapView", "ProjectLocation: ID=${location.id}, ProjectID=${location.projectId}, Lat=${location.latitude}, Lng=${location.longitude}")
        }
    }

    // Coordenadas iniciales (España)
    val initialPosition = remember { Point.fromLngLat(-3.7033, 40.5167) }
    val initialZoom = remember { 6.0 }

    Box(modifier = modifier.fillMaxSize()) {
        // Mapbox MapView en un AndroidView
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                val themeContext = ContextThemeWrapper(ctx, R.style.Theme_KanbanMapCompat)
                MapView(themeContext).apply {
                    mapView = this
                    mapboxMap.loadStyleUri(Style.MAPBOX_STREETS)

                    // Configurar la cámara inicial
                    mapboxMap.setCamera(
                        CameraOptions.Builder()
                            .center(initialPosition)
                            .zoom(initialZoom)
                            .build()
                    )
                }
            },
            update = { view ->
                // Actualizar marcadores cuando cambia la lista de empleados o proyectos
                if (!loading.value) {
                    // Separar la creación de marcadores en dos bucles independientes
                    try {
                        // Crear gestor de anotaciones si no existe
                        val annotationManager = view.annotations.createPointAnnotationManager()

                        // Dentro de la función update del AndroidView, después de crear el annotationManager:

// Añadir listener de clic a los marcadores
                        // Modificar el listener de clics para detectar proyectos
                        annotationManager.addClickListener { annotation ->
                            try {
                                // Obtener los datos del marcador
                                val data = annotation.getData()?.asString

                                if (data != null) {
                                    // Comprobar si es un marcador de proyecto
                                    if (data.startsWith("project_")) {
                                        val projectId = data.substringAfter("project_").toIntOrNull()
                                        if (projectId != null) {
                                            // Buscar el proyecto por ID
                                            val project = projectList.value.find { it.id == projectId }
                                            if (project != null) {
                                                selectedProject = project
                                                showProjectInfo = true
                                                showEmployeeInfo = false  // Ocultar info de empleado si estaba visible
                                                Log.d("MapView", "Proyecto seleccionado: ${project.name}")
                                            }
                                        }
                                    } else {
                                        // Es un marcador de empleado
                                        val employeeId = data.toIntOrNull()
                                        if (employeeId != null) {
                                            val employee = employeeList.value.find { it.id == employeeId }
                                            if (employee != null) {
                                                selectedEmployee = employee
                                                showEmployeeInfo = true
                                                showProjectInfo = false  // Ocultar info de proyecto si estaba visible
                                                Log.d("MapView", "Empleado seleccionado: ${employee.name}")
                                            }
                                        }
                                    }
                                } else {
                                    // Compatibilidad con marcadores antiguos
                                    val point = annotation.point
                                    val nearbyEmployee = employeeList.value.find { employee ->
                                        employee.latitude != null && employee.longitude != null &&
                                                Math.abs(employee.latitude!! - point.latitude()) < 0.01 &&
                                                Math.abs(employee.longitude!! - point.longitude()) < 0.01
                                    }

                                    if (nearbyEmployee != null) {
                                        selectedEmployee = nearbyEmployee
                                        showEmployeeInfo = true
                                        showProjectInfo = false
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("MapView", "Error al procesar clic en marcador: ${e.message}")
                            }
                            true  // Indica que el evento ha sido consumido
                        }

                        // Eliminar marcadores existentes
                        annotationManager.deleteAll()

                        // Variable para contar marcadores creados
                        var markersCreated = 0

                        // 1. AÑADIR MARCADORES DE EMPLEADOS (independiente de proyectos)
                        // Solo mostrar empleados SIN proyecto asignado
                        employeeList.value.forEach { employee ->
                            // Solo procesar si el empleado NO tiene proyecto asignado
                            if (employee.project_id == null) {
                                employee.latitude?.let { lat ->
                                    employee.longitude?.let { lng ->
                                        if (lat != 0.0 && lng != 0.0) {
                                            try {
                                                val point = Point.fromLngLat(lng, lat)

                                                // Convertir el drawable a bitmap para empleados sin proyecto
                                                val drawable = ResourcesCompat.getDrawable(context.resources, R.drawable.custom_marker, null)

                                                // Tinte verde para empleados sin proyecto
                                                drawable?.setTint(Orange500.toArgb())

                                                val bitmap = if (drawable != null) {
                                                    val bitmap = createBitmap(48, 48)
                                                    val canvas = Canvas(bitmap)
                                                    drawable.setBounds(0, 0, canvas.width, canvas.height)
                                                    drawable.draw(canvas)
                                                    bitmap
                                                } else {
                                                    null
                                                }

                                                if (bitmap != null) {
                                                    val pointAnnotationOptions = PointAnnotationOptions()
                                                        .withPoint(point)
                                                        .withIconImage(bitmap)
                                                        .withIconSize(1.0)
                                                        .withData(JsonPrimitive(employee.id.toString()))  // Almacenar ID para el click

                                                    annotationManager.create(pointAnnotationOptions)
                                                    markersCreated++
                                                    Log.d("MapView", "Marcador de empleado SIN PROYECTO creado para ${employee.name} en $lat, $lng")
                                                }
                                            } catch (e: Exception) {
                                                Log.e("MapView", "Error al crear marcador para ${employee.name}: ${e.message}")
                                                e.printStackTrace()
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 2. AÑADIR MARCADORES DE PROYECTOS (completamente separado)
                        projectList.value.forEach { project ->
                            // Mapa para agrupar empleados por proyecto
                            val employeesByProject = mutableMapOf<Int?, MutableList<Employee>>()

                            // Agrupar empleados por proyecto
                            employeeList.value.forEach { employee ->
                                if (employee.project_id != null) {
                                    val projectEmployees = employeesByProject.getOrPut(employee.project_id) { mutableListOf() }
                                    projectEmployees.add(employee)
                                }
                            }

                            // Para cada proyecto, colocar sus empleados en un patrón circular
                            project.id?.let { projectId ->
                                val employees = employeesByProject[projectId] ?: emptyList()
                                val projectLocation = projectLocationList.value.find { it.projectId == projectId }

                                if (projectLocation != null && projectLocation.latitude != 0.0 && projectLocation.longitude != 0.0) {
                                    // Datos del proyecto para posicionar empleados
                                    val centerLat = projectLocation.latitude
                                    val centerLng = projectLocation.longitude

                                    // Distribuir empleados alrededor del proyecto
                                    employees.forEachIndexed { index, employee ->
                                        try {
                                            // Crear un patrón circular alrededor del proyecto
                                            // Radio base en grados (aproximadamente 100-200 metros)
                                            // Radio base en grados (aproximadamente 300-400 metros)
                                            val baseRadius = 0.004  // Aumentar de 0.002 a 0.004

// Calcular ángulo para distribución circular (en radianes)
                                            val angle = 2 * Math.PI * index / (employees.size.coerceAtLeast(1))

// Calcular desplazamiento
                                            val offsetLat = baseRadius * Math.sin(angle)
                                            val offsetLng = baseRadius * Math.cos(angle)

// Aplicar desplazamiento
                                            val empLat = centerLat + offsetLat
                                            val empLng = centerLng + offsetLng



                                            val point = Point.fromLngLat(empLng, empLat)

                                            // Convertir el drawable a bitmap para empleados
                                            val drawableEmp = ResourcesCompat.getDrawable(context.resources, R.drawable.custom_marker, null)

                                            // Buscar el proyecto del empleado
                                            val projectEmp = projectList.value.find { it.id == employee.project_id }

                                            if (projectEmp != null) {
                                                drawableEmp?.setTint(
                                                    checkColorMarkerByProject(
                                                        employee,
                                                        province = projectEmp.state ?: ""
                                                    )
                                                )
                                            } else {
                                                drawableEmp?.setTint(Green700.toArgb())
                                            }

                                            val bitmap = if (drawableEmp != null) {
                                                val bitmap = createBitmap(48, 48)
                                                val canvas = Canvas(bitmap)
                                                drawableEmp.setBounds(0, 0, canvas.width, canvas.height)
                                                drawableEmp.draw(canvas)
                                                bitmap
                                            } else {
                                                null
                                            }

                                            if (bitmap != null) {
                                                val pointAnnotationOptions = PointAnnotationOptions()
                                                    .withPoint(point)
                                                    .withIconImage(bitmap)
                                                    .withIconSize(1.0)
                                                    .withData(JsonPrimitive(employee.id.toString()))

                                                // Después de crear el marcador del empleado, añadir una línea al proyecto
                                                val lineManager = view.annotations.createPolylineAnnotationManager()
                                                val linePoints = listOf(
                                                    Point.fromLngLat(centerLng, centerLat), // Ubicación del proyecto
                                                    Point.fromLngLat(empLng, empLat)        // Ubicación del empleado
                                                )
                                                 val lineColor = if(project.type == "external") {
                                                    Purple500.toArgb()
                                                 }else{
                                                    Green500.toArgb()
                                                 }
                                                val lineOptions = PolylineAnnotationOptions()
                                                    .withPoints(linePoints)
                                                    .withLineColor(lineColor) // Usar el mismo color que el marcador
                                                    .withLineWidth(1.5)       // Línea fina pero visible
                                                    .withLineOpacity(0.6)     // Semi-transparente

                                                lineManager.create(lineOptions)

                                                annotationManager.create(pointAnnotationOptions)
                                                markersCreated++
                                                Log.d("MapView", "Marcador de empleado creado para ${employee.name} en $empLat, $empLng (pertenece a proyecto ${project.name})")
                                            }
                                        } catch (e: Exception) {
                                            Log.e("MapView", "Error al crear marcador para ${employee.name}: ${e.message}")
                                            e.printStackTrace()
                                        }
                                    }
                                } else {
                                    // Si el proyecto no tiene ubicación, usar las ubicaciones originales de los empleados
                                    employees.forEach { employee ->
                                        employee.latitude?.let { lat ->
                                            employee.longitude?.let { lng ->
                                                if (lat != 0.0 && lng != 0.0) {
                                                    try {
                                                        val point = Point.fromLngLat(lng, lat)

                                                        val drawableEmp = ResourcesCompat.getDrawable(context.resources, R.drawable.custom_marker, null)

                                                        val projectEmp = projectList.value.find { it.id == employee.project_id }

                                                        if (projectEmp != null) {
                                                            drawableEmp?.setTint(
                                                                checkColorMarkerByProject(
                                                                    employee,
                                                                    province = projectEmp.state ?: ""
                                                                )
                                                            )
                                                        } else {
                                                            drawableEmp?.setTint(Green700.toArgb())
                                                        }

                                                        val bitmap = if (drawableEmp != null) {
                                                            val bitmap = createBitmap(48, 48)
                                                            val canvas = Canvas(bitmap)
                                                            drawableEmp.setBounds(0, 0, canvas.width, canvas.height)
                                                            drawableEmp.draw(canvas)
                                                            bitmap
                                                        } else {
                                                            null
                                                        }

                                                        if (bitmap != null) {
                                                            val pointAnnotationOptions = PointAnnotationOptions()
                                                                .withPoint(point)
                                                                .withIconImage(bitmap)
                                                                .withIconSize(1.0)
                                                                .withData(JsonPrimitive(employee.id.toString()))

                                                            annotationManager.create(pointAnnotationOptions)
                                                            markersCreated++
                                                            Log.d("MapView", "Marcador de empleado creado en su ubicación original: ${employee.name}")
                                                        }
                                                    } catch (e: Exception) {
                                                        Log.e("MapView", "Error al crear marcador para ${employee.name}: ${e.message}")
                                                        e.printStackTrace()
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Dentro del bloque update del AndroidView, después de los marcadores de empleados
// pero antes del final del bloque try

// 3. AÑADIR MARCADORES DE PROYECTOS
                        projectLocationList.value.forEach { projectLocation ->
                            try {
                                // Verificar que las coordenadas son válidas
                                if (projectLocation.latitude != 0.0 && projectLocation.longitude != 0.0) {
                                    val point = Point.fromLngLat(projectLocation.longitude, projectLocation.latitude)

                                    // Buscar información del proyecto
                                    val project = projectList.value.find { it.id == projectLocation.projectId }

                                    // Crear icono para el proyecto
                                    val drawable = ResourcesCompat.getDrawable(context.resources, R.drawable.euro, null)

                                    // Aplicar tinte según el estado del proyecto o su provincia
                                    val tintColor = if (project != null) {
                                        when (project.type) {
                                            "external" -> Purple500.toArgb()
                                            "project" -> Lime500.toArgb()
                                            else -> Red400.toArgb()
                                        }
                                    } else {
                                        Red400.toArgb() // Color por defecto
                                    }

                                    drawable?.setTint(tintColor)

                                    // Convertir drawable a bitmap
                                    val bitmap = if (drawable != null) {
                                        val bitmap = createBitmap(64, 64) // Tamaño más grande para destacar
                                        val canvas = Canvas(bitmap)
                                        drawable.setBounds(0, 0, canvas.width, canvas.height)
                                        drawable.draw(canvas)
                                        bitmap
                                    } else {
                                        null
                                    }

                                    if (bitmap != null) {
                                        val pointAnnotationOptions = PointAnnotationOptions()
                                            .withPoint(point)
                                            .withIconImage(bitmap)
                                            .withIconSize(0.8) // Tamaño más grande para destacar
                                            .withData(JsonPrimitive("project_${projectLocation.projectId}"))

                                        annotationManager.create(pointAnnotationOptions)
                                        markersCreated++

                                        val projectName = project?.name ?: "Desconocido"
                                        Log.d("MapView", "Marcador de PROYECTO creado: $projectName en ${projectLocation.latitude}, ${projectLocation.longitude}")
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("MapView", "Error al crear marcador de proyecto: ${e.message}")
                                e.printStackTrace()
                            }
                        }

                        //Log.d("MapView", "Total marcadores creados: $markersCreated")
                    } catch (e: Exception) {
                        Log.e("MapView", "Error al actualizar marcadores: ${e.message}")
                        e.printStackTrace()
                    }
                }
            }
        )

        // Controles de mapa superpuestos
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Botones de zoom
            MapControlButton(
                icon = Icons.Default.KeyboardArrowUp,
                description = "Acercar",
                onClick = {
                    mapView?.mapboxMap?.let { map ->
                        val currentZoom = map.cameraState.zoom
                        map.setCamera(
                            CameraOptions.Builder().zoom(currentZoom + 1.0).build()
                        )
                    }
                }
            )

            MapControlButton(
                icon = Icons.Default.ArrowDropDown,
                description = "Alejar",
                onClick = {
                    mapView?.mapboxMap?.let { map ->
                        val currentZoom = map.cameraState.zoom
                        map.setCamera(
                            CameraOptions.Builder().zoom(currentZoom - 1.0).build()
                        )
                    }
                }
            )

            MapControlButton(
                icon = Icons.Default.LocationOn,
                description = "Volver al centro",
                onClick = {
                    mapView?.mapboxMap?.setCamera(
                        CameraOptions.Builder()
                            .center(initialPosition)
                            .zoom(initialZoom)
                            .build()
                    )
                }
            )
        }

        // Indicador de carga
        if (loading.value) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(50.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Agregar al final del Box principal, justo antes del cierre
        // En el mismo Box donde está la tarjeta de empleado, añade una condición adicional
        if (showEmployeeInfo && selectedEmployee != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Column {
                    EmployeeInfoCard(
                        employee = selectedEmployee!!,
                        projectList = projectList.value
                    )

                    // Botón para cerrar la información
                    TextButton(
                        onClick = { showEmployeeInfo = false },
                        modifier = Modifier.align(Alignment.End),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Purple500
                        )
                    ) {
                        Text("Cerrar")
                    }
                }
            }
        } else if (showProjectInfo && selectedProject != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Column {
                    ProjectInfoCard(
                        project = selectedProject!!,
                        employeeList = employeeList.value
                    )

                    // Botón para cerrar la información
                    TextButton(
                        onClick = { showProjectInfo = false },
                        modifier = Modifier.align(Alignment.End),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Purple500
                        )
                    ) {
                        Text("Cerrar")
                    }
                }
            }
        }
    }

    // Efecto para limpiar recursos al salir
    DisposableEffect(Unit) {
        onDispose {
            mapView?.onDestroy()
        }
    }
}

@Composable
private fun MapControlButton(
    icon: ImageVector,
    description: String,
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier.size(48.dp),
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun EmployeeInfoCard(employee: Employee, projectList: List<Project>) {
    val employeeProject = projectList.find { it.id == employee.project_id }

    Card(
        modifier = Modifier
            .width(400.dp)
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Encabezado con avatar y nombre
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Avatar circular
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Nombre con estilo destacado
                Text(
                    text = employee.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(8.dp))

            // Información de ubicación
            Text(
                text = "Ubicación",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )

            // Ciudad
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Ciudad",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = employee.city ?: "N/A",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Provincia
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Build,
                    contentDescription = "Provincia",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = employee.state ?: "N/A",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(8.dp))

            // Proyecto
            Text(
                text = "Información de proyecto",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )

            // Estado del proyecto
            if (employeeProject != null) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = "Proyecto",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = employeeProject.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            if (employeeProject.description != null) {
                                Text(
                                    text = employeeProject.description!!,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            } else {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Sin proyecto",
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Sin proyecto asignado",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            // Número de contacto (si existe)
            if (employee.driver_license != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Contacto",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Teléfono",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "${employee.driver_license}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@Composable
private fun ProjectInfoCard(project: Project, employeeList: List<Employee>) {
    val projectEmployees = employeeList.filter { it.project_id == project.id }

    Card(
        modifier = Modifier
            .width(400.dp)
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState()), // Añadir scroll vertical
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Encabezado con icono y nombre
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Icono circular para el proyecto
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            when (project.type) {
                                "external" -> Purple500
                                "project" -> Lime500
                                else -> Red400
                            },
                            CircleShape
                        )
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = null,
                        tint = White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Nombre con estilo destacado
                Text(
                    text = project.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(8.dp))

            // Descripción del proyecto
            if (project.description != null) {
                Text(
                    text = "Descripción",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = project.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Ubicación
            Text(
                text = "Ubicación",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Ubicación",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${project.state ?: "N/A"}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Empleados asignados (${projectEmployees.size})",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )

            if (projectEmployees.isNotEmpty()) {
                // Mostrar todos los empleados
                projectEmployees.forEach { employee ->
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Empleado",
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = employee.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            } else {
                Text(
                    text = "No hay empleados asignados a este proyecto",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Gray500,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

private fun checkColorMarkerByProject(employee: Employee, province: String?): Int {
    return when {
        employee.state == province -> Blue500.toArgb()
        employee.state == "" -> Green700.toArgb()
        employee.state != province -> Red500.toArgb()
        else -> Gray500.toArgb()
    }
}