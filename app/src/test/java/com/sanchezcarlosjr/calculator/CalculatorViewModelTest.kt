package com.sanchezcarlosjr.calculator

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule
import org.junit.rules.TestRule

class CalculatorViewModelTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @Test
    fun `it should stream new tokens`() {
        val viewModel = CalculatorViewModel()
        viewModel.stream("5")
        assertEquals("5", viewModel.expression.value)
        viewModel.stream(".")
        assertEquals("5.", viewModel.expression.value)
        viewModel.stream("6")
        assertEquals("5.6", viewModel.expression.value)
    }

    @Test
    fun `it should calculate stream`() {
        val viewModel = CalculatorViewModel()
        viewModel.stream("5.+5.")
        viewModel.stream("=")
        assertEquals(10.0, viewModel.result.value)
        viewModel.stream("A")
        assertEquals(0.0, viewModel.result.value)
        viewModel.stream("6+6")
        viewModel.stream("=")
        assertEquals(12.0, viewModel.result.value)
        viewModel.stream("A")
        viewModel.stream("1+1*2")
        viewModel.stream("=")
        assertEquals(3.0, viewModel.result.value)
        viewModel.stream("A")
        viewModel.stream("1-2")
        viewModel.stream("=")
        assertEquals(-1.0, viewModel.result.value)
        viewModel.stream("A")
        viewModel.stream("1/2+1")
        viewModel.stream("=")
        assertEquals(1.5, viewModel.result.value)
        viewModel.stream("A")
        viewModel.stream("1/2/2")
        viewModel.stream("=")
        assertEquals(0.25, viewModel.result.value)
        viewModel.stream("A")
        viewModel.stream("S")
        viewModel.stream("4")
        viewModel.stream("=")
        assertEquals(2.0, viewModel.result.value)
        viewModel.stream("A")
        viewModel.stream("5/0")
        viewModel.stream("=")
        assertEquals("NaN", viewModel.expression.value)
        assertEquals(0.0, viewModel.result.value)
    }

}