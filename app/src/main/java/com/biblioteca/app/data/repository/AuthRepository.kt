package com.biblioteca.app.data.repository

import com.biblioteca.app.data.api.LibrosRetrofitClient
import com.biblioteca.app.data.model.RespuestaAuth
import com.biblioteca.app.data.model.SolicitudLogin
import com.biblioteca.app.data.model.SolicitudRegistro

class AuthRepository {
    
    private val api = LibrosRetrofitClient.servicioAuth
    
    suspend fun login(email: String, password: String): Result<RespuestaAuth> {
        return try {
            val response = api.login(SolicitudLogin(email, password))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("credenciales incorrectas"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun registro(email: String, password: String, nombre: String): Result<RespuestaAuth> {
        return try {
            val response = api.registro(SolicitudRegistro(email, password, nombre))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("error al registrar usuario"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun verificarToken(token: String): Boolean {
        return try {
            val response = api.verificarToken("Bearer $token")
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}
