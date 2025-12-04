# Booksy Fullstack - App de Gestión de Libros

Booksy es una aplicación fullstack para estudiantes y profesores que quieren administrar sus libros de estudio. Es una biblioteca digital con autenticación JWT y roles.

## Stack Tecnológico

### Frontend (Android)
- Kotlin
- Jetpack Compose para la interfaz
- SharedPreferences para persistencia local (token y foto de perfil)
- Retrofit para conexiones HTTP con la API
- Navigation Compose para navegación entre pantallas
- Coil para carga de imágenes

### Backend (Spring Boot)
- Java 17 + Spring Boot 3.2.0
- MongoDB como base de datos
- JWT para autenticación
- Spring Security para control de acceso por roles
- Swagger/OpenAPI para documentación

## Enlaces de la API

| Recurso | URL |
|---------|-----|
| **Swagger UI** | https://booksy-backend-fullstack.onrender.com/swagger-ui.html |
| **API Docs** | https://booksy-backend-fullstack.onrender.com/v3/api-docs |
| **Base URL** | https://booksy-backend-fullstack.onrender.com |

## Endpoints de la API

### Autenticación (`/api/auth`)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/auth/login` | Iniciar sesión (devuelve JWT) |
| POST | `/api/auth/registro` | Registrar nuevo usuario |
| GET | `/api/auth/verificar` | Verificar token válido |

### Libros (`/api/libros`)
| Método | Endpoint | Acceso | Descripción |
|--------|----------|--------|-------------|
| GET | `/api/libros` | Público | Listar todos los libros |
| GET | `/api/libros/{id}` | Público | Obtener libro por ID |
| GET | `/api/libros/buscar?titulo=` | Público | Buscar por título |
| POST | `/api/libros` | Solo ADMIN | Crear libro |
| PUT | `/api/libros/{id}` | Solo ADMIN | Actualizar libro |
| DELETE | `/api/libros/{id}` | Solo ADMIN | Eliminar libro |

## Credenciales de Prueba

| Rol | Email | Password |
|-----|-------|----------|
| Admin | `admin@booksy.com` | `admin123` |
| Usuario | `usuario@booksy.com` | `usuario123` |

## Arquitectura

### MVVM (Model-View-ViewModel)
- **View**: Pantallas Composable que observan estados con `collectAsState()`
- **ViewModel**: Maneja lógica de negocio y estados con `StateFlow`
- **Model**: Entidades y repositorios

### Flujo de la App
1. Login/Registro → obtener token JWT
2. Token se guarda en SharedPreferences
3. Ver catálogo de libros (público)
4. Si es admin: puede crear/editar/eliminar libros
5. Perfil con foto desde galería

## Funcionalidades

- Login y registro con validaciones (contraseña mínimo 6 caracteres)
- Catálogo de libros con búsqueda
- CRUD completo de libros (solo admin)
- Pantalla de perfil con foto desde galería
- Animaciones con AnimatedVisibility
- Persistencia local con SharedPreferences
- Control de acceso por roles (admin/user)

## Estructura del Proyecto

```
biblioteca-android/
├── app/src/main/java/com/biblioteca/app/
│   ├── data/
│   │   ├── api/           # Retrofit services
│   │   ├── model/         # Data classes
│   │   └── repository/    # Repositorios
│   ├── ui/
│   │   ├── screens/       # Pantallas Composable
│   │   ├── viewmodel/     # ViewModels
│   │   ├── navigation/    # Navegación
│   │   └── theme/         # Tema y estilos
│   └── MainActivity.kt

booksy-backend/
├── src/main/java/com/booksy/
│   ├── controller/        # REST Controllers
│   ├── model/             # Entidades MongoDB
│   ├── repository/        # Repositorios
│   ├── service/           # Servicios
│   ├── security/          # JWT y Spring Security
│   └── config/            # Configuraciones
```

## Probar la API con cURL

```bash
# Login
curl -X POST "https://booksy-backend-fullstack.onrender.com/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@booksy.com","password":"admin123"}'

# Obtener libros (público)
curl "https://booksy-backend-fullstack.onrender.com/api/libros"

# Crear libro (requiere token de admin)
curl -X POST "https://booksy-backend-fullstack.onrender.com/api/libros" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TU_TOKEN>" \
  -d '{"titulo":"Nuevo Libro","descripcion":"Descripción","imagen":"url","precio":9990}'
```

## Permisos Android

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
```

## Repositorios

- **Frontend Android**: Este repositorio
- **Backend Spring Boot**: Desplegado en Render
- **Base de datos**: MongoDB Atlas
