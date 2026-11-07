package com.rehabtrack.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rehabtrack.app.data.local.entity.SesionEntity
import com.rehabtrack.app.data.repository.RehabRepositoryProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface NextSessionUiState {
    data object Loading : NextSessionUiState

    data class Success(
        val sesion: SesionEntity
    ) : NextSessionUiState

    data class Error(
        val mensaje: String
    ) : NextSessionUiState
}

class NextSessionViewModel(application: Application) :
    AndroidViewModel(application) {

    private val provider = RehabRepositoryProvider(application)
    private val sesionRepository = provider.sesionRepository

    private val _uiState =
        MutableStateFlow<NextSessionUiState>(NextSessionUiState.Loading)

    val uiState: StateFlow<NextSessionUiState> =
        _uiState.asStateFlow()

    fun cargarSesion(idSesion: Int) {
        viewModelScope.launch {
            _uiState.value = NextSessionUiState.Loading

            try {
                val sesion = withContext(Dispatchers.IO) {
                    sesionRepository.obtenerSesionPorId(idSesion)
                }

                _uiState.value = if (sesion != null) {
                    NextSessionUiState.Success(sesion)
                } else {
                    NextSessionUiState.Error(
                        "No se encontró la sesión seleccionada."
                    )
                }

            } catch (e: Exception) {
                _uiState.value = NextSessionUiState.Error(
                    e.message ?: "No se pudo cargar la sesión."
                )
            }
        }
    }
}