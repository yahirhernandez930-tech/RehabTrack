package com.rehabtrack.app.ui.viewmodel

import androidx.lifecycle.ViewModel

class NavigationViewModel : ViewModel() {

    var ejerciciosSesion: List<SessionExerciseData> = emptyList()
        private set

    fun guardarEjercicios(ejercicios: List<SessionExerciseData>) {
        ejerciciosSesion = ejercicios
    }

    fun obtenerEjercicio(indice: Int): SessionExerciseData? {
        return ejerciciosSesion.getOrNull(indice)
    }

    fun limpiarEjercicios() {
        ejerciciosSesion = emptyList()
    }
}