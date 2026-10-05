package com.sanchez.modaapp.model

import java.io.Serializable

data class Usuario(
    val id: Int = 0,
    val usuario: String,
    val clave: String,
    val rol: String,
    val telefono: String
) : Serializable

data class Categoria(
    val id: Int = 0,
    val nombre: String
) : Serializable

data class Ropa(
    val id: Int = 0,
    val modelo: String,
    val idCategoria: Int,
    val talla: String,
    val marca: String,
    val color: String,
    val precio: Double,
    val cantidad: Int,
    val foto: String = "",
    val categoriaNombre: String = ""
) : Serializable
