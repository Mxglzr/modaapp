package com.sanchez.modaapp.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sanchez.modaapp.R
import com.sanchez.modaapp.databinding.ItemCarritoBinding
import com.sanchez.modaapp.model.ItemCarrito
import java.io.File

class CarritoAdapter(
    private var lista: List<ItemCarrito>,
    private val onQuitarClick: (ItemCarrito) -> Unit
) : RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder>() {

    inner class CarritoViewHolder(val binding: ItemCarritoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarritoViewHolder {
        val binding = ItemCarritoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CarritoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarritoViewHolder, position: Int) {
        val item = lista[position]
        with(holder.binding) {
            tvItemCarritoModelo.text = item.ropa.modelo
            tvItemCarritoDetalle.text = "Talla ${item.ropa.talla} · Cant: ${item.cantidad} x S/ ${String.format("%.2f", item.ropa.precio)}"
            tvItemCarritoSubtotal.text = "Subtotal: S/ ${String.format("%.2f", item.subtotal)}"

            if (item.ropa.foto.isNotEmpty() && File(item.ropa.foto).exists()) {
                ivItemCarritoFoto.imageTintList = null
                ivItemCarritoFoto.setImageBitmap(BitmapFactory.decodeFile(item.ropa.foto))
            } else {
                ivItemCarritoFoto.setImageResource(R.drawable.ic_menu_ropa)
                ivItemCarritoFoto.setColorFilter(root.context.getColor(R.color.primary))
            }

            btnItemCarritoQuitar.setOnClickListener { onQuitarClick(item) }
            root.setOnLongClickListener {
                onQuitarClick(item)
                true
            }
        }
    }

    override fun getItemCount(): Int = lista.size

    fun actualizarLista(nuevaLista: List<ItemCarrito>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}
