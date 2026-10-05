package com.sanchez.modaapp.data

import com.sanchez.modaapp.model.ItemCarrito
import com.sanchez.modaapp.model.Ropa

object Carrito {

    private val items = mutableListOf<ItemCarrito>()

    fun agregar(ropa: Ropa, cantidad: Int) {
        val existente = items.find { it.ropa.id == ropa.id }
        if (existente != null) {
            val nuevaCantidad = existente.cantidad + cantidad
            existente.cantidad = if (nuevaCantidad <= ropa.cantidad) nuevaCantidad else ropa.cantidad
        } else {
            items.add(ItemCarrito(ropa, cantidad))
        }
    }

    fun quitar(idRopa: Int) {
        items.removeAll { it.ropa.id == idRopa }
    }

    fun limpiar() {
        items.clear()
    }

    fun obtenerItems(): List<ItemCarrito> = items.toList()

    fun calcularTotal(): Double = items.sumOf { it.subtotal }

    fun contarItems(): Int = items.sumOf { it.cantidad }
}
