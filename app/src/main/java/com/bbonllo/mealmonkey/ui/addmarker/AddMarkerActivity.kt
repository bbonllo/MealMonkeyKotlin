package com.bbonllo.mealmonkey.ui.addmarker

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import com.bbonllo.mealmonkey.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.materialswitch.MaterialSwitch


class AddMarkerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_marker)

        val txtName = findViewById<EditText>(R.id.txt_name)
        val txtAddress = findViewById<EditText>(R.id.txt_address)
        val txtComment = findViewById<EditText>(R.id.txt_comment)
        val btnAdd = findViewById<Button>(R.id.btn_add)
        val txtTag = findViewById<TextView>(R.id.txt_tag)
        val switchTry = findViewById<MaterialSwitch>(R.id.switch_try_later)
        val txtTry = findViewById<TextView>(R.id.text_try_later)
        val txtPhone = findViewById<EditText>(R.id.txt_phone)
        val txtWebsite = findViewById<EditText>(R.id.txt_website)
        val txtArticle = findViewById<EditText>(R.id.txt_article)
        val chipScrollView = findViewById<NestedScrollView>(R.id.chip_scroll_view)
        val chipGroup = findViewById<ChipGroup>(R.id.chip_group_tags)

        val typedArray = theme.obtainStyledAttributes(intArrayOf(com.google.android.material.R.attr.colorSecondary))
        val themeSecondaryColor = typedArray.getColor(0, Color.GRAY)
        typedArray.recycle()

        for (i in 1..50) {
            val bgColor = Color.rgb((0..255).random(), (0..255).random(), (0..255).random())
            val strokeColor = Color.rgb(250, 250, 250)

            val chip = Chip(this).apply {
                text = "chip$i"
                isCheckable = true

                chipBackgroundColor = ColorStateList.valueOf(bgColor)
                chipStrokeColor = ColorStateList.valueOf(themeSecondaryColor)
                chipStrokeWidth = 4f

                val luminosity = calculateLuminosity(bgColor)
                setTextColor(if (luminosity > 128) Color.BLACK else Color.WHITE)

                setEnsureMinTouchTargetSize(false)
            }

            chip.setOnCheckedChangeListener { _, _ ->
                val currentBg = chip.chipBackgroundColor?.defaultColor ?: bgColor
                val currentStroke = chip.chipStrokeColor?.defaultColor ?: strokeColor

                // Swap colors
                chip.chipBackgroundColor = ColorStateList.valueOf(currentStroke)
                chip.chipStrokeColor = ColorStateList.valueOf(currentBg)

                // Optional: update text color if background changes significantly
                val newLuminosity = calculateLuminosity(currentStroke)
                chip.setTextColor(if (newLuminosity > 128) Color.BLACK else Color.WHITE)
            }

            chipGroup.addView(chip)
        }

        switchTry.setOnCheckedChangeListener { _, isChecked ->
            txtTry.text = if (isChecked) "Probado" else "Por probar"
        }

        btnAdd.setOnClickListener {
            val name = txtName.text.toString()
            val address = txtAddress.text.toString()
            val comment = txtComment.text.toString()

            Toast.makeText(this, "Marcador añadido: $name", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}

fun calculateLuminosity(color: Int): Int {
    val r = Color.red(color)
    val g = Color.green(color)
    val b = Color.blue(color)

    return (0.299 * r + 0.587 * g + 0.114 * b).toInt()
}