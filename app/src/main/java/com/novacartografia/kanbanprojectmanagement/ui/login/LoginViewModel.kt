package com.novacartografia.kanbanprojectmanagement.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novacartografia.kanbanprojectmanagement.models.LoginResponse
import com.novacartografia.kanbanprojectmanagement.models.TokenResponse
import com.novacartografia.kanbanprojectmanagement.repository.AuthRepository
import com.novacartografia.kanbanprojectmanagement.utils.Resource
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<Resource<TokenResponse>>()
    val loginResult: LiveData<Resource<TokenResponse>> = _loginResult

    fun login(email: String, password: String) {
        _loginResult.value = Resource.Loading

        //Validación básica
        if (email.isEmpty() || password.isEmpty()){
            _loginResult.value = Resource.Error("Email y contraseña son obligatorios")
            return
        }

        viewModelScope.launch {
            val result = authRepository.login(email, password)
            _loginResult.value = result
        }
    }
}