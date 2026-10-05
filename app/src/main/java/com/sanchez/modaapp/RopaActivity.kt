package com.sanchez.modaapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.sanchez.modaapp.adapter.RopaAdapter
import com.sanchez.modaapp.data.RopaDao
import com.sanchez.modaapp.databinding.ActivityRopaBinding

class RopaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRopaBinding
    private lateinit var ropaDao: RopaDao
    private lateinit var adapter: RopaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRopaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ropaDao = RopaDao(this)

        setupRecyclerView()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        cargarPrendas()
    }

    private fun setupRecyclerView() {
        adapter = RopaAdapter(emptyList()) { ropa ->
            // En Sprint 3 abre edición
            val intent = Intent(this, RopaFormActivity::class.java).apply {
                putExtra("EXTRA_ROPA", ropa)
            }
            startActivity(intent)
        }
        binding.rvRopa.layoutManager = LinearLayoutManager(this)
        binding.rvRopa.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnBackRopa.setOnClickListener { finish() }

        // HU-05: FAB para registrar nueva prenda
        binding.fabAgregarRopa.setOnClickListener {
            val intent = Intent(this, RopaFormActivity::class.java)
            startActivity(intent)
        }

        // HU-07 CA3: Búsqueda en tiempo real por modelo, marca o color
        binding.etBuscarRopa.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                buscarPrendas(s?.toString().orEmpty())
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    private fun cargarPrendas() {
        val query = binding.etBuscarRopa.text?.toString().orEmpty()
        if (query.isNotBlank()) {
            buscarPrendas(query)
        } else {
            val lista = ropaDao.listar()
            mostrarLista(lista)
        }
    }

    private fun buscarPrendas(filtro: String) {
        val lista = if (filtro.isBlank()) {
            ropaDao.listar()
        } else {
            ropaDao.buscar(filtro)
        }
        mostrarLista(lista)
    }

    private fun mostrarLista(lista: List<com.sanchez.modaapp.model.Ropa>) {
        adapter.actualizarLista(lista)
        if (lista.isEmpty()) {
            binding.tvRopaVacia.visibility = View.VISIBLE
            binding.rvRopa.visibility = View.GONE
        } else {
            binding.tvRopaVacia.visibility = View.GONE
            binding.rvRopa.visibility = View.VISIBLE
        }
    }
}
