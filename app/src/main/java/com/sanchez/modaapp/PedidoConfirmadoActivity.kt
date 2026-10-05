package com.sanchez.modaapp

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sanchez.modaapp.data.PedidoDao
import com.sanchez.modaapp.data.UsuarioDao
import com.sanchez.modaapp.databinding.ActivityPedidoConfirmadoBinding

class PedidoConfirmadoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPedidoConfirmadoBinding
    private lateinit var pedidoDao: PedidoDao
    private lateinit var usuarioDao: UsuarioDao

    private var idPedido: Int = 0
    private var telefonoCliente: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPedidoConfirmadoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pedidoDao = PedidoDao(this)
        usuarioDao = UsuarioDao(this)

        idPedido = intent.getIntExtra("EXTRA_ID_PEDIDO", 0)
        telefonoCliente = intent.getStringExtra("EXTRA_TEL_CLIENTE") ?: ""

        binding.tvNumeroPedidoConfirmado.text = "Pedido #$idPedido · Estado: PENDIENTE"

        setupListeners()
    }

    private fun setupListeners() {
        // HU-10 CA1: WhatsApp al cliente
        binding.btnWhatsAppCliente.setOnClickListener {
            enviarWhatsAppCliente()
        }

        // HU-10 CA2: WhatsApp a la tienda (administrador)
        binding.btnWhatsAppTienda.setOnClickListener {
            enviarWhatsAppTienda()
        }

        binding.btnVolverInicio.setOnClickListener {
            val intent = Intent(this, CatalogoActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            startActivity(intent)
            finish()
        }
    }

    private fun enviarWhatsAppCliente() {
        val detalles = pedidoDao.obtenerDetalles(idPedido)
        val sb = StringBuilder()
        sb.append("🛍️ *MODAAPP - BOUTIQUE MODA URBANA*\n")
        sb.append("¡Gracias por tu compra!\n\n")
        sb.append("📌 *Pedido:* #$idPedido\n")
        sb.append("Estado: *PENDIENTE*\n\n")
        sb.append("👗 *Prendas:* \n")

        var total = 0.0
        for (d in detalles) {
            sb.append("• ${d.modelo} (${d.talla}, ${d.color}) x${d.cantidad} = S/ ${String.format("%.2f", d.subtotal)}\n")
            total += d.subtotal
        }
        sb.append("\n💰 *TOTAL A PAGAR:* S/ ${String.format("%.2f", total)}\n")
        sb.append("Nos pondremos en contacto contigo para coordinar la entrega.")

        abrirWhatsApp(telefonoCliente, sb.toString())
    }

    private fun enviarWhatsAppTienda() {
        val detalles = pedidoDao.obtenerDetalles(idPedido)
        val telefonoAdmin = usuarioDao.obtenerAdminTelefono()

        val sb = StringBuilder()
        sb.append("🔔 *NUEVO PEDIDO RECIBIDO - MODAAPP*\n")
        sb.append("📌 *Pedido:* #$idPedido\n")
        sb.append("📱 *Cliente Teléfono:* $telefonoCliente\n\n")
        sb.append("👗 *Detalle:* \n")

        var total = 0.0
        for (d in detalles) {
            sb.append("• ${d.modelo} (${d.talla}, ${d.color}) x${d.cantidad} = S/ ${String.format("%.2f", d.subtotal)}\n")
            total += d.subtotal
        }
        sb.append("\n💰 *Total:* S/ ${String.format("%.2f", total)}\n")
        sb.append("Por favor atender en el panel de administración.")

        abrirWhatsApp(telefonoAdmin, sb.toString())
    }

    private fun abrirWhatsApp(telefono: String, mensaje: String) {
        val uri = Uri.parse("https://wa.me/51$telefono?text=" + Uri.encode(mensaje))
        val intent = Intent(Intent.ACTION_VIEW, uri)
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "WhatsApp no está instalado", Toast.LENGTH_SHORT).show()
        }
    }
}
