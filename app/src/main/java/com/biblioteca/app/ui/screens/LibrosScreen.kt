package com.biblioteca.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.biblioteca.app.data.model.Libro
import com.biblioteca.app.ui.viewmodel.LibrosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibrosScreen(
    onNavegar: (String) -> Unit
) {
    val contexto = LocalContext.current
    val viewModel = remember { LibrosViewModel(contexto) }
    LibrosScreenConViewModel(viewModel = viewModel, onNavegar = onNavegar)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibrosScreenConViewModel(
    viewModel: LibrosViewModel,
    onNavegar: (String) -> Unit = {}
) {
    val librosFiltrados by viewModel.librosFiltrados.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val error by viewModel.error.collectAsState()
    val busqueda by viewModel.busqueda.collectAsState()
    val esAdmin by viewModel.esAdmin.collectAsState()
    val operacionExitosa by viewModel.operacionExitosa.collectAsState()
    
    var mostrarDialogoCrear by remember { mutableStateOf(false) }
    var mostrarDialogoEditar by remember { mutableStateOf(false) }
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }
    var libroSeleccionado by remember { mutableStateOf<Libro?>(null) }
    
    // mostrar snackbar cuando hay operacion exitosa
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(operacionExitosa) {
        operacionExitosa?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.limpiarMensajes()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("catalogo") },
                actions = {
                    IconButton(onClick = { onNavegar("perfil") }) {
                        Icon(Icons.Default.AccountCircle, "perfil")
                    }
                }
            )
        },
        floatingActionButton = {
            if (esAdmin) {
                FloatingActionButton(
                    onClick = { mostrarDialogoCrear = true }
                ) {
                    Icon(Icons.Default.Add, "agregar libro")
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = busqueda,
                onValueChange = { viewModel.buscar(it) },
                label = { Text("buscar libro") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                singleLine = true
            )
            
            if (cargando) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (error != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = error!!,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.cargarLibros() }) {
                            Text("reintentar")
                        }
                    }
                }
            } else if (librosFiltrados.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("no encontramos libros")
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(librosFiltrados) { libro ->
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                AsyncImage(
                                    model = libro.imagen,
                                    contentDescription = libro.titulo,
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = libro.titulo,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = libro.descripcion,
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 2
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "$${libro.precio.toInt()}",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                
                                // botones de admin
                                if (esAdmin) {
                                    Column {
                                        IconButton(onClick = {
                                            libroSeleccionado = libro
                                            mostrarDialogoEditar = true
                                        }) {
                                            Icon(
                                                Icons.Default.Edit,
                                                "editar",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        IconButton(onClick = {
                                            libroSeleccionado = libro
                                            mostrarDialogoEliminar = true
                                        }) {
                                            Icon(
                                                Icons.Default.Delete,
                                                "eliminar",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    // dialogo para crear libro
    if (mostrarDialogoCrear) {
        DialogoLibro(
            titulo = "nuevo libro",
            libro = null,
            onDismiss = { mostrarDialogoCrear = false },
            onConfirmar = { titulo, descripcion, imagen, precio ->
                viewModel.crearLibro(titulo, descripcion, imagen, precio)
                mostrarDialogoCrear = false
            }
        )
    }
    
    // dialogo para editar libro
    if (mostrarDialogoEditar && libroSeleccionado != null) {
        DialogoLibro(
            titulo = "editar libro",
            libro = libroSeleccionado,
            onDismiss = { 
                mostrarDialogoEditar = false
                libroSeleccionado = null
            },
            onConfirmar = { titulo, descripcion, imagen, precio ->
                libroSeleccionado?.id?.let { id ->
                    viewModel.actualizarLibro(id, titulo, descripcion, imagen, precio)
                }
                mostrarDialogoEditar = false
                libroSeleccionado = null
            }
        )
    }
    
    // dialogo para confirmar eliminacion
    if (mostrarDialogoEliminar && libroSeleccionado != null) {
        AlertDialog(
            onDismissRequest = { 
                mostrarDialogoEliminar = false
                libroSeleccionado = null
            },
            title = { Text("eliminar libro") },
            text = { Text("estas seguro de eliminar ${libroSeleccionado?.titulo}") },
            confirmButton = {
                TextButton(
                    onClick = {
                        libroSeleccionado?.id?.let { id ->
                            viewModel.eliminarLibro(id)
                        }
                        mostrarDialogoEliminar = false
                        libroSeleccionado = null
                    }
                ) {
                    Text("eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    mostrarDialogoEliminar = false
                    libroSeleccionado = null
                }) {
                    Text("cancelar")
                }
            }
        )
    }
}

@Composable
fun DialogoLibro(
    titulo: String,
    libro: Libro?,
    onDismiss: () -> Unit,
    onConfirmar: (String, String, String, Double) -> Unit
) {
    var tituloLibro by remember { mutableStateOf(libro?.titulo ?: "") }
    var descripcion by remember { mutableStateOf(libro?.descripcion ?: "") }
    var imagen by remember { mutableStateOf(libro?.imagen ?: "") }
    var precio by remember { mutableStateOf(libro?.precio?.toString() ?: "") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(titulo) },
        text = {
            Column {
                OutlinedTextField(
                    value = tituloLibro,
                    onValueChange = { tituloLibro = it },
                    label = { Text("titulo") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("descripcion") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = imagen,
                    onValueChange = { imagen = it },
                    label = { Text("url de imagen") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = precio,
                    onValueChange = { precio = it },
                    label = { Text("precio") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val precioDouble = precio.toDoubleOrNull() ?: 0.0
                    onConfirmar(tituloLibro, descripcion, imagen, precioDouble)
                },
                enabled = tituloLibro.isNotBlank() && descripcion.isNotBlank()
            ) {
                Text("guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("cancelar")
            }
        }
    )
}
