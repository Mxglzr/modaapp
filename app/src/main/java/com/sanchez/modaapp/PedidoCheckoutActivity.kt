package com.sanchez.modaapp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sanchez.modaapp.data.Carrito
import com.sanchez.modaapp.data.ClienteDao
import com.sanchez.modaapp.data.PedidoDao
import com.sanchez.modaapp.databinding.ActivityPedidoCheckoutBinding
import com.sanchez.modaapp.model.Cliente
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PedidoCheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPedidoCheckoutBinding
    private lateinit var clienteDao: ClienteDao
    private lateinit var pedidoDao: PedidoDao

    private var clienteExistente: Cliente? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPedidoCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        clienteDao = ClienteDao(this)
        pedidoDao = PedidoDao(this)

        setupResumen()
        setupListeners()
    }

    private fun setupResumen() {
        val count = Carrito.contarItems()
        val total = Carrito.calcularTotal()
        binding.tvResumenItemsCheckout.text = "$count prenda(s) seleccionada(s)"
        binding.tvTotalCheckout.text = "S/ ${String.format("%.2f", total)}"
    }

    private fun setupListeners() {
        binding.btnBackCheckout.setOnClickListener { finish() }

        // HU-09 CA1: Detección en tiempo real del teléfono
        binding.etTelefonoCliente.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val tel = s?.toString()?.trim().orEmpty()
                if (tel.length == 9) {
                    verificarTelefono(tel)
                } else {
                    clienteExistente = null
                    binding.tvSaludoClienteExistente.visibility = View.GONE
                    binding.layoutDatosNuevoCliente.visibility = View.GONE
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // HU-09 CA3: Confirmar pedido con transacción
        binding.btnConfirmarPedido.setOnClickListener {
            confirmarPedido()
        }
    }

    private fun verificarTelefono(telefono: String) {
        val cliente = clienteDao.buscarPorTelefono(telefono)
        if (cliente != null) {
            clienteExistente = cliente
            binding.tvSaludoClienteExistente.text = "¡Hola, ${cliente.nombres}! Tu cuenta está registrada."
            binding.tvSaludoClienteExistente.visibility = View.VISIBLE
            binding.layoutDatosNuevoCliente.visibility = View.GONE
        } else {
            clienteExistente = null
            binding.tvSaludoClienteExistente.visibility = View.GONE
            binding.layoutDatosNuevoCliente.visibility = View.VISIBLE
        }
    }

    private fun confirmarPedido() {
        val tel = binding.etTelefonoCliente.text?.toString()?.trim().orEmpty()

        if (tel.length != 9) {
            binding.tilTelefonoCliente.error = "Ingrese un número válido de 9 dígitos"
            return
        } else {
            binding.tilTelefonoCliente.error = null
        }

        var idCliente = clienteExistente?.id ?: 0

        // HU-09 CA2: Si es cliente nuevo, validar y registrar
        if (clienteExistente == null) {
            val nombres = binding.etNombresCliente.text?.toString()?.trim().orEmpty()
            val apellidos = binding.etApellidosCliente.text?.toString()?.trim().orEmpty()

            var hayError = false
            if (nombres.isEmpty()) {
                binding.tilNombresCliente.error = "Nombres requeridos"
                hayError = true
            } else {
                binding.tilNombresCliente.error = null
            }

            if (apellidos.isEmpty()) {
                binding.tilApellidosCliente.error = "Apellidos requeridos"
                hayError = true
            } else {
                binding.tilApellidosCliente.error = null
            }

            if (hayError) return

            val fechaReg = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            val nuevoCliente = Cliente(
                telefono = tel,
                nombres = nombres,
                apellidos = apellidos,
                fechaRegistro = fechaReg
            )
            val idInsertado = clienteDao.insertar(nuevoCliente)
            if (idInsertado <= 0) {
                Toast.makeText(this, "Error al registrar cliente", Toast.LENGTH_SHORT).show()
                return
            }
            idCliente = idInsertado.toInt()
        }

        val items = Carrito.obtenerItems()
        if (items.isEmpty()) {
            Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
            return
        }

        val total = Carrito.calcularTotal()

        // HU-09 CA3: Guardar pedido en transacción SQLite
        val idPedido = pedidoDao.registrarPedido(idCliente, items, total)

        if (idPedido > 0) {
            // CA4: Vaciar carrito y notificar
            Carrito.limpiar()
            Toast.makeText(this, "Pedido #$idPedido registrado con éxito", Toast.LENGTH_LONG).show()

            // Abrir pantalla de confirmación con WhatsApp (Sprint 4)
            val intent = Intent(this, PedidoConfirmadoActivity::class.java).apply {
                putExtra("EXTRA_ID_PEDIDO", idPedido.toInt())
                putExtra("EXTRA_TEL_CLIENTE", tel)
            }
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Error al registrar el pedido", Toast.LENGTH_SHORT).show()
        }
    }
}
