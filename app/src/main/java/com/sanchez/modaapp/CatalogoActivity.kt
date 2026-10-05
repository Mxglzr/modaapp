package com.sanchez.modaapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sanchez.modaapp.databinding.ActivityCatalogoBinding

class CatalogoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCatalogoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCatalogoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBackCatalogo.setOnClickListener {
            finish()
        }

        binding.btnAbrirCarrito.setOnClickListener {
            startActivity(Intent(this, CarritoActivity::class.java))
        }
    }
}
