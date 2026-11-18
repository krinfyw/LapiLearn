package com.example.lapilearn

import com.example.lapilearn.data.local.entity.*
import com.example.lapilearn.data.repository.AsignacionRepository
import com.example.lapilearn.model.*
import com.example.lapilearn.utils.AsignacionReglas
import com.example.lapilearn.viewModels.AsistentePanelViewModel
import io.kotest.matchers.shouldBe
import io.mockk.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import kotlinx.coroutines.flow.*
import org.junit.jupiter.api.Test

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
internal class AsistentePanelViewModelTest : ViewModelTestBase() {
    private val repo = mockk<AsignacionRepository>()
    private val actividades = MutableStateFlow(listOf(actividad(), actividad().copy(id = 2, estado = "Borrador"), actividad().copy(id = 3, modulo = "Lectura", curso = "2° básico", dificultad = "Media")))
    private fun TestScope.modelo(): AsistentePanelViewModel {
        every { repo.actividades } returns actividades
        every { repo.asignaciones } returns flowOf(listOf(asignacion()))
        every { repo.intentos } returns flowOf(listOf(intento(fin = 20)))
        every { repo.respuestas } returns flowOf(listOf(RespuestaEntity(1, 1, 1, true, 15)))
        every { repo.preguntas } returns flowOf(listOf(pregunta()))
        val vm = registrar(AsistentePanelViewModel(app, repo))
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.detalle.collect() }
        runCurrent()
        return vm
    }

    @Test
    fun `filtros solo muestran disponibles y pueden alternarse y limpiarse`() = runTest {
        val vm = modelo()
        vm.uiState.value.actividades.map { it.id } shouldBe listOf(1, 3)
        vm.uiState.value.hayFiltros shouldBe false
        vm.onModuloChange("Lectura")
        runCurrent()
        vm.uiState.value.actividades.map { it.id } shouldBe listOf(3)
        vm.onModuloChange("Lectura")
        vm.onCursoChange("1° básico")
        vm.onDificultadChange("Fácil")
        runCurrent()
        vm.uiState.value.actividades.map { it.id } shouldBe listOf(1)
        vm.uiState.value.hayFiltros shouldBe true
        vm.onCursoChange("1° básico")
        vm.onDificultadChange("Fácil")
        vm.onModuloChange("Matemática")
        vm.limpiarFiltros()
        runCurrent()
        vm.uiState.value.hayFiltros shouldBe false
        vm.verRespuestas(vm.uiState.value.asignaciones.single())
        runCurrent()
        vm.detalle.value.single().respuestas.single().esCorrecta shouldBe true
    }

    @Test
    fun `asignacion valida alias limita texto y evita duplicar codigo`() = runTest {
        coEvery { repo.crearAsignacion(1, any(), "Asistente") } returns "XYZ234"
        val vm = modelo()
        vm.confirmarAsignacion("Asistente")
        vm.abrirAsignacion(actividad())
        vm.confirmarAsignacion("Asistente")
        runCurrent()
        (vm.uiState.value.errorAlias != null) shouldBe true
        vm.onAliasChange("a".repeat(30))
        runCurrent()
        vm.uiState.value.alias.length shouldBe AsignacionReglas.MAX_ALIAS
        vm.onAliasChange("Alumno ficticio")
        vm.confirmarAsignacion("Asistente")
        vm.confirmarAsignacion("Asistente")
        runCurrent()
        vm.uiState.value.codigoGenerado shouldBe "XYZ234"
        vm.uiState.value.guardando shouldBe false
        vm.confirmarAsignacion("Asistente")
        coVerify(exactly = 1) { repo.crearAsignacion(any(), any(), any()) }
        vm.cerrarAsignacion()
        runCurrent()
        vm.uiState.value.actividadSeleccionada shouldBe null
        vm.uiState.value.codigoGenerado shouldBe null
    }

}
