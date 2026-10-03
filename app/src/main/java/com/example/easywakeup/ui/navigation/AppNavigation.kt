package com.example.easywakeup.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.easywakeup.ui.alarm.AlarmScreen
import com.example.easywakeup.ui.home.HomeScreen

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier
) {
    //Navigation control
    val navController = rememberNavController()

    //Set NavHost with first screen is home screen
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        //Route to Home Screen
        composable(
            route = Screen.Home.route,
        ) {
            HomeScreen(
                onclick = {
                    navController.navigate(Screen.Alarm.route)
                }
            )
        }

        //Route to Alarm Screen
        composable(
            route = Screen.Alarm.route,
        ) {
            AlarmScreen()
        }
    }
}