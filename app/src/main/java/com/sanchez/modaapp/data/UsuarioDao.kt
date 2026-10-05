package com.sanchez.modaapp.data

import android.content.Context
import com.sanchez.modaapp.model.Usuario

class UsuarioDao(context: Context) {

    private val dbHelper = DBHelper(context)

    // HU-04 CA2: Consulta parametrizada con ?
    fun validarUsuario(usuario: String, clave: String): Usuario? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT id, usuario, clave, rol, telefono FROM usuario WHERE usuario = ? AND clave = ?",
            arrayOf(usuario, clave)
        )
        return cursor.use {
            if (it.moveToFirst()) {
                Usuario(
                    id = it.getInt(it.getColumnIndexOrThrow("id")),
                    usuario = it.getString(it.getColumnIndexOrThrow("usuario")),
                    clave = it.getString(it.getColumnIndexOrThrow("clave")),
                    rol = it.getString(it.getColumnIndexOrThrow("rol")),
                    telefono = it.getString(it.getColumnIndexOrThrow("telefono"))
                )
            } else {
                null
            }
        }
    }

    fun obtenerAdminTelefono(): String {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT telefono FROM usuario WHERE rol = 'ADMIN' LIMIT 1", null)
        return cursor.use {
            if (it.moveToFirst()) {
                it.getString(0)
            } else {
                "987654321"
            }
        }
    }
}
