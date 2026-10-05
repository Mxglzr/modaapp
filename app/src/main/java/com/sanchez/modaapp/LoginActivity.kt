package com.sanchez.modaapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sanchez.modaapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        // HU-01: Ingreso del Administrador
        binding.btnIngresar.setOnClickListener {
            val usuario = binding.etUsuario.text?.toString()?.trim().orEmpty()
            val clave = binding.etClave.text?.toString()?.trim().orEmpty()

            // CA1: Validar campos vacíos
            var hayError = false
            if (usuario.isEmpty()) {
                binding.tilUsuario.error = getString(R.string.error_campo_vacio)
                hayError = true
            } else {
                binding.tilUsuario.error = null
            }

            if (clave.isEmpty()) {
                binding.tilClave.error = getString(R.string.error_campo_vacio)
                hayError = true
            } else {
                binding.tilClave.error = null
            }

            if (hayError) return@setOnClickListener

            // CA2: Validación de credenciales admin / 1234
            if (usuario == "admin" && clave == "1234") {
                val intent = Intent(this, MenuActivity::class.java).apply {
                    putExtra("EXTRA_USUARIO", "Administrador")
                    putExtra("EXTRA_ROL", "ADMIN")
                }
                startActivity(intent)
                finish() // No regresa al login con botón atrás
            } else {
                // CA3: Credenciales incorrectas
                Toast.makeText(this, getString(R.string.msg_credenciales_incorrectas), Toast.LENGTH_SHORT).show()
            }
        }

        // HU-01 CA5: Acceso libre del cliente al catálogo
        binding.btnVerCatalogo.setOnClickListener {
            val intent = Intent(this, CatalogoActivity::class.java)
            startActivity(intent)
        }
    }
}
