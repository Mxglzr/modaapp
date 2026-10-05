package com.sanchez.modaapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sanchez.modaapp.adapter.CarritoAdapter
import com.sanchez.modaapp.data.Carrito
import com.sanchez.modaapp.databinding.ActivityCarritoBinding
import com.sanchez.modaapp.model.ItemCarrito

class CarritoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCarritoBinding
    private lateinit var adapter: CarritoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCarritoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        refrescarCarrito()
    }

    private fun setupRecyclerView() {
        adapter = CarritoAdapter(emptyList()) { item ->
            confirmarEliminarItem(item)
        }
        binding.rvCarrito.layoutManager = LinearLayoutManager(this)
        binding.rvCarrito.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnBackCarrito.setOnClickListener { finish() }

        // HU-09: Abrir checkout con número de teléfono
        binding.btnHacerPedido.setOnClickListener {
            startActivity(Intent(this, PedidoCheckoutActivity::class.java))
        }
    }

    private fun confirmarEliminarItem(item: ItemCarrito) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Quitar prenda")
            .setMessage("¿Deseas quitar \"${item.ropa.modelo}\" del carrito?")
            .setPositiveButton("Quitar") { _, _ ->
                Carrito.quitar(item.ropa.id)
                refrescarCarrito()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun refrescarCarrito() {
        val items = Carrito.obtenerItems()
        adapter.actualizarLista(items)

        val total = Carrito.calcularTotal()
        binding.tvTotalCarrito.text = "S/ ${String.format("%.2f", total)}"

        // HU-08 CA4: Si el carrito está vacío
        if (items.isEmpty()) {
            binding.tvCarritoVacio.visibility = View.VISIBLE
            binding.rvCarrito.visibility = View.GONE
            binding.btnHacerPedido.isEnabled = false
        } else {
            binding.tvCarritoVacio.visibility = View.GONE
            binding.rvCarrito.visibility = View.VISIBLE
            binding.btnHacerPedido.isEnabled = true
        }
    }
}
