package com.sanchez.modaapp.data

import android.content.ContentValues
import android.content.Context
import com.sanchez.modaapp.model.Ropa

class RopaDao(context: Context) {

    private val dbHelper = DBHelper(context)

    // HU-05: Insertar nueva prenda con foto
    fun insertar(ropa: Ropa): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("modelo", ropa.modelo)
            put("id_categoria", ropa.idCategoria)
            put("talla", ropa.talla)
            put("marca", ropa.marca)
            put("color", ropa.color)
            put("precio", ropa.precio)
            put("cantidad", ropa.cantidad)
            put("foto", ropa.foto)
        }
        return db.insert("ropa", null, values)
    }

    // HU-05 CA4: Listar todas las prendas para el Administrador
    fun listar(): List<Ropa> {
        val lista = mutableListOf<Ropa>()
        val db = dbHelper.readableDatabase
        val query = """
            SELECT r.id, r.modelo, r.id_categoria, r.talla, r.marca, r.color, r.precio, r.cantidad, r.foto, c.nombre as categoria_nombre
            FROM ropa r
            INNER JOIN categoria c ON r.id_categoria = c.id
            ORDER BY r.id DESC
        """.trimIndent()
        val cursor = db.rawQuery(query, null)
        cursor.use {
            while (it.moveToNext()) {
                lista.add(
                    Ropa(
                        id = it.getInt(it.getColumnIndexOrThrow("id")),
                        modelo = it.getString(it.getColumnIndexOrThrow("modelo")),
                        idCategoria = it.getInt(it.getColumnIndexOrThrow("id_categoria")),
                        talla = it.getString(it.getColumnIndexOrThrow("talla")),
                        marca = it.getString(it.getColumnIndexOrThrow("marca")),
                        color = it.getString(it.getColumnIndexOrThrow("color")),
                        precio = it.getDouble(it.getColumnIndexOrThrow("precio")),
                        cantidad = it.getInt(it.getColumnIndexOrThrow("cantidad")),
                        foto = it.getString(it.getColumnIndexOrThrow("foto")) ?: "",
                        categoriaNombre = it.getString(it.getColumnIndexOrThrow("categoria_nombre")) ?: ""
                    )
                )
            }
        }
        return lista
    }

    // HU-06: Catálogo disponible (cantidad > 0) con filtro opcional por categoría
    fun listarDisponibles(idCategoria: Int? = null): List<Ropa> {
        val lista = mutableListOf<Ropa>()
        val db = dbHelper.readableDatabase
        val query: String
        val args: Array<String>?
        if (idCategoria != null && idCategoria > 0) {
            query = """
                SELECT r.id, r.modelo, r.id_categoria, r.talla, r.marca, r.color, r.precio, r.cantidad, r.foto, c.nombre as categoria_nombre
                FROM ropa r
                INNER JOIN categoria c ON r.id_categoria = c.id
                WHERE r.cantidad > 0 AND r.id_categoria = ?
                ORDER BY r.id DESC
            """.trimIndent()
            args = arrayOf(idCategoria.toString())
        } else {
            query = """
                SELECT r.id, r.modelo, r.id_categoria, r.talla, r.marca, r.color, r.precio, r.cantidad, r.foto, c.nombre as categoria_nombre
                FROM ropa r
                INNER JOIN categoria c ON r.id_categoria = c.id
                WHERE r.cantidad > 0
                ORDER BY r.id DESC
            """.trimIndent()
            args = null
        }
        val cursor = db.rawQuery(query, args)
        cursor.use {
            while (it.moveToNext()) {
                lista.add(
                    Ropa(
                        id = it.getInt(it.getColumnIndexOrThrow("id")),
                        modelo = it.getString(it.getColumnIndexOrThrow("modelo")),
                        idCategoria = it.getInt(it.getColumnIndexOrThrow("id_categoria")),
                        talla = it.getString(it.getColumnIndexOrThrow("talla")),
                        marca = it.getString(it.getColumnIndexOrThrow("marca")),
                        color = it.getString(it.getColumnIndexOrThrow("color")),
                        precio = it.getDouble(it.getColumnIndexOrThrow("precio")),
                        cantidad = it.getInt(it.getColumnIndexOrThrow("cantidad")),
                        foto = it.getString(it.getColumnIndexOrThrow("foto")) ?: "",
                        categoriaNombre = it.getString(it.getColumnIndexOrThrow("categoria_nombre")) ?: ""
                    )
                )
            }
        }
        return lista
    }
}
