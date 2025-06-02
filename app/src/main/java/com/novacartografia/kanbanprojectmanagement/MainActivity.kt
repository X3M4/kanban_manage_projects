package com.novacartografia.kanbanprojectmanagement

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.novacartografia.kanbanprojectmanagement.ui.login.LoginView
import com.novacartografia.kanbanprojectmanagement.ui.login.LoginViewModel
import com.novacartografia.kanbanprojectmanagement.ui.theme.KanbanProjectManagementTheme

// Primero necesitamos importar el viewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.novacartografia.kanbanprojectmanagement.api.AuthApiService
import com.novacartografia.kanbanprojectmanagement.api.RetrofitClient
import com.novacartografia.kanbanprojectmanagement.repository.AuthRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        val authApiService = RetrofitClient.authService
        val authRepository = AuthRepository(authApiService)

        setContent {
            KanbanProjectManagementTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Crear ViewModelFactory
                    val factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                                return LoginViewModel(authRepository) as T
                            }
                            throw IllegalArgumentException("Unknown ViewModel class")
                        }
                    }

                    val loginViewModel: LoginViewModel = viewModel(factory = factory)

                    LoginView(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        onLoginSuccess = {
                            // Navegar a otra actividad sin usar composables
                            val intent = Intent(this@MainActivity, BaseActivity::class.java)
                            startActivity(intent)
                            finish() // Opcional: cerrar esta actividad
                        },
                        loginViewModel = loginViewModel
                    )
                }
            }
        }
    }
}