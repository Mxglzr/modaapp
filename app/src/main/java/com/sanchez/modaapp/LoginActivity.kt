package com.sanchez.modaapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sanchez.modaapp.data.UsuarioDao
import com.sanchez.modaapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var usuarioDao: UsuarioDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        usuarioDao = UsuarioDao(this)
        setupListeners()
    }

    private fun setupListeners() {
        // HU-04: Autenticación en Base de Datos SQLite (modaapp.db)
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

            // HU-04 CA2: Validación con consulta parametrizada a SQLite
            val user = usuarioDao.validarUsuario(usuario, clave)
            if (user != null) {
                // CA3: Mostrar nombre y rol del usuario
                val intent = Intent(this, MenuActivity::class.java).apply {
                    putExtra("EXTRA_USUARIO", user.usuario.replaceFirstChar { it.uppercase() })
                    putExtra("EXTRA_ROL", user.rol)
                }
                startActivity(intent)
                finish()
            } else {
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
