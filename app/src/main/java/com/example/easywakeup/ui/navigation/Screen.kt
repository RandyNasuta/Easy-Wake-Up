package com.example.easywakeup.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home_screen")
    object Alarm : Screen("alarm_screen/{alarmId}") {
        fun createRoute(alarmId: Long = -1L): String {
            return "alarm_screen/$alarmId"
        }
    }
}