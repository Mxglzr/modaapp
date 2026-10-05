package com.sanchez.modaapp.data

import android.content.Context
import com.sanchez.modaapp.model.Categoria

class CategoriaDao(context: Context) {

    private val dbHelper = DBHelper(context)

    fun listar(): List<Categoria> {
        val lista = mutableListOf<Categoria>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT id, nombre FROM categoria ORDER BY id ASC", null)
        cursor.use {
            while (it.moveToNext()) {
                lista.add(
                    Categoria(
                        id = it.getInt(it.getColumnIndexOrThrow("id")),
                        nombre = it.getString(it.getColumnIndexOrThrow("nombre"))
                    )
                )
            }
        }
        return lista
    }
}
