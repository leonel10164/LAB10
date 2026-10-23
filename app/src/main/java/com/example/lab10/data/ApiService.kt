package com.example.lab10.data

import retrofit2.Response
import retrofit2.http.*

data class Serie(
    val id: Int? = null,
    val name: String,
    val release_date: String,
    val rating: Int,
    val category: String
)

data class Espacio(
    val id: Int? = null,
    val nombre: String,
    val ubicacion: String,
    val aforo_maximo: Int,
    val estado: String = "DISPONIBLE"
)

data class Credenciales(val username: String, val password: String)
data class Token(val token: String)
interface Api {
    @GET("serie")
    suspend fun series(): List<Serie>
    @GET("serie/{id}")
    suspend fun serie(@Path("id") id: Int): Serie
    @POST("serie")
    suspend fun crearSerie(@Body item: Serie): Response<Serie>
    @PUT("serie/{id}")
    suspend fun editarSerie(@Path("id") id: Int, @Body item: Serie): Response<Serie>
    @DELETE("serie/{id}")
    suspend fun borrarSerie(@Path("id") id: Int): Response<Serie>
    @POST("api/login/")
    suspend fun login(@Body body: Credenciales): Token
    @GET("api/espacios/")
    suspend fun espacios(): List<Espacio>
    @GET("api/espacios/{id}/")
    suspend fun espacio(@Path("id") id: Int): Espacio
    @POST("api/espacios/")
    suspend fun crearEspacio(
        @Header("Authorization") token: String,
        @Body item: Espacio
    ): Response<Espacio>

    @PUT("api/espacios/{id}/")
    suspend fun editarEspacio(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body item: Espacio
    ): Response<Espacio>

    @DELETE("api/espacios/{id}/")
    suspend fun borrarEspacio(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Unit>
}

fun <T> verificar(r: Response<T>) {
    if (!r.isSuccessful) error(
        "HTTP ${r.code()}: ${
            r.errorBody()?.string() ?: "Solicitud rechazada"
        }"
    )
}
