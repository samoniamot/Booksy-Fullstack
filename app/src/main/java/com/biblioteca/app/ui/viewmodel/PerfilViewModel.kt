package com.biblioteca.app.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.biblioteca.app.data.repository.PreferenciasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PerfilViewModel(private val contexto: Context) : ViewModel() {
    private val preferencias = PreferenciasRepository(contexto)
    
    private val _nombre = MutableStateFlow("")
    val nombre: StateFlow<String> = _nombre
    
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email
    
    private val _rol = MutableStateFlow("")
    val rol: StateFlow<String> = _rol
    
    private val _imagenUri = MutableStateFlow<String?>(null)
    val imagenUri: StateFlow<String?> = _imagenUri
    
    private val _esAdmin = MutableStateFlow(false)
    val esAdmin: StateFlow<Boolean> = _esAdmin
    
    init {
        cargarPerfil()
        cargarImagenGuardada()
    }
    
    private fun cargarImagenGuardada() {
        val uriGuardado = preferencias.obtenerImagenPerfil()
        if (uriGuardado != null) {
            _imagenUri.value = uriGuardado
        }
    }
    
    fun cargarPerfil() {
        _nombre.value = preferencias.obtenerNombre() ?: ""
        _email.value = preferencias.obtenerEmail() ?: ""
        _rol.value = preferencias.obtenerRol() ?: "user"
        _esAdmin.value = preferencias.esAdmin()
    }
    
    fun guardarImagen(uri: Uri) {
        _imagenUri.value = uri.toString()
        preferencias.guardarImagenPerfil(uri.toString())
    }
    
    fun cerrarSesion(onExito: () -> Unit) {
        preferencias.cerrarSesion()
        onExito()
    }
}
