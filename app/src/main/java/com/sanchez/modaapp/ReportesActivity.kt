package com.sanchez.modaapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.sanchez.modaapp.adapter.StockReporteAdapter
import com.sanchez.modaapp.data.PedidoDao
import com.sanchez.modaapp.data.RopaDao
import com.sanchez.modaapp.databinding.ActivityReportesBinding

class ReportesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportesBinding
    private lateinit var pedidoDao: PedidoDao
    private lateinit var ropaDao: RopaDao
    private lateinit var adapter: StockReporteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pedidoDao = PedidoDao(this)
        ropaDao = RopaDao(this)

        setupRecyclerView()
        setupListeners()
        cargarMetricas()
    }

    private fun setupRecyclerView() {
        adapter = StockReporteAdapter(emptyList())
        binding.rvStockPrendas.layoutManager = LinearLayoutManager(this)
        binding.rvStockPrendas.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnBackReportes.setOnClickListener {
            finish()
        }
    }

    private fun cargarMetricas() {
        // HU-12 CA1: Total vendido y pedidos atendidos en el mes
        val totalVendido = pedidoDao.totalVendidoMes()
        val pedidosAtendidos = pedidoDao.pedidosAtendidosMes()

        binding.tvTotalVendidoMes.text = "S/ ${String.format("%.2f", totalVendido)}"
        binding.tvPedidosAtendidosMes.text = "$pedidosAtendidos pedido(s) atendido(s)"

        // HU-12 CA2: Prendas con alerta de stock crítico
        val prendas = ropaDao.listar()
        adapter.actualizarLista(prendas)
    }
}
