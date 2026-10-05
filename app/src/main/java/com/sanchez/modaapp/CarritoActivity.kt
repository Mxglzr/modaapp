package com.sanchez.modaapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sanchez.modaapp.databinding.ActivityCarritoBinding

class CarritoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCarritoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCarritoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBackCarrito.setOnClickListener {
            finish()
        }
    }
}
