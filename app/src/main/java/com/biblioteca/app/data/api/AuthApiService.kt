package com.biblioteca.app.data.api

import com.biblioteca.app.data.model.RespuestaAuth
import com.biblioteca.app.data.model.SolicitudLogin
import com.biblioteca.app.data.model.SolicitudRegistro
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApiService {
    
    @POST("api/auth/login")
    suspend fun login(@Body solicitud: SolicitudLogin): Response<RespuestaAuth>
    
    @POST("api/auth/registro")
    suspend fun registro(@Body solicitud: SolicitudRegistro): Response<RespuestaAuth>
    
    @GET("api/auth/verificar")
    suspend fun verificarToken(@Header("Authorization") token: String): Response<Any>
}
