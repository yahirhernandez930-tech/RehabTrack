package com.rehabtrack.app.navigation

sealed class Routes(val route: String) {

    data object Login : Routes("login")
    data object Register : Routes("register")

    data object Home : Routes("home")
    data object Progress : Routes("progress")
    data object History : Routes("history")
    data object Profile : Routes("profile")

    data object SessionDetail : Routes("session_detail")

    data object ChangeEmail : Routes("change_email")
    data object ChangePassword : Routes("change_password")

    data object NextSession : Routes("next_session")
    data object SessionConfirmation : Routes("session_confirmation")
    data object ExerciseInfo : Routes("exercise_info")
    data object Exercise : Routes("exercise")
    data object ExerciseConfirmation : Routes("exercise_confirmation")
    data object SessionCompletion : Routes("session_completion")
}