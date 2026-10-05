package com.sanchez.modaapp.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sanchez.modaapp.R
import com.sanchez.modaapp.databinding.ItemCatalogoBinding
import com.sanchez.modaapp.model.Ropa
import java.io.File

class CatalogoAdapter(
    private var lista: List<Ropa>,
    private val onAgregarClick: (Ropa) -> Unit
) : RecyclerView.Adapter<CatalogoAdapter.CatalogoViewHolder>() {

    inner class CatalogoViewHolder(val binding: ItemCatalogoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogoViewHolder {
        val binding = ItemCatalogoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CatalogoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CatalogoViewHolder, position: Int) {
        val ropa = lista[position]
        with(holder.binding) {
            tvCatalogoModelo.text = ropa.modelo
            tvCatalogoTalla.text = "Talla ${ropa.talla} · ${ropa.categoriaNombre}"
            tvCatalogoPrecio.text = "S/ ${String.format("%.2f", ropa.precio)}"

            if (ropa.foto.isNotEmpty() && File(ropa.foto).exists()) {
                ivCatalogoFoto.imageTintList = null
                ivCatalogoFoto.setImageBitmap(BitmapFactory.decodeFile(ropa.foto))
            } else {
                ivCatalogoFoto.setImageResource(R.drawable.ic_menu_ropa)
                ivCatalogoFoto.setColorFilter(root.context.getColor(R.color.primary))
            }

            btnItemAgregarCarrito.setOnClickListener { onAgregarClick(ropa) }
        }
    }

    override fun getItemCount(): Int = lista.size

    fun actualizarLista(nuevaLista: List<Ropa>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}
