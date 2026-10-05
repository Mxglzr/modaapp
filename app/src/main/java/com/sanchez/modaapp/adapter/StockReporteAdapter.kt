package com.sanchez.modaapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sanchez.modaapp.R
import com.sanchez.modaapp.databinding.ItemStockReporteBinding
import com.sanchez.modaapp.model.Ropa

class StockReporteAdapter(
    private var lista: List<Ropa>
) : RecyclerView.Adapter<StockReporteAdapter.StockViewHolder>() {

    inner class StockViewHolder(val binding: ItemStockReporteBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val binding = ItemStockReporteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StockViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        val prenda = lista[position]
        with(holder.binding) {
            tvStockModelo.text = prenda.modelo
            tvStockDetalles.text = "Talla ${prenda.talla} · Color ${prenda.color} · S/ ${String.format("%.2f", prenda.precio)}"

            if (prenda.cantidad <= 3) {
                // Stock crítico
                tvStockBadge.setBackgroundResource(R.drawable.badge_critico)
                tvStockBadge.text = "CRÍTICO: ${prenda.cantidad}"
                cardStock.setCardBackgroundColor(
                    ContextCompat.getColor(root.context, R.color.status_critico_bg)
                )
                cardStock.strokeColor = ContextCompat.getColor(root.context, R.color.status_critico)
            } else {
                tvStockBadge.setBackgroundResource(R.drawable.badge_atendido)
                tvStockBadge.text = "Stock: ${prenda.cantidad}"
                cardStock.setCardBackgroundColor(
                    ContextCompat.getColor(root.context, R.color.white)
                )
                cardStock.strokeColor = ContextCompat.getColor(root.context, R.color.stroke_card)
            }
        }
    }

    override fun getItemCount(): Int = lista.size

    fun actualizarLista(nuevaLista: List<Ropa>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}
