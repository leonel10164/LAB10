package com.example.lab10.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.lab10.data.Api
import com.example.lab10.data.Credenciales
import com.example.lab10.data.Espacio
import com.example.lab10.data.Serie
import com.example.lab10.data.verificar
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriesApp() {
    val api = remember {
        Retrofit.Builder().baseUrl("http://10.0.2.2:8000/")
            .addConverterFactory(GsonConverterFactory.create()).build().create(Api::class.java)
    }
    val scope = rememberCoroutineScope()
    var tab by remember { mutableStateOf("Inicio") }
    var series by remember { mutableStateOf(emptyList<Serie>()) }
    var espacios by remember { mutableStateOf(emptyList<Espacio>()) }
    var token by remember { mutableStateOf("") }
    var user by remember { mutableStateOf("") };
    var pass by remember { mutableStateOf("") }
    var ocupado by remember { mutableStateOf(false) };
    var mensaje by remember { mutableStateOf("") }
    var formulario by remember { mutableStateOf(false) };
    var id by remember { mutableStateOf<Int?>(null) }
    var nombre by remember { mutableStateOf("") };
    var dato by remember { mutableStateOf("") }
    var numero by remember { mutableStateOf("") };
    var categoria by remember { mutableStateOf("") }
    var eliminar by remember { mutableStateOf<Int?>(null) }
    suspend fun cargar() {
        if (tab == "Series") series =
            api.series().sortedByDescending { it.id } else if (tab == "Espacios") espacios =
            api.espacios().sortedByDescending { it.id }
    }

    fun ejecutar(block: suspend () -> Unit) {
        scope.launch {
            ocupado = true; mensaje = ""; try {
            block()
        } catch (e: Exception) {
            mensaje = e.message ?: "No se pudo conectar con el servidor"
        } finally {
            ocupado = false
        }
        }
    }
    LaunchedEffect(tab) {
        ocupado = true; mensaje = ""; try {
        cargar()
    } catch (e: Exception) {
        mensaje = e.message ?: "Error de conexión"
    } finally {
        ocupado = false
    }
    }
    Scaffold(
        topBar = { TopAppBar(title = { Text("Lab10 · Retrofit CRUD") }) },
        bottomBar = {
            NavigationBar {
                listOf(
                    "Inicio",
                    "Series",
                    "Espacios"
                ).forEach { item ->
                    NavigationBarItem(
                        selected = tab == item,
                        onClick = { tab = item; formulario = false },
                        icon = {
                            Text(
                                when (item) {
                                    "Inicio" -> "⌂";"Series" -> "▶";else -> "▣"
                                }
                            )
                        },
                        label = { Text(item) })
                }
            }
        }) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (ocupado) LinearProgressIndicator(Modifier.fillMaxWidth())
            if (mensaje.isNotEmpty()) Text(mensaje, color = MaterialTheme.colorScheme.primary)
            if (tab == "Inicio") {
                Text(
                    "Consumo de API con Retrofit II",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text("Kotlin + Jetpack Compose + Django REST Framework")
                Card(Modifier.fillMaxWidth()) {
                    Column(
                        Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Parte guiada: Series",
                            style = MaterialTheme.typography.titleLarge
                        ); Text(
                        "Consulta, registra, edita y elimina series conectadas al servicio Django."
                    )
                    }
                }
                Card(Modifier.fillMaxWidth()) {
                    Column(
                        Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Ejercicio: EventFlow",
                            style = MaterialTheme.typography.titleLarge
                        ); Text(
                        "Gestiona los espacios de tu proyecto. Inicia sesión para realizar cambios."
                    )
                    }
                }
                Text("Servidor: http://10.0.2.2:8000/")
            } else {
                Text(
                    if (formulario) {
                        if (id == null) "Nuevo registro" else "Editar registro #$id"
                    } else tab, style = MaterialTheme.typography.headlineMedium
                )
                if (tab == "Espacios" && token.isEmpty()) {
                    OutlinedTextField(
                        user,
                        { user = it },
                        label = { Text("Usuario Django") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        pass,
                        { pass = it },
                        label = { Text("Contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        enabled = !ocupado && user.isNotBlank() && pass.isNotBlank(),
                        onClick = {
                            ejecutar {
                                token = "Token " + api.login(Credenciales(user, pass)).token; pass =
                                ""; mensaje = "Sesión iniciada"
                            }
                        }) { Text("Iniciar sesión") }
                }
                if (formulario) {
                    OutlinedTextField(
                        nombre,
                        { nombre = it },
                        label = { Text(if (tab == "Series") "Nombre de la serie" else "Nombre del espacio") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        dato,
                        { dato = it },
                        label = { Text(if (tab == "Series") "Fecha YYYY-MM-DD" else "Ubicación") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        numero,
                        { numero = it },
                        label = { Text(if (tab == "Series") "Rating entero" else "Aforo máximo") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(if (tab == "Series") "Categoría" else "Estado")
                    (if (tab == "Series") listOf("horror", "comedy", "action", "drama") else listOf(
                        "DISPONIBLE",
                        "OCUPADO",
                        "MANTENIMIENTO"
                    )).forEach { valor ->
                        FilterChip(
                            selected = categoria == valor,
                            onClick = { categoria = valor },
                            label = { Text(valor) })
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(enabled = !ocupado, onClick = {
                            ejecutar {
                                require(nombre.trim().length >= 3 && dato.isNotBlank()) { "Completa nombre y ${if (tab == "Series") "fecha" else "ubicación"}." }
                                val n =
                                    numero.toIntOrNull() ?: error("Ingresa un número entero válido")
                                if (tab == "Series") {
                                    val formato = SimpleDateFormat(
                                        "yyyy-MM-dd",
                                        Locale.ROOT
                                    ).apply { isLenient = false }
                                    require(Regex("\\d{4}-\\d{2}-\\d{2}").matches(dato) && runCatching {
                                        formato.parse(
                                            dato
                                        )
                                    }.isSuccess) { "Fecha inválida; usa YYYY-MM-DD" }
                                    val s = Serie(id, nombre.trim(), dato, n, categoria)
                                    verificar(
                                        if (id == null) api.crearSerie(s) else api.editarSerie(
                                            id!!,
                                            s
                                        )
                                    )
                                } else {
                                    require(n > 0) { "El aforo debe ser mayor que cero" }; require(
                                        token.isNotEmpty()
                                    ) { "Inicia sesión" }
                                    val s = Espacio(id, nombre.trim(), dato.trim(), n, categoria)
                                    verificar(
                                        if (id == null) api.crearEspacio(
                                            token,
                                            s
                                        ) else api.editarEspacio(token, id!!, s)
                                    )
                                }
                                formulario = false; cargar(); mensaje =
                                "Registro guardado correctamente"
                            }
                        }) { Text("Guardar") }
                        OutlinedButton(enabled = !ocupado, onClick = { formulario = false }) {
                            Text(
                                "Cancelar"
                            )
                        }
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            enabled = !ocupado && (tab == "Series" || token.isNotEmpty()),
                            onClick = {
                                id = null; nombre = ""; dato = ""; numero = ""; categoria =
                                if (tab == "Series") "drama" else "DISPONIBLE"; formulario = true
                            }) { Text("+ Nuevo") }
                        OutlinedButton(
                            enabled = !ocupado,
                            onClick = { ejecutar { cargar() } }) { Text("Actualizar") }
                    }
                    if ((tab == "Series" && series.isEmpty()) || (tab == "Espacios" && espacios.isEmpty())) Text(
                        "No hay registros para mostrar."
                    )
                    val items = if (tab == "Series") series.map {
                        Triple(
                            it.id!!,
                            it.name,
                            "${it.release_date} · ${it.category} · Rating ${it.rating}"
                        )
                    } else espacios.map {
                        Triple(
                            it.id!!,
                            it.nombre,
                            "${it.ubicacion} · Aforo ${it.aforo_maximo}\n${it.estado}"
                        )
                    }
                    items.forEach { item ->
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp)) {
                                Text(
                                    "#${item.first}  ${item.second}",
                                    style = MaterialTheme.typography.titleMedium
                                ); Text(item.third)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    TextButton(
                                        enabled = !ocupado && (tab == "Series" || token.isNotEmpty()),
                                        onClick = {
                                            ejecutar {
                                                id = item.first
                                                if (tab == "Series") {
                                                    val s = api.serie(item.first); nombre =
                                                        s.name; dato = s.release_date; numero =
                                                        s.rating.toString(); categoria = s.category
                                                } else {
                                                    val s = api.espacio(item.first); nombre =
                                                        s.nombre; dato = s.ubicacion; numero =
                                                        s.aforo_maximo.toString(); categoria =
                                                        s.estado
                                                }
                                                formulario = true
                                            }
                                        }) { Text("Editar") }
                                    TextButton(
                                        enabled = !ocupado && (tab == "Series" || token.isNotEmpty()),
                                        onClick = { eliminar = item.first }) { Text("Eliminar") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    eliminar?.let { clave ->
        AlertDialog(
            onDismissRequest = { if (!ocupado) eliminar = null },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Eliminar el registro #$clave? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    enabled = !ocupado,
                    onClick = {
                        ejecutar {
                            if (tab == "Series") verificar(api.borrarSerie(clave)) else verificar(
                                api.borrarEspacio(token, clave)
                            ); eliminar = null; cargar(); mensaje =
                            "Registro eliminado correctamente"
                        }
                    }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(
                    enabled = !ocupado,
                    onClick = { eliminar = null }) { Text("Cancelar") }
            })
    }
}

