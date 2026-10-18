package com.example.lapilearn.data

import com.example.lapilearn.model.Actividad
import com.example.lapilearn.model.Asignacion
import com.example.lapilearn.model.Dificultad
import com.example.lapilearn.model.EstadoActividad
import com.example.lapilearn.model.EstadoAsignacion
import com.example.lapilearn.model.Modulo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate

/**
 * Base de prueba en memoria (sin red, sin datos reales).
 * Es un singleton para que docente, asistente y estudiante vean los mismos datos.
 *
 * Puntos de integración:
 *  - Docente: puede leer/editar [actividades] (agregar a futuro con su propia función).
 *  - Estudiante: usa [buscarPorCodigo] para validar el código temporal.
 */
object RepositorioActividades {

    private const val ALFABETO_CODIGO = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789" // sin 0/O/1/I
    private const val LARGO_CODIGO = 6

    private val _actividades = MutableStateFlow(actividadesDePrueba())
    val actividades: StateFlow<List<Actividad>> = _actividades.asStateFlow()

    private val _asignaciones = MutableStateFlow(asignacionesDePrueba())
    val asignaciones: StateFlow<List<Asignacion>> = _asignaciones.asStateFlow()

    fun crearAsignacion(
        actividad: Actividad,
        aliasEstudiante: String,
        asistente: String
    ): Asignacion {
        val nueva = Asignacion(
            id = (_asignaciones.value.maxOfOrNull { it.id } ?: 0) + 1,
            aliasEstudiante = aliasEstudiante.trim(),
            actividadId = actividad.id,
            nombreActividad = actividad.nombre,
            modulo = actividad.modulo,
            curso = actividad.curso,
            dificultad = actividad.dificultad,
            asistenteResponsable = asistente,
            fecha = LocalDate.now().toString(),
            codigo = generarCodigoUnico()
        )
        _asignaciones.update { it + nueva }
        return nueva
    }

    fun registrarObservacion(asignacionId: Int, texto: String) {
        _asignaciones.update { lista ->
            lista.map { if (it.id == asignacionId) it.copy(observacion = texto.trim()) else it }
        }
    }

    fun buscarPorCodigo(codigo: String): Asignacion? =
        _asignaciones.value.firstOrNull { it.codigo.equals(codigo.trim(), ignoreCase = true) }

    private fun generarCodigoUnico(): String {
        val usados = _asignaciones.value.map { it.codigo }.toSet()
        while (true) {
            val candidato = (1..LARGO_CODIGO).map { ALFABETO_CODIGO.random() }.joinToString("")
            if (candidato !in usados) return candidato
        }
    }

    // ---------- Datos ficticios ----------

    private fun actividadesDePrueba(): List<Actividad> = listOf(
        // Secuencia de Lectura
        Actividad(1, "Vocabulario: animales", Modulo.LECTURA, 1, "Vocabulario",
            "Reconocer el significado de palabras de uso cotidiano.", Dificultad.BASICA,
            "Lee la palabra y elige la imagen o definición correcta.", 5, 2, EstadoActividad.DISPONIBLE),
        Actividad(2, "El gato y la luna", Modulo.LECTURA, 2, "Comprensión explícita",
            "Localizar información explícita en un texto breve.", Dificultad.BASICA,
            "Lee el cuento y responde las preguntas.", 5, 2, EstadoActividad.DISPONIBLE),
        Actividad(3, "El bosque encantado", Modulo.LECTURA, 2, "Comprensión implícita",
            "Inferir información que no está escrita directamente.", Dificultad.MEDIA,
            "Lee con calma y piensa qué quiere decir el texto.", 6, 2, EstadoActividad.DISPONIBLE),
        Actividad(4, "La feria del pueblo", Modulo.LECTURA, 3, "Comprensión implícita",
            "Inferir causas y emociones de los personajes.", Dificultad.MEDIA,
            "Lee el texto y elige la mejor respuesta.", 6, 3, EstadoActividad.DISPONIBLE),
        Actividad(5, "Un viaje al volcán", Modulo.LECTURA, 4, "Comprensión y vocabulario",
            "Relacionar ideas del texto y usar el contexto para entender palabras.", Dificultad.AVANZADA,
            "Lee el texto completo antes de responder.", 8, 3, EstadoActividad.DISPONIBLE),
        // Secuencia de Matemática
        Actividad(6, "Contar hasta 20", Modulo.MATEMATICA, 1, "Numeración",
            "Contar y comparar cantidades hasta 20.", Dificultad.BASICA,
            "Cuenta los objetos y elige el número.", 5, 2, EstadoActividad.DISPONIBLE),
        Actividad(7, "Sumas hasta 100", Modulo.MATEMATICA, 2, "Operatoria",
            "Resolver sumas con números hasta 100.", Dificultad.MEDIA,
            "Resuelve la suma y elige el resultado.", 6, 2, EstadoActividad.BORRADOR),
        Actividad(8, "Restas con canje", Modulo.MATEMATICA, 3, "Operatoria",
            "Resolver restas con reagrupación.", Dificultad.MEDIA,
            "Resuelve la resta paso a paso.", 6, 3, EstadoActividad.DISPONIBLE),
        Actividad(9, "Problemas de reparto", Modulo.MATEMATICA, 4, "Resolución de problemas",
            "Resolver problemas de reparto equitativo.", Dificultad.AVANZADA,
            "Lee el problema, identifica los datos y resuelve.", 6, 3, EstadoActividad.DISPONIBLE),
        Actividad(10, "Tablas del 2 y del 5", Modulo.MATEMATICA, 3, "Multiplicación",
            "Memorizar y aplicar las tablas del 2 y del 5.", Dificultad.BASICA,
            "Completa la multiplicación.", 8, 2, EstadoActividad.DESACTIVADA)
    )

    private fun asignacionesDePrueba(): List<Asignacion> {
        val hoy = LocalDate.now().toString()
        return listOf(
            Asignacion(1, "Estudiante A", 3, "El bosque encantado", Modulo.LECTURA, 2,
                Dificultad.MEDIA, CuentasDePrueba.ASISTENTE_ALIAS, hoy, "K7M4QX",
                EstadoAsignacion.FINALIZADA, aciertos = 4, errores = 2, intentos = 2,
                observacion = "Se concentró bien. Le cuesta cuando el texto es largo."),
            Asignacion(2, "Estudiante B", 8, "Restas con canje", Modulo.MATEMATICA, 3,
                Dificultad.MEDIA, CuentasDePrueba.ASISTENTE_ALIAS, hoy, "R2TZ9P",
                EstadoAsignacion.EN_DESARROLLO, aciertos = 2, errores = 3, intentos = 1),
            Asignacion(3, "Estudiante C", 1, "Vocabulario: animales", Modulo.LECTURA, 1,
                Dificultad.BASICA, CuentasDePrueba.ASISTENTE_ALIAS, hoy, "H5WN3D")
        )
    }
}
