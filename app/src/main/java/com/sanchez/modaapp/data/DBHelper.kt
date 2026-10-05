package com.sanchez.modaapp.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        const val DB_NAME = "modaapp.db"
        const val DB_VERSION = 1
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        // HU-04: Tabla usuario
        db.execSQL("""
            CREATE TABLE usuario (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                usuario TEXT UNIQUE NOT NULL,
                clave TEXT NOT NULL,
                rol TEXT NOT NULL,
                telefono TEXT NOT NULL
            )
        """.trimIndent())

        // Insertar usuario administrador inicial (admin / 1234)
        db.execSQL("""
            INSERT OR IGNORE INTO usuario (usuario, clave, rol, telefono)
            VALUES ('admin', '1234', 'ADMIN', '987654321')
        """.trimIndent())

        // HU-05: Tabla categoria
        db.execSQL("""
            CREATE TABLE categoria (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT UNIQUE NOT NULL
            )
        """.trimIndent())

        // Precarga de las 5 categorías según la plantilla
        val categorias = listOf("Polos", "Pantalones", "Vestidos", "Casacas", "Zapatillas")
        for (cat in categorias) {
            db.execSQL("INSERT OR IGNORE INTO categoria (nombre) VALUES ('$cat')")
        }

        // HU-05: Tabla ropa
        db.execSQL("""
            CREATE TABLE ropa (
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

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // En Sprint 3 se agregará la migración a DB_VERSION = 2
    }
}
