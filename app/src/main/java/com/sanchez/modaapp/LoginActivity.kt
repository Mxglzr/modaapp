package com.sanchez.modaapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sanchez.modaapp.data.UsuarioDao
import com.sanchez.modaapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var usuarioDao: UsuarioDao

    companion object {
        const val PREFS_NAME = "ModaAppPrefs"
        const val KEY_SESION_ACTIVA = "sesion_activa"
        const val KEY_USUARIO = "usuario_nombre"
        const val KEY_ROL = "usuario_rol"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // HU-13 CA1: Comprobar sesión activa persistente
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (prefs.getBoolean(KEY_SESION_ACTIVA, false)) {
            val usuarioGuardado = prefs.getString(KEY_USUARIO, "Administrador") ?: "Administrador"
            val rolGuardado = prefs.getString(KEY_ROL, "ADMIN") ?: "ADMIN"
            irAlMenu(usuarioGuardado, rolGuardado)
            return
        }

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
                // HU-13 CA1: Guardar sesión si marcó el checkbox
                if (binding.cbRecordarSesion.isChecked) {
                    val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    prefs.edit()
                        .putBoolean(KEY_SESION_ACTIVA, true)
                        .putString(KEY_USUARIO, user.usuario.replaceFirstChar { it.uppercase() })
                        .putString(KEY_ROL, user.rol)
                        .apply()
                }

                irAlMenu(user.usuario.replaceFirstChar { it.uppercase() }, user.rol)
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

    private fun irAlMenu(usuario: String, rol: String) {
        val intent = Intent(this, MenuActivity::class.java).apply {
            putExtra("EXTRA_USUARIO", usuario)
            putExtra("EXTRA_ROL", rol)
        }
        startActivity(intent)
        finish()
    }
}
