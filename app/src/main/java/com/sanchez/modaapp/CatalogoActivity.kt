package com.sanchez.modaapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sanchez.modaapp.adapter.CatalogoAdapter
import com.sanchez.modaapp.data.Carrito
import com.sanchez.modaapp.data.CategoriaDao
import com.sanchez.modaapp.data.RopaDao
import com.sanchez.modaapp.databinding.ActivityCatalogoBinding
import com.sanchez.modaapp.model.Categoria
import com.sanchez.modaapp.model.Ropa

class CatalogoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCatalogoBinding
    private lateinit var ropaDao: RopaDao
    private lateinit var categoriaDao: CategoriaDao
    private lateinit var adapter: CatalogoAdapter

    private var categoriaSeleccionadaId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCatalogoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ropaDao = RopaDao(this)
        categoriaDao = CategoriaDao(this)

        setupRecyclerView()
        setupChips()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        cargarCatalogo()
        actualizarBadgeCarrito()
    }

    private fun setupRecyclerView() {
        adapter = CatalogoAdapter(emptyList()) { ropa ->
            mostrarDialogoCantidad(ropa)
        }
        // HU-06 CA1: Grilla de 2 columnas
        binding.rvCatalogo.layoutManager = GridLayoutManager(this, 2)
        binding.rvCatalogo.adapter = adapter
    }

    // HU-08 CA1: Diálogo para elegir cantidad sin superar stock
    private fun mostrarDialogoCantidad(ropa: Ropa) {
        val view = LayoutInflater.from(this).inflate(android.R.layout.simple_list_item_2, null)
        val input = EditText(this).apply {
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            setText("1")
            setSelection(1)
            hint = "Cantidad a pedir"
        }

        MaterialAlertDialogBuilder(this)
            .setTitle(ropa.modelo)
            .setMessage("Disponible: ${ropa.cantidad} unidades\nPrecio: S/ ${String.format("%.2f", ropa.precio)}")
            .setView(input)
            .setPositiveButton("Agregar al carrito") { _, _ ->
                val cantStr = input.text.toString().trim()
                val cantidad = cantStr.toIntOrNull() ?: 0

                if (cantidad <= 0) {
                    Toast.makeText(this, "La cantidad debe ser mayor a 0", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (cantidad > ropa.cantidad) {
                    Toast.makeText(this, "No puede superar el stock (${ropa.cantidad})", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                Carrito.agregar(ropa, cantidad)
                actualizarBadgeCarrito()
                Toast.makeText(this, "Agregado al carrito: ${ropa.modelo} (x$cantidad)", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun setupChips() {
        val categorias = categoriaDao.listar()

        binding.chipTodas.setOnClickListener {
            categoriaSeleccionadaId = null
            cargarCatalogo()
        }

        for (cat in categorias) {
            val chip = Chip(this).apply {
                text = cat.nombre
                isCheckable = true
                setOnClickListener {
                    categoriaSeleccionadaId = cat.id
                    cargarCatalogo()
                }
            }
            binding.chipGroupCategorias.addView(chip)
        }
    }

    private fun setupListeners() {
        binding.btnBackCatalogo.setOnClickListener { finish() }

        binding.btnAbrirCarrito.setOnClickListener {
            startActivity(Intent(this, CarritoActivity::class.java))
        }
    }

    private fun cargarCatalogo() {
        val disponibles = ropaDao.listarDisponibles(categoriaSeleccionadaId)
        adapter.actualizarLista(disponibles)
    }

    private fun actualizarBadgeCarrito() {
        val totalItems = Carrito.contarItems()
        if (totalItems > 0) {
            binding.tvBadgeCarrito.text = totalItems.toString()
            binding.tvBadgeCarrito.visibility = View.VISIBLE
        } else {
            binding.tvBadgeCarrito.visibility = View.GONE
        }
    }
}
