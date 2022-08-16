package com.sanchezcarlosjr.calculator

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule
import org.junit.rules.TestRule

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class SimpleViewModelUnitTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    var viewModel = CalculatorViewModel()
    @Test
    fun onLikeUpdateLikes() {
        assertEquals(0, viewModel.likes.value)
        viewModel.onLike()
        assertEquals(1, viewModel.likes.value)
    }
}