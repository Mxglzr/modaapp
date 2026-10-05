package com.sanchez.modaapp

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.sanchez.modaapp.data.CategoriaDao
import com.sanchez.modaapp.data.RopaDao
import com.sanchez.modaapp.databinding.ActivityRopaFormBinding
import com.sanchez.modaapp.model.Categoria
import com.sanchez.modaapp.model.Ropa
import java.io.File
import java.io.FileOutputStream

class RopaFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRopaFormBinding
    private lateinit var categoriaDao: CategoriaDao
    private lateinit var ropaDao: RopaDao

    private var categorias: List<Categoria> = emptyList()
    private val tallas = listOf("XS", "S", "M", "L", "XL")

    private var rutaFotoSeleccionada: String = ""
    private var ropaEdicion: Ropa? = null

    // HU-05 CA1: Selector de foto de la galería
    private val seleccionarFotoLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { copiarFotoAAlmacenamientoInterno(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRopaFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        categoriaDao = CategoriaDao(this)
        ropaDao = RopaDao(this)

        setupDropdowns()
        verificarModoEdicion()
        setupListeners()
    }

    private fun setupDropdowns() {
        // Cargar categorías de la base de datos (HU-05 CA2)
        categorias = categoriaDao.listar()
        val nombresCategorias = categorias.map { it.nombre }
        val adapterCat = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, nombresCategorias)
        binding.actvCategoria.setAdapter(adapterCat)
        if (nombresCategorias.isNotEmpty() && ropaEdicion == null) {
            binding.actvCategoria.setText(nombresCategorias[0], false)
        }

        // Dropdown de Tallas
        val adapterTalla = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, tallas)
        binding.actvTalla.setAdapter(adapterTalla)
        if (tallas.isNotEmpty() && ropaEdicion == null) {
            binding.actvTalla.setText(tallas[1], false) // "S" por defecto
        }
    }

    private fun verificarModoEdicion() {
        @Suppress("DEPRECATION")
        ropaEdicion = intent.getSerializableExtra("EXTRA_ROPA") as? Ropa

        ropaEdicion?.let { r ->
            binding.tvTituloRopaForm.text = "Editar Prenda"
            binding.btnGuardarRopa.text = "Actualizar Prenda"
            binding.btnEliminarRopa.visibility = View.VISIBLE

            binding.etModelo.setText(r.modelo)
            binding.actvCategoria.setText(r.categoriaNombre, false)
            binding.actvTalla.setText(r.talla, false)
            binding.etMarca.setText(r.marca)
            binding.etColor.setText(r.color)
            binding.etPrecio.setText(r.precio.toString())
            binding.etCantidad.setText(r.cantidad.toString())

            if (r.foto.isNotEmpty() && File(r.foto).exists()) {
                rutaFotoSeleccionada = r.foto
                binding.ivFotoPrenda.setImageBitmap(BitmapFactory.decodeFile(r.foto))
                binding.layoutPlaceholderFoto.visibility = View.GONE
            }
        }
    }

    private fun setupListeners() {
        binding.btnBackRopaForm.setOnClickListener { finish() }

        binding.cardSeleccionarFoto.setOnClickListener {
            seleccionarFotoLauncher.launch("image/*")
        }

        binding.btnGuardarRopa.setOnClickListener {
            guardarOActualizar()
        }
    }

    private fun copiarFotoAAlmacenamientoInterno(uri: Uri) {
        try {
            val carpetaFotos = File(filesDir, "ropa_fotos")
            if (!carpetaFotos.exists()) carpetaFotos.mkdirs()

            val archivoDestino = File(carpetaFotos, "prenda_${System.currentTimeMillis()}.jpg")
            contentResolver.openInputStream(uri)?.use { entrada ->
                FileOutputStream(archivoDestino).use { salida ->
                    entrada.copyTo(salida)
                }
            }
            rutaFotoSeleccionada = archivoDestino.absolutePath
            binding.ivFotoPrenda.setImageBitmap(BitmapFactory.decodeFile(rutaFotoSeleccionada))
            binding.layoutPlaceholderFoto.visibility = View.GONE
        } catch (e: Exception) {
            Toast.makeText(this, "Error al guardar foto: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun guardarOActualizar() {
        val modelo = binding.etModelo.text?.toString()?.trim().orEmpty()
        val catNombre = binding.actvCategoria.text?.toString()?.trim().orEmpty()
        val talla = binding.actvTalla.text?.toString()?.trim().orEmpty()
        val marca = binding.etMarca.text?.toString()?.trim().orEmpty()
        val color = binding.etColor.text?.toString()?.trim().orEmpty()
        val precioStr = binding.etPrecio.text?.toString()?.trim().orEmpty()
        val cantStr = binding.etCantidad.text?.toString()?.trim().orEmpty()

        // Validaciones (HU-05 CA3)
        var hayError = false
        if (modelo.isEmpty()) {
            binding.tilModelo.error = "Ingrese el modelo de la prenda"
            hayError = true
        } else {
            binding.tilModelo.error = null
        }

        val categoria = categorias.find { it.nombre.equals(catNombre, ignoreCase = true) }
        if (categoria == null) {
            binding.tilCategoria.error = "Seleccione una categoría válida"
            hayError = true
        } else {
            binding.tilCategoria.error = null
        }

        if (talla.isEmpty()) {
            binding.tilTalla.error = "Seleccione una talla"
            hayError = true
        } else {
            binding.tilTalla.error = null
        }

        val precio = precioStr.toDoubleOrNull() ?: 0.0
        if (precio <= 0) {
            binding.tilPrecio.error = "El precio debe ser mayor a 0"
            hayError = true
        } else {
            binding.tilPrecio.error = null
        }

        val cantidad = cantStr.toIntOrNull() ?: -1
        if (cantidad < 0) {
            binding.tilCantidad.error = "La cantidad no puede ser negativa"
            hayError = true
        } else {
            binding.tilCantidad.error = null
        }

        if (hayError) return

        val nuevaRopa = Ropa(
            id = ropaEdicion?.id ?: 0,
            modelo = modelo,
            idCategoria = categoria!!.id,
            talla = talla,
            marca = marca,
            color = color,
            precio = precio,
            cantidad = cantidad,
            foto = rutaFotoSeleccionada
        )

        val idInsertado = ropaDao.insertar(nuevaRopa)
        if (idInsertado > 0) {
            Toast.makeText(this, "Prenda registrada con éxito", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error al registrar la prenda", Toast.LENGTH_SHORT).show()
        }
    }
}
