package com.biblioteca.app.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.biblioteca.app.data.model.Libro
import com.biblioteca.app.data.repository.LibrosRepository
import com.biblioteca.app.data.repository.PreferenciasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class LibrosViewModel(context: Context? = null) : ViewModel() {
    
    private val repositorio = LibrosRepository()
    private val preferencias: PreferenciasRepository? = context?.let { PreferenciasRepository(it) }
    
    private val _libros = MutableStateFlow<List<Libro>>(emptyList())
    val libros: StateFlow<List<Libro>> = _libros
    
    private val _librosFiltrados = MutableStateFlow<List<Libro>>(emptyList())
    open val librosFiltrados: StateFlow<List<Libro>> = _librosFiltrados
    
    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda
    
    private val _esAdmin = MutableStateFlow(false)
    val esAdmin: StateFlow<Boolean> = _esAdmin
    
    private val _operacionExitosa = MutableStateFlow<String?>(null)
    val operacionExitosa: StateFlow<String?> = _operacionExitosa
    
    init {
        verificarRol()
        cargarLibros()
    }
    
    private fun verificarRol() {
        _esAdmin.value = preferencias?.esAdmin() ?: false
    }
    
    fun cargarLibros() {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            try {
                val lista = repositorio.obtenerLibros()
                _libros.value = lista
                _librosFiltrados.value = lista
            } catch (e: Exception) {
                _error.value = "no se pudieron cargar los libros"
            } finally {
                _cargando.value = false
            }
        }
    }
    
    fun buscar(texto: String) {
        _busqueda.value = texto
        filtrar()
    }
    
    private fun filtrar() {
        val texto = _busqueda.value.lowercase()
        _librosFiltrados.value = if (texto.isEmpty()) {
            _libros.value
        } else {
            _libros.value.filter { libro ->
                libro.titulo.lowercase().contains(texto) ||
                libro.descripcion.lowercase().contains(texto)
            }
        }
    }
    
    fun crearLibro(titulo: String, descripcion: String, imagen: String, precio: Double) {
        if (!_esAdmin.value) {
            _error.value = "no tienes permisos para crear libros"
            return
        }
        
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            try {
                val libro = Libro(
                    titulo = titulo,
                    descripcion = descripcion,
                    imagen = imagen,
                    precio = precio
                )
                repositorio.crearLibro(libro)
                _operacionExitosa.value = "libro creado correctamente"
                cargarLibros()
            } catch (e: Exception) {
                _error.value = "error al crear el libro"
            } finally {
                _cargando.value = false
            }
        }
    }
    
    fun actualizarLibro(id: String, titulo: String, descripcion: String, imagen: String, precio: Double) {
        if (!_esAdmin.value) {
            _error.value = "no tienes permisos para editar libros"
            return
        }
        
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            try {
                val libro = Libro(
                    id = id,
                    titulo = titulo,
                    descripcion = descripcion,
                    imagen = imagen,
                    precio = precio
                )
                repositorio.actualizarLibro(id, libro)
                _operacionExitosa.value = "libro actualizado correctamente"
                cargarLibros()
            } catch (e: Exception) {
                _error.value = "error al actualizar el libro"
            } finally {
                _cargando.value = false
            }
        }
    }
    
    fun eliminarLibro(id: String) {
        if (!_esAdmin.value) {
            _error.value = "no tienes permisos para eliminar libros"
            return
        }
        
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            try {
                repositorio.eliminarLibro(id)
                _operacionExitosa.value = "libro eliminado correctamente"
                cargarLibros()
            } catch (e: Exception) {
                _error.value = "error al eliminar el libro"
            } finally {
                _cargando.value = false
            }
        }
    }
    
    fun limpiarMensajes() {
        _error.value = null
        _operacionExitosa.value = null
    }
}