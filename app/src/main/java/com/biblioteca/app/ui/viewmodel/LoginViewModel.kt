package com.biblioteca.app.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.biblioteca.app.data.repository.AuthRepository
import com.biblioteca.app.data.repository.PreferenciasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(context: Context) : ViewModel() {
    
    private val prefsRepo = PreferenciasRepository(context)
    private val authRepo = AuthRepository()
    
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email
    
    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password
    
    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    init {
        // cargar token si existe al iniciar
        prefsRepo.cargarTokenEnCliente()
    }
    
    fun actualizarEmail(nuevoEmail: String) {
        _email.value = nuevoEmail
        _error.value = null
    }
    
    fun actualizarPassword(nuevoPassword: String) {
        _password.value = nuevoPassword
        _error.value = null
    }
    
    fun verificarSesionExistente(): Boolean {
        return prefsRepo.estaLogueado()
    }
    
    fun login(onExito: () -> Unit) {
        if (_email.value.isBlank() || _password.value.isBlank()) {
            _error.value = "completa todos los campos"
            return
        }
        
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            
            val resultado = authRepo.login(_email.value, _password.value)
            
            resultado.onSuccess { respuesta ->
                prefsRepo.guardarSesion(
                    token = respuesta.token,
                    id = respuesta.id,
                    email = respuesta.email,
                    nombre = respuesta.nombre,
                    rol = respuesta.rol
                )
                onExito()
            }.onFailure { e ->
                _error.value = e.message ?: "error al iniciar sesion"
            }
            
            _cargando.value = false
        }
    }
}
