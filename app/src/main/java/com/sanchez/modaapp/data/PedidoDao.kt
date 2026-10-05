package com.sanchez.modaapp.data

import android.content.ContentValues
import android.content.Context
import com.sanchez.modaapp.model.DetallePedido
import com.sanchez.modaapp.model.ItemCarrito
import com.sanchez.modaapp.model.Pedido
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PedidoDao(context: Context) {

    private val dbHelper = DBHelper(context)

    // HU-09 CA3: Registro de pedido con transacción SQLite
    fun registrarPedido(idCliente: Int, items: List<ItemCarrito>, total: Double): Long {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            val fechaActual = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

            val valoresPedido = ContentValues().apply {
                put("id_cliente", idCliente)
                put("fecha", fechaActual)
                put("total", total)
                put("estado", "PENDIENTE")
            }
            val idPedido = db.insert("pedido", null, valoresPedido)
            if (idPedido <= 0) return -1

            for (item in items) {
                val valoresDetalle = ContentValues().apply {
                    put("id_pedido", idPedido)
                    put("id_ropa", item.ropa.id)
                    put("cantidad", item.cantidad)
                    put("precio_unit", item.ropa.precio)
                    put("subtotal", item.subtotal)
                }
                val idDetalle = db.insert("detalle_pedido", null, valoresDetalle)
                if (idDetalle <= 0) return -1
            }

            db.setTransactionSuccessful()
            return idPedido
        } catch (e: Exception) {
            return -1
        } finally {
            db.endTransaction()
        }
    }

    // HU-11 CA1: Listar pedidos por estado (PENDIENTE o ATENDIDO)
    fun listarPorEstado(estado: String): List<Pedido> {
        val lista = mutableListOf<Pedido>()
        val db = dbHelper.readableDatabase
        val query = """
            SELECT p.id, p.id_cliente, p.fecha, p.total, p.estado, p.fecha_atencion,
                   c.nombres, c.apellidos, c.telefono
            FROM pedido p
            INNER JOIN cliente c ON p.id_cliente = c.id
            WHERE p.estado = ?
            ORDER BY p.id DESC
        """.trimIndent()
        val cursor = db.rawQuery(query, arrayOf(estado))
        cursor.use {
            while (it.moveToNext()) {
                lista.add(
                    Pedido(
                        id = it.getInt(it.getColumnIndexOrThrow("id")),
                        idCliente = it.getInt(it.getColumnIndexOrThrow("id_cliente")),
                        fecha = it.getString(it.getColumnIndexOrThrow("fecha")),
                        total = it.getDouble(it.getColumnIndexOrThrow("total")),
                        estado = it.getString(it.getColumnIndexOrThrow("estado")),
                        fechaAtencion = it.getString(it.getColumnIndexOrThrow("fecha_atencion")),
                        clienteNombres = it.getString(it.getColumnIndexOrThrow("nombres")),
                        clienteApellidos = it.getString(it.getColumnIndexOrThrow("apellidos")),
                        clienteTelefono = it.getString(it.getColumnIndexOrThrow("telefono"))
                    )
                )
            }
        }
        return lista
    }

    // HU-11 CA2: Obtener detalles de un pedido
    fun obtenerDetalles(idPedido: Int): List<DetallePedido> {
        val lista = mutableListOf<DetallePedido>()
        val db = dbHelper.readableDatabase
        val query = """
            SELECT d.id, d.id_pedido, d.id_ropa, d.cantidad, d.precio_unit, d.subtotal,
                   r.modelo, r.talla, r.color, r.foto
            FROM detalle_pedido d
            INNER JOIN ropa r ON d.id_ropa = r.id
            WHERE d.id_pedido = ?
        """.trimIndent()
        val cursor = db.rawQuery(query, arrayOf(idPedido.toString()))
        cursor.use {
            while (it.moveToNext()) {
                lista.add(
                    DetallePedido(
                        id = it.getInt(it.getColumnIndexOrThrow("id")),
                        idPedido = it.getInt(it.getColumnIndexOrThrow("id_pedido")),
                        idRopa = it.getInt(it.getColumnIndexOrThrow("id_ropa")),
                        cantidad = it.getInt(it.getColumnIndexOrThrow("cantidad")),
                        precioUnit = it.getDouble(it.getColumnIndexOrThrow("precio_unit")),
                        subtotal = it.getDouble(it.getColumnIndexOrThrow("subtotal")),
                        modelo = it.getString(it.getColumnIndexOrThrow("modelo")),
                        talla = it.getString(it.getColumnIndexOrThrow("talla")),
                        color = it.getString(it.getColumnIndexOrThrow("color")),
                        foto = it.getString(it.getColumnIndexOrThrow("foto")) ?: ""
                    )
                )
            }
        }
        return lista
    }

    // HU-11 CA3 y CA4: Atender pedido con transacción y descuento de stock
    fun atenderPedido(idPedido: Int): Pair<Boolean, String> {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            val detalles = obtenerDetalles(idPedido)
            for (det in detalles) {
                val cursorStock = db.rawQuery("SELECT cantidad, modelo FROM ropa WHERE id = ?", arrayOf(det.idRopa.toString()))
                val (stockActual, modelo) = cursorStock.use {
                    if (it.moveToFirst()) {
                        Pair(it.getInt(0), it.getString(1))
                    } else Pair(-1, "")
                }

                if (stockActual < det.cantidad) {
                    return Pair(false, "Stock insuficiente: $modelo (Disponible: $stockActual)")
                }

                val values = ContentValues().apply {
                    put("cantidad", stockActual - det.cantidad)
                }
                db.update("ropa", values, "id = ?", arrayOf(det.idRopa.toString()))
            }

            val fechaAtencion = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
            val valPedido = ContentValues().apply {
                put("estado", "ATENDIDO")
                put("fecha_atencion", fechaAtencion)
            }
            db.update("pedido", valPedido, "id = ?", arrayOf(idPedido.toString()))

            db.setTransactionSuccessful()
            return Pair(true, "Pedido atendido con éxito")
        } catch (e: Exception) {
            return Pair(false, "Error al atender pedido: ${e.message}")
        } finally {
            db.endTransaction()
        }
    }

    // HU-12 CA1: Métricas para Reportes
    fun totalVendidoMes(): Double {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT SUM(total) FROM pedido WHERE estado = 'ATENDIDO'", null)
        return cursor.use {
            if (it.moveToFirst()) it.getDouble(0) else 0.0
        }
    }

    fun pedidosAtendidosMes(): Int {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM pedido WHERE estado = 'ATENDIDO'", null)
        return cursor.use {
            if (it.moveToFirst()) it.getInt(0) else 0
        }
    }
}
