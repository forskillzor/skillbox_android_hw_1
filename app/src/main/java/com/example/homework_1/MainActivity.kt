package com.example.homework_1

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.homework_1.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val counter = MutableStateFlow(0)
        val text = MutableStateFlow("")
        val minusButtonEnabled = MutableStateFlow(false)
        val plusButtonEnabled = MutableStateFlow(false)
        val resetButtonVisibility = MutableStateFlow(View.INVISIBLE)

        binding.textView.setText(R.string.main_text_empty)

        val max = 49

        binding.plusButton.setOnClickListener {
            counter.value++
            if (counter.value > max + 1)
                counter.value = max + 1
        }

        binding.minusButton.setOnClickListener {
            if (counter.value == 0) {
                counter.value = 0
            } else
                counter.value--
        }

        val scopeMinusButton = CoroutineScope(Dispatchers.Main)
        scopeMinusButton.launch {
            minusButtonEnabled.collect {
                when (it) {
                    true -> binding.minusButton.isEnabled = true
                    false -> binding.minusButton.isEnabled = false
                }
            }
        }

        val scopePlusButton = CoroutineScope(Dispatchers.Main)
        scopePlusButton.launch {
            plusButtonEnabled.collect{
                when (it) {
                    true -> binding.plusButton.isEnabled = true
                    false -> binding.plusButton.isEnabled = false
                }
            }
        }

        val scopeResetButton = CoroutineScope(Dispatchers.Main)
        scopeResetButton.launch {
            resetButtonVisibility.collect {
                when (it) {
                    View.VISIBLE -> binding.resetButton.visibility = View.VISIBLE
                    View.INVISIBLE -> binding.resetButton.visibility = View.INVISIBLE
                }
            }
        }

        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            counter.collect {
                binding.counter.text = counter.value.toString()
                when {
                    it == 0 -> {
                        binding.textView.setText(R.string.main_text_empty)
                        binding.textView.setTextColor(Color.GREEN)
                        minusButtonEnabled.value = false
                        plusButtonEnabled.value = true
                    }

                    it in 1..max -> {
                        binding.textView.text =
                            getString(R.string.main_text_remain, (max - counter.value))
                        binding.textView.setTextColor(Color.BLACK)
                        minusButtonEnabled.value = true
                        plusButtonEnabled.value = true
                        resetButtonVisibility.value = View.INVISIBLE
                    }

                    it > max -> {
                        binding.textView.setText(R.string.main_text_toomuch)
                        binding.textView.setTextColor(Color.RED)
                        plusButtonEnabled.value = false
                        minusButtonEnabled.value = true
                        resetButtonVisibility.value = View.VISIBLE
                    }
                }
            }

        }
    }

}