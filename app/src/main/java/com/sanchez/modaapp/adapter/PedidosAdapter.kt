package com.sanchez.modaapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sanchez.modaapp.R
import com.sanchez.modaapp.databinding.ItemPedidoBinding
import com.sanchez.modaapp.model.Pedido

class PedidosAdapter(
    private var lista: List<Pedido>,
    private val onVerDetalle: (Pedido) -> Unit
) : RecyclerView.Adapter<PedidosAdapter.PedidoViewHolder>() {

    inner class PedidoViewHolder(val binding: ItemPedidoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val binding = ItemPedidoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PedidoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        val pedido = lista[position]
        with(holder.binding) {
            tvItemPedidoId.text = "Pedido #${pedido.id}"
            tvItemPedidoEstado.text = pedido.estado

            if (pedido.estado.equals("ATENDIDO", ignoreCase = true)) {
                tvItemPedidoEstado.setBackgroundResource(R.drawable.badge_atendido)
                tvItemPedidoFecha.text = "Fecha: ${pedido.fecha} · Atendido: ${pedido.fechaAtencion ?: "-"}"
                btnItemPedidoDetalle.text = "Ver Detalle"
            } else {
                tvItemPedidoEstado.setBackgroundResource(R.drawable.badge_pendiente)
                tvItemPedidoFecha.text = "Fecha: ${pedido.fecha}"
                btnItemPedidoDetalle.text = "Atender Pedido"
            }

            tvItemPedidoCliente.text = "Cliente: ${pedido.clienteNombres} ${pedido.clienteApellidos} (${pedido.clienteTelefono})"
            tvItemPedidoTotal.text = "S/ ${String.format("%.2f", pedido.total)}"

            btnItemPedidoDetalle.setOnClickListener {
                onVerDetalle(pedido)
            }
            root.setOnClickListener {
                onVerDetalle(pedido)
            }
        }
    }

    override fun getItemCount(): Int = lista.size

    fun actualizarLista(nuevaLista: List<Pedido>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}
