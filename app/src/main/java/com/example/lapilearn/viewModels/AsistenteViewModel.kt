package com.example.lapilearn.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lapilearn.data.repository.UsuarioRepository
import com.example.lapilearn.model.AsistenteUiState
import com.example.lapilearn.model.AsistenteErrores
import com.example.lapilearn.utils.isValidEmail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AsistenteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UsuarioRepository(application)

    private val _uiState = MutableStateFlow(AsistenteUiState())
    val uiState: StateFlow<AsistenteUiState> = _uiState.asStateFlow()

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
                errores = AsistenteErrores(
                    correo = nuevoCorreo,
                    contrasena = nuevaContrasena
                )
            )
        }
        return !hayErrores
    }

    fun loginAsistente(onExito: () -> Unit) {
        _uiState.update { it.copy(usuarioId = null) }
        if (!validarFormulario()) return

        val estado = _uiState.value
        viewModelScope.launch {
            val asistente = repository.loginAsistente(estado.correo, estado.contrasena)
            if (asistente != null) {
                _uiState.update { it.copy(nombre = asistente.nombre, usuarioId = asistente.id) }
                onExito()
            } else {
                _uiState.update {
                    it.copy(errores = it.errores.copy(contrasena = "Correo o contraseña incorrectos"))
                }
            }
        }
    }

    /** Limpia correo, contraseña y nombre al cerrar sesión. */
    fun cerrarSesion() {
        _uiState.value = AsistenteUiState()
    }
}
