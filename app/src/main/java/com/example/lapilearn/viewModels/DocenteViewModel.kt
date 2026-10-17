package com.example.lapilearn.viewModels
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.lapilearn.model.DocenteUiState
import com.example.lapilearn.model.DocenteErrores
import com.example.lapilearn.utils.isValidEmail
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DocenteViewModel : ViewModel(){

    private val _uiState = MutableStateFlow(DocenteUiState())
    val uiState: StateFlow<DocenteUiState> = _uiState.asStateFlow()

    fun onCorreoChange(valor: String) {
        _uiState.update {
            it.copy(
                correo = valor,
                errores = it.errores.copy(correo = null)
            )
        }
    }

    fun onContrasenaChange(valor: String) {
        _uiState.update {
            it.copy(
                contrasena = valor,
                errores = it.errores.copy(contrasena = null)
            )
        }
    }

    fun validarFormulario(): Boolean {
        val estado = _uiState.value

        val nuevoCorreo = when {
            estado.correo.isBlank() -> "El correo es obligatorio"
            !estado.correo.isValidEmail() -> "El correo no tiene un formato válido"
            else -> null
        }

        val nuevaContrasena = when {
            estado.contrasena.isBlank() -> "La contraseña es obligatoria"
            estado.contrasena.length < 6 -> "Debe tener al menos 6 caracteres"
            else -> null
        }

        val hayErrores = nuevoCorreo != null || nuevaContrasena != null

        _uiState.update {
            it.copy(
                errores = DocenteErrores(
                    correo = nuevoCorreo,
                    contrasena = nuevaContrasena
                )
            )
        }
        return !hayErrores
    }

}