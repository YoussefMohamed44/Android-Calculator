package com.example.calculator

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.sqrt

class GeometricActivity : AppCompatActivity() {

    private lateinit var rgOperation: RadioGroup
    private lateinit var rgShape: RadioGroup
    private lateinit var inputsContainer: View
    private lateinit var etInput1: EditText
    private lateinit var etInput2: EditText
    private lateinit var etInput3: EditText
    private lateinit var btnCalculate: Button
    private lateinit var btnBack: Button
    private lateinit var tvResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geometric)

        rgOperation = findViewById(R.id.rg_operation)
        rgShape = findViewById(R.id.rg_shape)
        inputsContainer = findViewById(R.id.inputs_container)
        etInput1 = findViewById(R.id.et_input1)
        etInput2 = findViewById(R.id.et_input2)
        etInput3 = findViewById(R.id.et_input3)
        btnCalculate = findViewById(R.id.btn_calculate_geo)
        btnBack = findViewById(R.id.btn_back)
        tvResult = findViewById(R.id.tv_geo_result)

        setupListeners()
    }

    private fun setupListeners() {
        rgOperation.setOnCheckedChangeListener { _, _ -> updateUI() }
        rgShape.setOnCheckedChangeListener { _, _ -> updateUI() }
        
        btnCalculate.setOnClickListener { calculate() }
        btnBack.setOnClickListener { finish() }
    }

    private fun updateUI() {
        tvResult.text = ""
        val shapeId = rgShape.checkedRadioButtonId
        
        if (shapeId == -1) {
            inputsContainer.visibility = View.GONE
            return
        }

        inputsContainer.visibility = View.VISIBLE
        etInput1.text.clear()
        etInput2.text.clear()
        etInput3.text.clear()

        when (shapeId) {
            R.id.rb_square -> {
                etInput1.visibility = View.VISIBLE
                etInput1.hint = "Side Length"
                etInput2.visibility = View.GONE
                etInput3.visibility = View.GONE
            }
            R.id.rb_rectangle -> {
                etInput1.visibility = View.VISIBLE
                etInput1.hint = "Length"
                etInput2.visibility = View.VISIBLE
                etInput2.hint = "Width"
                etInput3.visibility = View.GONE
            }
            R.id.rb_triangle -> {
                etInput1.visibility = View.VISIBLE
                etInput2.visibility = View.VISIBLE
                etInput3.visibility = View.GONE // Default hidden

                val opId = rgOperation.checkedRadioButtonId
                if (opId == R.id.rb_area) {
                    etInput1.hint = "Base"
                    etInput2.hint = "Height"
                } else {
                    etInput1.hint = "Side 1"
                    etInput2.hint = "Side 2"
                    etInput3.visibility = View.VISIBLE
                    etInput3.hint = "Side 3"
                }
            }
        }
    }

    private fun calculate() {
        val opId = rgOperation.checkedRadioButtonId
        val shapeId = rgShape.checkedRadioButtonId

        if (opId == -1 || shapeId == -1) {
            Toast.makeText(this, "Please select operation and shape", Toast.LENGTH_SHORT).show()
            return
        }

        val val1 = etInput1.text.toString().toDoubleOrNull()
        val val2 = etInput2.text.toString().toDoubleOrNull()
        val val3 = etInput3.text.toString().toDoubleOrNull()

        var result = 0.0
        val isArea = opId == R.id.rb_area

        try {
            when (shapeId) {
                R.id.rb_square -> {
                    if (val1 == null) throw IllegalArgumentException("Enter Side Length")
                    if (isArea) {
                        result = val1 * val1
                    } else {
                        result = 4 * val1
                    }
                }
                R.id.rb_rectangle -> {
                    if (val1 == null || val2 == null) throw IllegalArgumentException("Enter Length and Width")
                    if (isArea) {
                        result = val1 * val2
                    } else {
                        result = 2 * (val1 + val2)
                    }
                }
                R.id.rb_triangle -> {
                    if (isArea) {
                        if (val1 == null || val2 == null) throw IllegalArgumentException("Enter Base and Height")
                        result = 0.5 * val1 * val2
                    } else {
                        if (val1 == null || val2 == null || val3 == null) throw IllegalArgumentException("Enter all 3 sides")
                        result = val1 + val2 + val3
                    }
                }
            }
            
            val opText = if (isArea) "Area" else "Perimeter"
            val resultLong = result.toLong()
            val resultString = if (result == resultLong.toDouble()) resultLong.toString() else result.toString()
            
            tvResult.text = "$opText: $resultString"

        } catch (e: IllegalArgumentException) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }
}
