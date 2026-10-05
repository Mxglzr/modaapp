package com.sanchez.modaapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.sanchez.modaapp.adapter.ClientesAdapter
import com.sanchez.modaapp.data.ClienteDao
import com.sanchez.modaapp.databinding.ActivityClientesBinding

class ClientesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClientesBinding
    private lateinit var clienteDao: ClienteDao
    private lateinit var adapter: ClientesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        clienteDao = ClienteDao(this)

        setupRecyclerView()
        setupListeners()
        cargarClientes()
    }

    private fun setupRecyclerView() {
        adapter = ClientesAdapter(emptyList())
        binding.rvClientes.layoutManager = LinearLayoutManager(this)
        binding.rvClientes.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnBackClientes.setOnClickListener {
            finish()
        }
    }

    private fun cargarClientes() {
        // HU-12 CA3: Listar clientes y su cantidad de pedidos
        val lista = clienteDao.listarConPedidos()
        adapter.actualizarLista(lista)

        if (lista.isEmpty()) {
            binding.tvClientesVacio.visibility = View.VISIBLE
            binding.rvClientes.visibility = View.GONE
        } else {
            binding.tvClientesVacio.visibility = View.GONE
            binding.rvClientes.visibility = View.VISIBLE
        }
    }
}
