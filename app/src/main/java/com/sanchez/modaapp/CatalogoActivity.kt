package com.sanchez.modaapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.chip.Chip
import com.sanchez.modaapp.adapter.CatalogoAdapter
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
    }

    private fun setupRecyclerView() {
        adapter = CatalogoAdapter(emptyList()) { ropa ->
            // En Sprint 3 agrega con selector de cantidad
            Toast.makeText(this, "${ropa.modelo} seleccionado", Toast.LENGTH_SHORT).show()
        }
        // HU-06 CA1: Grilla de 2 columnas
        binding.rvCatalogo.layoutManager = GridLayoutManager(this, 2)
        binding.rvCatalogo.adapter = adapter
    }

    private fun setupChips() {
        // HU-06 CA2: Cargar categorías dinámicas desde la BD
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
        // HU-06 CA3: Solo prendas con cantidad > 0
        val disponibles = ropaDao.listarDisponibles(categoriaSeleccionadaId)
        adapter.actualizarLista(disponibles)
    }
}
