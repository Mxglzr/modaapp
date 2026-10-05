package com.sanchez.modaapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sanchez.modaapp.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nombre = intent.getStringExtra("EXTRA_USUARIO") ?: "Administrador"
        val rol = intent.getStringExtra("EXTRA_ROL") ?: "ADMIN"
        binding.tvSaludoUsuario.text = getString(R.string.menu_bienvenida, nombre, rol)

        setupListeners()
    }

    private fun setupListeners() {
        // HU-02: Navegación del Administrador
        binding.cardRopa.setOnClickListener {
            startActivity(Intent(this, RopaActivity::class.java))
        }

        binding.cardPedidos.setOnClickListener {
            startActivity(Intent(this, PedidosActivity::class.java))
        }

        binding.cardClientes.setOnClickListener {
            startActivity(Intent(this, ClientesActivity::class.java))
        }

        binding.cardReportes.setOnClickListener {
            startActivity(Intent(this, ReportesActivity::class.java))
        }

        // HU-02 CA3: Salir y volver al Login
        binding.btnSalir.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
