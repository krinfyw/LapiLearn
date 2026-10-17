package com.example.lapilearn.viewModels

import androidx.lifecycle.ViewModel
import com.example.lapilearn.model.EstudianteUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EstudianteViewModel: ViewModel() {

    private val _uiState = MutableStateFlow(EstudianteUiState())
    val uiState: StateFlow<EstudianteUiState> = _uiState.asStateFlow()

    fun onCodigoChange(valor: String){
        _uiState.update {
            it.copy(
                codigo = valor.uppercase(),
                errores = it.errores.copy(codigo = null)
            )
        }
    }

    // ¿Le agregamos validación?

}