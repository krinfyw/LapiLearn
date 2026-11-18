package com.example.lapilearn

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

@OptIn(ExperimentalCoroutinesApi::class)
internal abstract class ViewModelTestBase {
    protected val app = mockk<Application>()
    protected val dispatcher = StandardTestDispatcher()
    private val modelos = mutableListOf<ViewModel>()

    protected fun <T : ViewModel> registrar(modelo: T): T {
        modelos.add(modelo)
        return modelo
    }

    @BeforeEach
    fun configurarMain() { Dispatchers.setMain(dispatcher) }

    @AfterEach
    fun limpiarMain() {
        modelos.forEach { it.viewModelScope.cancel() }
        Dispatchers.resetMain()
        unmockkAll()
    }
}
