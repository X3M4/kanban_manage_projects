package com.novacartografia.kanbanprojectmanagement

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.novacartografia.kanbanprojectmanagement.models.ProjectMovementLine
import com.novacartografia.kanbanprojectmanagement.ui.movements.ProjectMovementLineView
import com.novacartografia.kanbanprojectmanagement.ui.movements.ProjectMovementLineViewModel
import com.novacartografia.kanbanprojectmanagement.ui.theme.KanbanProjectManagementTheme

class ProjectMovementLineActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            KanbanProjectManagementTheme {
                val context = LocalContext.current
                val viewModel = viewModel<ProjectMovementLineViewModel>()

                Scaffold(
                    topBar = {
                        TopAppBar(context)
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    ProjectMovementLineView(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        onMovementClick = { movement ->
                            // Manejar clic en un movimiento (ver detalles o editar)
                            handleMovementClick(movement)
                        },
                        onAddMovementClick = {
                            // Navegar a pantalla de creación de nuevo movimiento
                            // Por ejemplo, se podría abrir un diálogo o navegar a otra vista
                            // Esto se implementaría según la navegación de tu app
                        }
                    )
                }
            }
        }
    }

    private fun handleMovementClick(movement: ProjectMovementLine) {
        // Aquí se manejaría la lógica al hacer clic en un movimiento
        // Por ejemplo, mostrar detalles o abrir un formulario de edición
    }
}