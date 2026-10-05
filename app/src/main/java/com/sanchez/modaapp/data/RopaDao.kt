package com.sanchez.modaapp.data

import android.content.ContentValues
import android.content.Context
import com.sanchez.modaapp.model.Ropa

class RopaDao(context: Context) {

    private val dbHelper = DBHelper(context)

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

    // HU-07: Actualizar prenda existente
    fun actualizar(ropa: Ropa): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("modelo", ropa.modelo)
            put("id_categoria", ropa.idCategoria)
            put("talla", ropa.talla)
            put("marca", ropa.marca)
            put("color", ropa.color)
            put("precio", ropa.precio)
            put("cantidad", ropa.cantidad)
            if (ropa.foto.isNotEmpty()) {
                put("foto", ropa.foto)
            }
        }
        return db.update("ropa", values, "id = ?", arrayOf(ropa.id.toString()))
    }

    // HU-07: Eliminar prenda por ID
    fun eliminar(id: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete("ropa", "id = ?", arrayOf(id.toString()))
    }

    // HU-07: Verificar si una prenda ya está en algún detalle de pedido
    fun tienePedidos(idRopa: Int): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM detalle_pedido WHERE id_ropa = ?", arrayOf(idRopa.toString()))
        return cursor.use {
            if (it.moveToFirst()) it.getInt(0) > 0 else false
        }
    }

    // HU-07 CA3: Buscador con LIKE en modelo, marca y color
    fun buscar(filtro: String): List<Ropa> {
        val lista = mutableListOf<Ropa>()
        val db = dbHelper.readableDatabase
        val patron = "%$filtro%"
        val query = """
            SELECT r.id, r.modelo, r.id_categoria, r.talla, r.marca, r.color, r.precio, r.cantidad, r.foto, c.nombre as categoria_nombre
            FROM ropa r
            INNER JOIN categoria c ON r.id_categoria = c.id
            WHERE r.modelo LIKE ? OR r.marca LIKE ? OR r.color LIKE ?
            ORDER BY r.id DESC
        """.trimIndent()
        val cursor = db.rawQuery(query, arrayOf(patron, patron, patron))
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

    // Descontar stock para atención de pedidos (HU-11)
    fun descontarStock(idRopa: Int, cantidadDescontar: Int): Boolean {
        val db = dbHelper.writableDatabase
        val cursor = db.rawQuery("SELECT cantidad FROM ropa WHERE id = ?", arrayOf(idRopa.toString()))
        val stockActual = cursor.use {
            if (it.moveToFirst()) it.getInt(0) else return false
        }
        if (stockActual < cantidadDescontar) return false
        val nuevoStock = stockActual - cantidadDescontar
        val values = ContentValues().apply { put("cantidad", nuevoStock) }
        return db.update("ropa", values, "id = ?", arrayOf(idRopa.toString())) > 0
    }
}
