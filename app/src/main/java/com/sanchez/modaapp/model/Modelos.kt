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

// HU-09: Modelo Cliente
data class Cliente(
    val id: Int = 0,
    val telefono: String,
    val nombres: String,
    val apellidos: String,
    val fechaRegistro: String = ""
) : Serializable

// HU-09: Modelo Pedido
data class Pedido(
    val id: Int = 0,
    val idCliente: Int,
    val fecha: String,
    val total: Double,
    val estado: String = "PENDIENTE",
    val fechaAtencion: String? = null,
    val clienteNombres: String = "",
    val clienteApellidos: String = "",
    val clienteTelefono: String = ""
) : Serializable

// HU-09: Modelo Detalle de Pedido
data class DetallePedido(
    val id: Int = 0,
    val idPedido: Int,
    val idRopa: Int,
    val cantidad: Int,
    val precioUnit: Double,
    val subtotal: Double,
    val modelo: String = "",
    val talla: String = "",
    val color: String = "",
    val foto: String = ""
) : Serializable

// HU-08: Item del Carrito de compras
data class ItemCarrito(
    val ropa: Ropa,
    var cantidad: Int
) : Serializable {
    val subtotal: Double
        get() = ropa.precio * cantidad
}
