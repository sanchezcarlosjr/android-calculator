package com.sanchezcarlosjr.calculator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CalculatorViewModel : ViewModel() {
    private val _result = MutableLiveData<Float>(0.0F)
    private val _expression = MutableLiveData("")
    private val _state = MutableLiveData(0)

    val result: LiveData<Float> = _result
    val expression: LiveData<String> = _expression

    fun calculate(token: String) {
        _expression.value = String()
    }

    fun calculate(token: Number) {
    }

}
