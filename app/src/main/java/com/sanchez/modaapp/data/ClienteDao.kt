package com.sanchez.modaapp.data

import android.content.ContentValues
import android.content.Context
import com.sanchez.modaapp.model.Cliente

class ClienteDao(context: Context) {

    private val dbHelper = DBHelper(context)

    // HU-09 CA1: Buscar por teléfono de 9 dígitos
    fun buscarPorTelefono(telefono: String): Cliente? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT id, telefono, nombres, apellidos, fecha_registro FROM cliente WHERE telefono = ?",
            arrayOf(telefono)
        )
        return cursor.use {
            if (it.moveToFirst()) {
                Cliente(
                    id = it.getInt(it.getColumnIndexOrThrow("id")),
                    telefono = it.getString(it.getColumnIndexOrThrow("telefono")),
                    nombres = it.getString(it.getColumnIndexOrThrow("nombres")),
                    apellidos = it.getString(it.getColumnIndexOrThrow("apellidos")),
                    fechaRegistro = it.getString(it.getColumnIndexOrThrow("fecha_registro"))
                )
            } else null
        }
    }

    // HU-09 CA2: Registrar nuevo cliente
    fun insertar(cliente: Cliente): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("telefono", cliente.telefono)
            put("nombres", cliente.nombres)
            put("apellidos", cliente.apellidos)
            put("fecha_registro", cliente.fechaRegistro)
        }
        return db.insert("cliente", null, values)
    }

    // HU-12 CA3: Clientes con número de pedidos
    fun listarConPedidos(): List<Pair<Cliente, Int>> {
        val lista = mutableListOf<Pair<Cliente, Int>>()
        val db = dbHelper.readableDatabase
        val query = """
            SELECT c.id, c.telefono, c.nombres, c.apellidos, c.fecha_registro, COUNT(p.id) as total_pedidos
            FROM cliente c
            LEFT JOIN pedido p ON c.id = p.id_cliente
            GROUP BY c.id
            ORDER BY total_pedidos DESC, c.nombres ASC
        """.trimIndent()
        val cursor = db.rawQuery(query, null)
        cursor.use {
            while (it.moveToNext()) {
                val cliente = Cliente(
                    id = it.getInt(it.getColumnIndexOrThrow("id")),
                    telefono = it.getString(it.getColumnIndexOrThrow("telefono")),
                    nombres = it.getString(it.getColumnIndexOrThrow("nombres")),
                    apellidos = it.getString(it.getColumnIndexOrThrow("apellidos")),
                    fechaRegistro = it.getString(it.getColumnIndexOrThrow("fecha_registro"))
                )
                val count = it.getInt(it.getColumnIndexOrThrow("total_pedidos"))
                lista.add(Pair(cliente, count))
            }
        }
        return lista
    }
}
