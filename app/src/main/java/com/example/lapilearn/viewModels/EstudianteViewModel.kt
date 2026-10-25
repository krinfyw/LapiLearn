package com.example.lapilearn.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lapilearn.data.repository.EstudianteRepository
import com.example.lapilearn.model.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale

class EstudianteViewModel(
    application: Application,
    private val repository: EstudianteRepository
) : AndroidViewModel(application) {
    constructor(application: Application) : this(application, EstudianteRepository(application))

    private val _uiState = MutableStateFlow(EstudianteUiState())
    val uiState = _uiState.asStateFlow()

    fun onCodigoChange(valor: String) {
        if (_uiState.value.cargando) return
        _uiState.update {
            it.copy(
                codigo = valor.uppercase(Locale.ROOT),
                errores = EstudianteErrores(),
                error = null
            )
        }
    }

    fun ingresar(onExito: () -> Unit) {
        if (_uiState.value.cargando) return
        if (_uiState.value.codigo.isBlank()) {
            _uiState.update { it.copy(errores = EstudianteErrores("Ingresa el código de tu actividad.")) }
            return
        }
        ejecutar(errorDeCodigo = true) {
            mostrar(repository.buscarSesion(_uiState.value.codigo))
            onExito()
        }
    }

    private fun mostrar(sesion: SesionEstudiante) {
        _uiState.update {
            it.copy(
                sesion = sesion,
                seleccion = null,
                confirmar = false,
                preguntaRespondida = null,
                paso = if (sesion.finalizada) PasoEstudiante.RESULTADO else PasoEstudiante.INSTRUCCIONES
            )
        }
    }

    fun comenzar() {
        _uiState.update { it.copy(paso = PasoEstudiante.PREGUNTA) }
    }

    fun seleccionar(indice: Int) {
        val estado = _uiState.value
        if (estado.cargando || estado.paso != PasoEstudiante.PREGUNTA || estado.confirmar) return
        if (indice !in (estado.preguntaActual?.alternativas?.indices ?: return)) return
        _uiState.update { it.copy(seleccion = indice, error = null) }
    }

    fun solicitarConfirmacion() {
        if (_uiState.value.seleccion != null && !_uiState.value.cargando)
            _uiState.update { it.copy(confirmar = true) }
    }

    fun cancelarConfirmacion() {
        if (!_uiState.value.cargando) _uiState.update { it.copy(confirmar = false) }
    }

    fun confirmarRespuesta() {
        val estado = _uiState.value
        if (estado.cargando || !estado.confirmar) return
        val sesion = estado.sesion ?: return
        val pregunta = estado.preguntaActual ?: return
        val seleccion = estado.seleccion ?: return
        ejecutar {
            val actualizada = repository.guardarRespuesta(sesion.intento.id, pregunta.id, seleccion)
            _uiState.update {
                it.copy(
                    sesion = actualizada, confirmar = false,
                    preguntaRespondida = pregunta, paso = PasoEstudiante.RETROALIMENTACION
                )
            }
        }
    }

    fun siguiente() {
        _uiState.update {
            it.copy(
                seleccion = null, preguntaRespondida = null,
                paso = if (it.sesion?.finalizada == true) PasoEstudiante.RESULTADO else PasoEstudiante.PREGUNTA
            )
        }
    }

    fun reintentar() {
        val id = _uiState.value.sesion?.intento?.id ?: return
        ejecutar { mostrar(repository.crearReintento(id)) }
    }

    fun salir() {
        if (_uiState.value.cargando) return
        _uiState.value = EstudianteUiState()
    }

    private fun ejecutar(errorDeCodigo: Boolean = false, accion: suspend () -> Unit) {
        if (_uiState.value.cargando) return
        _uiState.update { it.copy(cargando = true, error = null, errores = EstudianteErrores()) }
        viewModelScope.launch {
            try {
                accion()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val mensaje = if (e is IllegalArgumentException) e.message
                    ?: "Revisa los datos." else "No pudimos guardar o cargar los datos. Intenta nuevamente."
                _uiState.update {
                    if (errorDeCodigo) it.copy(errores = EstudianteErrores(mensaje)) else it.copy(
                        error = mensaje,
                        confirmar = false
                    )
                }
            } finally {
                _uiState.update { it.copy(cargando = false) }
            }
        }
    }

}
