package com.biblioteca.app.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.biblioteca.app.data.api.LibrosRetrofitClient

class PreferenciasRepository(contexto: Context) {
    private val prefs: SharedPreferences = 
        contexto.getSharedPreferences("biblioteca_prefs", Context.MODE_PRIVATE)
    
    companion object {
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_EMAIL = "user_email"
        private const val KEY_NOMBRE = "user_nombre"
        private const val KEY_ROL = "user_rol"
        private const val KEY_IMAGEN_PERFIL = "imagen_perfil"
    }
    
    fun guardarSesion(token: String, id: String, email: String, nombre: String, rol: String) {
        prefs.edit().apply {
            putString(KEY_TOKEN, token)
            putString(KEY_USER_ID, id)
            putString(KEY_EMAIL, email)
            putString(KEY_NOMBRE, nombre)
            putString(KEY_ROL, rol)
            apply()
        }
        // actualizar el token en el cliente retrofit
        LibrosRetrofitClient.setToken(token)
    }
    
    fun obtenerToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }
    
    fun obtenerUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }
    
    fun obtenerEmail(): String? {
        return prefs.getString(KEY_EMAIL, null)
    }
    
    fun obtenerNombre(): String? {
        return prefs.getString(KEY_NOMBRE, null)
    }
    
    fun obtenerRol(): String? {
        return prefs.getString(KEY_ROL, null)
    }
    
    fun esAdmin(): Boolean {
        return obtenerRol() == "admin"
    }
    
    fun estaLogueado(): Boolean {
        return obtenerToken() != null
    }
    
    fun guardarImagenPerfil(uri: String) {
        prefs.edit().putString(KEY_IMAGEN_PERFIL, uri).apply()
    }
    
    fun obtenerImagenPerfil(): String? {
        return prefs.getString(KEY_IMAGEN_PERFIL, null)
    }
    
    fun cerrarSesion() {
        prefs.edit().clear().apply()
        LibrosRetrofitClient.setToken(null)
    }
    
    // cargar token al iniciar la app
    fun cargarTokenEnCliente() {
        obtenerToken()?.let { token ->
            LibrosRetrofitClient.setToken(token)
        }
    }
}
