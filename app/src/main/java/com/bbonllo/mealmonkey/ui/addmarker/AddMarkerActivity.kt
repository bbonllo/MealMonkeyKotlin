package com.bbonllo.mealmonkey.ui.addmarker

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bbonllo.mealmonkey.databinding.ActivityAddMarkerBinding
import com.bbonllo.mealmonkey.databinding.DialogCreateTagBinding
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener

class AddMarkerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddMarkerBinding
    private var selectedColor: Int = Color.BLUE // Color por defecto
    private val selectedTags = mutableListOf<Pair<String, Int>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMarkerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTagSystem()
        setupSwitch()
        setupSaveButton()
        loadExistingTags() // Cargar tags existentes al iniciar
    }

    private fun setupTagSystem() {
        binding.btnCreateTag.setOnClickListener {
            showCreateTagDialog()
        }
    }

    private fun loadExistingTags() {
        // Ejemplo con tags de prueba - reemplaza con tu lógica real
        val exampleTags = mapOf(
            "Restaurante" to Color.RED,
            "Cafetería" to Color.BLUE,
            "Bar" to Color.GREEN,
            "Vegetariano" to Color.YELLOW,
            "Takeaway" to Color.CYAN
        )

        exampleTags.forEach { (name, color) ->
            addTagToAllGroup(name, color)
        }
    }

    private fun showCreateTagDialog() {
        val dialogBinding = DialogCreateTagBinding.inflate(LayoutInflater.from(this))
        var currentColor = selectedColor

        // Configuración del ColorPicker
        dialogBinding.colorPickerView.apply {
            setColorListener(object : ColorEnvelopeListener {
                override fun onColorSelected(envelope: ColorEnvelope, fromUser: Boolean) {
                    currentColor = envelope.color
                    updateColorPreview(dialogBinding, currentColor)
                }
            })
            setInitialColor(currentColor)
        }

        // Configuración del Slider de brillo
        dialogBinding.brightnessSlider.addOnChangeListener { _, value, _ ->
            currentColor = adjustBrightness(dialogBinding.colorPickerView.getColor(), value / 100f)
            updateColorPreview(dialogBinding, currentColor)
        }

        // Configuración inicial
        updateColorPreview(dialogBinding, currentColor)

        MaterialAlertDialogBuilder(this)
            .setTitle("Crear nueva etiqueta")
            .setView(dialogBinding.root)
            .setPositiveButton("Guardar") { _, _ ->
                val tagName = dialogBinding.editTagName.text.toString()
                if (tagName.isNotEmpty()) {
                    selectedColor = currentColor
                    addNewTagToGroup(tagName, selectedColor)
                } else {
                    Toast.makeText(this, "Ingresa un nombre para la etiqueta", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun updateColorPreview(dialogBinding: DialogCreateTagBinding, color: Int) {
        dialogBinding.colorPreview.setBackgroundColor(color)

        // Opcional: Cambiar el color del slider para mejor visibilidad
        val thumbColor = if (calculateLuminosity(color) > 180) Color.BLACK else Color.WHITE
        dialogBinding.brightnessSlider.setThumbTintList(ColorStateList.valueOf(thumbColor))
        dialogBinding.brightnessSlider.setTrackTintList(ColorStateList.valueOf(color))
    }

    private fun addTagToAllGroup(name: String, color: Int) {
        val chip = Chip(this).apply {
            text = name
            setTextColor(getContrastColor(color))
            chipBackgroundColor = ColorStateList.valueOf(color)
            chipStrokeColor = ColorStateList.valueOf(getContrastStrokeColor(color))
            chipStrokeWidth = 1f
            setEnsureMinTouchTargetSize(false)

            setOnClickListener {
                if (!selectedTags.any { it.first == name }) {
                    selectedTags.add(name to color)
                    addTagToSelectedGroup(name, color)
                }
            }
        }
        binding.allChipGroup.addView(chip)
    }

    private fun addTagToSelectedGroup(name: String, color: Int) {
        val chip = Chip(this).apply {
            text = name
            setTextColor(getContrastColor(color))
            chipBackgroundColor = ColorStateList.valueOf(color)
            chipStrokeColor = ColorStateList.valueOf(getSelectionStrokeColor(color))
            chipStrokeWidth = 2f
            isCloseIconVisible = true

            setOnCloseIconClickListener {
                selectedTags.removeIf { it.first == name }
                binding.selectedChipGroup.removeView(this)
            }
        }
        binding.selectedChipGroup.addView(chip)
    }

    private fun addNewTagToGroup(tagName: String, color: Int) {
        // Añadir a ambos grupos
        addTagToAllGroup(tagName, color)
        if (!selectedTags.any { it.first == tagName }) {
            selectedTags.add(tagName to color)
            addTagToSelectedGroup(tagName, color)
        }
    }

    private fun adjustBrightness(color: Int, factor: Float): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hsv[2] = hsv[2] * factor.coerceIn(0f, 1f)
        return Color.HSVToColor(hsv)
    }

    private fun getContrastStrokeColor(color: Int): Int {
        return if (calculateLuminosity(color) > 180) {
            darkenColor(color, 0.5f)
        } else {
            lightenColor(color, 0.5f)
        }
    }

    private fun getSelectionStrokeColor(color: Int): Int {
        val luminosity = calculateLuminosity(color)
        return when {
            luminosity > 200 -> Color.BLACK
            luminosity < 50 -> Color.WHITE
            else -> if (isSystemInDarkMode()) Color.WHITE else Color.BLACK
        }
    }

    private fun lightenColor(color: Int, factor: Float): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hsv[2] = (hsv[2] + (1 - hsv[2]) * factor).coerceAtMost(1f)
        return Color.HSVToColor(hsv)
    }

    private fun isSystemInDarkMode(): Boolean {
        return resources.configuration.uiMode and
                android.content.res.Configuration.UI_MODE_NIGHT_MASK ==
                android.content.res.Configuration.UI_MODE_NIGHT_YES
    }

    private fun calculateLuminosity(color: Int): Int {
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        return (0.299 * r + 0.587 * g + 0.114 * b).toInt()
    }

    private fun darkenColor(color: Int, factor: Float = 0.8f): Int {
        require(factor in 0f..1f) { "Factor must be between 0 and 1" }
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hsv[2] *= (1f - factor.coerceIn(0f, 1f))
        return Color.HSVToColor(hsv)
    }

    private fun getContrastColor(backgroundColor: Int): Int {
        return if (calculateLuminosity(backgroundColor) > 128) Color.BLACK else Color.WHITE
    }

    private fun setupSwitch() {
        binding.switchTryLater.setOnCheckedChangeListener { _, isChecked ->
            binding.textTryLater.text = if (isChecked) "Probado" else "Por probar"
        }
    }

    private fun setupSaveButton() {
        binding.btnAdd.setOnClickListener {
            val name = binding.txtName.text.toString()
            if (name.isBlank()) {
                binding.txtLayoutName.error = "Ingresa un nombre para el lugar"
                return@setOnClickListener
            }

            // Guardar también las etiquetas seleccionadas
            val tags = selectedTags.map { it.first }
            Toast.makeText(this, "Marcador guardado: $name con etiquetas: ${tags.joinToString()}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}