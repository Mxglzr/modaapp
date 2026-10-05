package com.sanchez.modaapp.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        const val DB_NAME = "modaapp.db"
        const val DB_VERSION = 2 // Actualizado en Sprint 3
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Tablas de Sprint 2 (v1)
        crearTablasV1(db)

        // Tablas de Sprint 3 (v2)
        crearTablasV2(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // HU-09: Migración a DB_VERSION = 2 sin perder datos
        if (oldVersion < 2) {
            crearTablasV2(db)
        }
    }

    private fun crearTablasV1(db: SQLiteDatabase) {
        // HU-04: Tabla usuario
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS usuario (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                usuario TEXT UNIQUE NOT NULL,
                clave TEXT NOT NULL,
                rol TEXT NOT NULL,
                telefono TEXT NOT NULL
            )
        """.trimIndent())

        // Insertar usuario administrador inicial
        db.execSQL("""
            INSERT OR IGNORE INTO usuario (usuario, clave, rol, telefono)
            VALUES ('admin', '1234', 'ADMIN', '987654321')
        """.trimIndent())

        // HU-05: Tabla categoria
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS categoria (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT UNIQUE NOT NULL
            )
        """.trimIndent())

        val categorias = listOf("Polos", "Pantalones", "Vestidos", "Casacas", "Zapatillas")
        for (cat in categorias) {
            db.execSQL("INSERT OR IGNORE INTO categoria (nombre) VALUES ('$cat')")
        }

        // HU-05: Tabla ropa
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS ropa (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                modelo TEXT NOT NULL,
                id_categoria INTEGER NOT NULL REFERENCES categoria(id),
                talla TEXT NOT NULL,
                marca TEXT,
                color TEXT,
                precio REAL NOT NULL CHECK(precio > 0),
                cantidad INTEGER NOT NULL CHECK(cantidad >= 0),
                foto TEXT
            )
        """.trimIndent())
    }

    private fun crearTablasV2(db: SQLiteDatabase) {
        // HU-09: Tabla cliente
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS cliente (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                telefono TEXT UNIQUE NOT NULL,
                nombres TEXT NOT NULL,
                apellidos TEXT NOT NULL,
                fecha_registro TEXT NOT NULL
            )
        """.trimIndent())

        // HU-09: Tabla pedido
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS pedido (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_cliente INTEGER NOT NULL REFERENCES cliente(id),
                fecha TEXT NOT NULL,
                total REAL NOT NULL,
                estado TEXT NOT NULL,
                fecha_atencion TEXT
            )
        """.trimIndent())

        // HU-09: Tabla detalle_pedido
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS detalle_pedido (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_pedido INTEGER NOT NULL REFERENCES pedido(id) ON DELETE CASCADE,
                id_ropa INTEGER NOT NULL REFERENCES ropa(id),
                cantidad INTEGER NOT NULL CHECK(cantidad > 0),
                precio_unit REAL NOT NULL,
                subtotal REAL NOT NULL
            )
        """.trimIndent())
    }
}
