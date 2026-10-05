package com.sanchez.modaapp.adapter

import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sanchez.modaapp.R
import com.sanchez.modaapp.databinding.ItemRopaBinding
import com.sanchez.modaapp.model.Ropa
import java.io.File

class RopaAdapter(
    private var lista: List<Ropa>,
    private val onItemClick: (Ropa) -> Unit
) : RecyclerView.Adapter<RopaAdapter.RopaViewHolder>() {

    inner class RopaViewHolder(val binding: ItemRopaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RopaViewHolder {
        val binding = ItemRopaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RopaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RopaViewHolder, position: Int) {
        val ropa = lista[position]
        with(holder.binding) {
            tvItemRopaModelo.text = ropa.modelo
            tvItemRopaDetalle.text = "Talla ${ropa.talla} · Color ${ropa.color} · ${ropa.categoriaNombre}"
            tvItemRopaPrecio.text = "S/ ${String.format("%.2f", ropa.precio)}"

            // Pastilla de stock con alerta visual si es crítico
            if (ropa.cantidad <= 3) {
                tvItemRopaStock.setBackgroundResource(R.drawable.badge_stock_alerta)
                tvItemRopaStock.setTextColor(Color.parseColor("#D32F2F"))
                tvItemRopaStock.text = "Stock: ${ropa.cantidad}"
            } else {
                tvItemRopaStock.setBackgroundResource(R.drawable.badge_stock_normal)
                tvItemRopaStock.setTextColor(Color.parseColor("#2E7D32"))
                tvItemRopaStock.text = "Stock: ${ropa.cantidad}"
            }

            // Cargar imagen de disco si existe o mostrar placeholder estilizado
            if (ropa.foto.isNotEmpty() && File(ropa.foto).exists()) {
                ivItemRopaFoto.setPadding(0, 0, 0, 0)
                ivItemRopaFoto.scaleType = ImageView.ScaleType.CENTER_CROP
                ivItemRopaFoto.clearColorFilter()
                ivItemRopaFoto.imageTintList = null
                ivItemRopaFoto.setImageBitmap(BitmapFactory.decodeFile(ropa.foto))
            } else {
                val pad = (16 * root.resources.displayMetrics.density).toInt()
                ivItemRopaFoto.setPadding(pad, pad, pad, pad)
                ivItemRopaFoto.scaleType = ImageView.ScaleType.FIT_CENTER
                ivItemRopaFoto.setImageResource(R.drawable.ic_menu_ropa)
                ivItemRopaFoto.setColorFilter(ContextCompat.getColor(root.context, R.color.primary))
            }

            root.setOnClickListener { onItemClick(ropa) }
        }
    }

    override fun getItemCount(): Int = lista.size

    fun actualizarLista(nuevaLista: List<Ropa>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}
