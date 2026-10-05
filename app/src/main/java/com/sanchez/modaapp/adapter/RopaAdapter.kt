package com.sanchez.modaapp.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
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
            tvItemRopaStock.text = "Stock: ${ropa.cantidad}"

            // Cargar imagen de disco si existe
            if (ropa.foto.isNotEmpty() && File(ropa.foto).exists()) {
                ivItemRopaFoto.imageTintList = null
                ivItemRopaFoto.setImageBitmap(BitmapFactory.decodeFile(ropa.foto))
            } else {
                ivItemRopaFoto.setImageResource(R.drawable.ic_menu_ropa)
                ivItemRopaFoto.setColorFilter(root.context.getColor(R.color.primary))
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
