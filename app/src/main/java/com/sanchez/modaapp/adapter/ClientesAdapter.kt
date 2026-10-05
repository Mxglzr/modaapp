package com.sanchez.modaapp.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.sanchez.modaapp.databinding.ItemClienteBinding
import com.sanchez.modaapp.model.Cliente

class ClientesAdapter(
    private var lista: List<Pair<Cliente, Int>>
) : RecyclerView.Adapter<ClientesAdapter.ClienteViewHolder>() {

    inner class ClienteViewHolder(val binding: ItemClienteBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClienteViewHolder {
        val binding = ItemClienteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ClienteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClienteViewHolder, position: Int) {
        val (cliente, totalPedidos) = lista[position]
        with(holder.binding) {
            tvClienteNombre.text = "${cliente.nombres} ${cliente.apellidos}"
            tvClienteTelefono.text = "Teléfono: ${cliente.telefono}"
            tvClientePedidosCount.text = "$totalPedidos pedido(s) registrado(s)"

            btnClienteWhatsApp.setOnClickListener {
                val url = "https://wa.me/51${cliente.telefono}?text=" +
                        Uri.encode("Hola ${cliente.nombres}, te saludamos de ModaApp Boutique Moda Urbana.")
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                try {
                    root.context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(root.context, "No se pudo abrir WhatsApp", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun getItemCount(): Int = lista.size

    fun actualizarLista(nuevaLista: List<Pair<Cliente, Int>>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}
