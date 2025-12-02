package com.biblioteca.app.data.model

data class Usuario(
    val id: String,
    val email: String,
    val nombre: String,
    val rol: String
)

data class Libro(
    val id: String? = null,
    val titulo: String,
    val descripcion: String,
    val imagen: String,
    val precio: Double
)

data class SolicitudLogin(
    val email: String,
    val password: String
)

data class SolicitudRegistro(
    val email: String,
    val password: String,
    val nombre: String
)

data class RespuestaAuth(
    val token: String,
    val id: String,
    val email: String,
    val nombre: String,
    val rol: String
)

data class MensajeRespuesta(
    val mensaje: String,
    val exito: Boolean
)
