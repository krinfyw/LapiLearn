package com.example.lapilearn.viewModels
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.lapilearn.viewModels.model.DocenteUiState
import com.example.lapilearn.viewModels.model.DocenteErrores

class DocenteViewModel : ViewModel(){

    private val _uiState = MutableStateFlow(DocenteUiState())
    val uiState: StateFlow<DocenteUiState> = _uiState

    fun onCorreoChange(valor: String) {
        _uiState.value = _uiState.value.copy(
            correo = valor,
            errores = _uiState.value.errores.copy(correo = null)
        )
    }

    fun onContrasenaChange(valor: String) {
        _uiState.value = _uiState.value.copy(
            contrasena = valor,
            errores = _uiState.value.errores.copy(contrasena = null)
        )
    }

    fun validarFormulario(): Boolean {
        val estado = _uiState.value
        var nuevoCorreo: String? = null
        var nuevaContrasena: String? = null

        if (estado.correo.isBlank()) {
            nuevoCorreo = "Campo obligatorio"
        } else if (!estado.correo.contains("@")) {
            nuevoCorreo = "Correo inválido"
        }

        if (estado.contrasena.isBlank()) {
            nuevaContrasena = "Campo obligatorio"
        } else if (estado.contrasena.length < 6) {
            nuevaContrasena = "Debe tener al menos 6 caracteres"
        }

        val hayErrores = nuevoCorreo != null || nuevaContrasena != null

        _uiState.value = _uiState.value.copy(
            errores = DocenteErrores(correo = nuevoCorreo, contrasena = nuevaContrasena)
        )

        return !hayErrores
    }

}