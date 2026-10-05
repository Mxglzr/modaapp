package com.sanchez.modaapp

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sanchez.modaapp.adapter.PedidosAdapter
import com.sanchez.modaapp.data.PedidoDao
import com.sanchez.modaapp.databinding.ActivityPedidosBinding
import com.sanchez.modaapp.model.Pedido

class PedidosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPedidosBinding
    private lateinit var pedidoDao: PedidoDao
    private lateinit var adapter: PedidosAdapter
    private var estadoActual: String = "PENDIENTE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPedidosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pedidoDao = PedidoDao(this)

        setupRecyclerView()
        setupListeners()
        cargarPedidos()
    }

    private fun setupRecyclerView() {
        adapter = PedidosAdapter(emptyList()) { pedido ->
            mostrarDialogoDetalle(pedido)
        }
        binding.rvPedidos.layoutManager = LinearLayoutManager(this)
        binding.rvPedidos.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnBackPedidos.setOnClickListener {
            finish()
        }

        binding.chipPendientes.setOnClickListener {
            estadoActual = "PENDIENTE"
            cargarPedidos()
        }

        binding.chipAtendidos.setOnClickListener {
            estadoActual = "ATENDIDO"
            cargarPedidos()
        }
    }

    private fun cargarPedidos() {
        val lista = pedidoDao.listarPorEstado(estadoActual)
        adapter.actualizarLista(lista)

        if (lista.isEmpty()) {
            binding.tvPedidosVacio.visibility = View.VISIBLE
            binding.rvPedidos.visibility = View.GONE
            binding.tvPedidosVacio.text = if (estadoActual == "PENDIENTE") {
                "No hay pedidos pendientes"
            } else {
                "No hay pedidos atendidos"
            }
        } else {
            binding.tvPedidosVacio.visibility = View.GONE
            binding.rvPedidos.visibility = View.VISIBLE
        }
    }

    // HU-11 CA2, CA3, CA4: Detalle y atención de pedido
    private fun mostrarDialogoDetalle(pedido: Pedido) {
        val detalles = pedidoDao.obtenerDetalles(pedido.id)
        val sb = StringBuilder()
        sb.append("👤 Cliente: ${pedido.clienteNombres} ${pedido.clienteApellidos}\n")
        sb.append("📱 Teléfono: ${pedido.clienteTelefono}\n")
        sb.append("📅 Fecha: ${pedido.fecha}\n")
        if (!pedido.fechaAtencion.isNullOrBlank()) {
            sb.append("✅ Atendido: ${pedido.fechaAtencion}\n")
        }
        sb.append("\n📦 Prendas solicitadas:\n")
        for (d in detalles) {
            sb.append("• ${d.modelo} [${d.talla}, ${d.color}]\n")
            sb.append("  Cant: ${d.cantidad} x S/ ${String.format("%.2f", d.precioUnit)} = S/ ${String.format("%.2f", d.subtotal)}\n")
        }
        sb.append("\n💰 TOTAL: S/ ${String.format("%.2f", pedido.total)}")

        val builder = MaterialAlertDialogBuilder(this)
            .setTitle("Detalle del Pedido #${pedido.id}")
            .setMessage(sb.toString())

        if (pedido.estado.equals("PENDIENTE", ignoreCase = true)) {
            builder.setPositiveButton("Marcar como Atendido") { _, _ ->
                confirmarAtencionPedido(pedido)
            }
            builder.setNegativeButton("Cerrar", null)
        } else {
            builder.setPositiveButton("Aceptar", null)
        }

        builder.show()
    }

    private fun confirmarAtencionPedido(pedido: Pedido) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Confirmar Atención")
            .setMessage("¿Deseas marcar el pedido #${pedido.id} como ATENDIDO? Se descontará el stock de las prendas automáticamente.")
            .setPositiveButton("Sí, Atender") { _, _ ->
                val (exito, mensaje) = pedidoDao.atenderPedido(pedido.id)
                if (exito) {
                    Toast.makeText(this, "Pedido #${pedido.id} atendido con éxito", Toast.LENGTH_SHORT).show()
                    cargarPedidos()
                } else {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("No se puede atender")
                        .setMessage(mensaje)
                        .setPositiveButton("Aceptar", null)
                        .show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
