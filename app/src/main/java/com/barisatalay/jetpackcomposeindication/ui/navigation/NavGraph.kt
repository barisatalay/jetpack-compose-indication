package com.barisatalay.jetpackcomposeindication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.barisatalay.jetpackcomposeindication.ui.screen.BubblingScreen
import com.barisatalay.jetpackcomposeindication.ui.screen.ChristmasButtonScreen
import com.barisatalay.jetpackcomposeindication.ui.screen.CustomRipple1Screen
import com.barisatalay.jetpackcomposeindication.ui.screen.CustomRipple2Screen
import com.barisatalay.jetpackcomposeindication.ui.screen.FillableScreen
import com.barisatalay.jetpackcomposeindication.ui.screen.HeartbeatScreen
import com.barisatalay.jetpackcomposeindication.ui.screen.MainScreen
import com.barisatalay.jetpackcomposeindication.ui.screen.NonClickingScreen
import com.barisatalay.jetpackcomposeindication.ui.screen.RememberRippleScreen
import com.barisatalay.jetpackcomposeindication.ui.screen.RippleScreen
import com.barisatalay.jetpackcomposeindication.ui.screen.Shake1Screen
import com.barisatalay.jetpackcomposeindication.ui.screen.Shake2Screen
import com.barisatalay.jetpackcomposeindication.ui.screen.SnowflakesScreen


@Composable
fun NavigationStack(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        composable(route = Screen.Main.route) {
            MainScreen(navController = navController)
        }
        composable(route = Screen.ChristmasButton.route) {
            ChristmasButtonScreen()
        }
        composable(route = Screen.RememberRipple.route) {
            RememberRippleScreen()
        }
        composable(route = Screen.Ripple.route) {
            RippleScreen()
        }
        composable(route = Screen.CustomRipple1.route) {
            CustomRipple1Screen()
        }
        composable(route = Screen.CustomRipple2.route) {
            CustomRipple2Screen()
        }
        composable(route = Screen.NonClicking.route) {
            NonClickingScreen()
        }
        composable(route = Screen.Bubbling.route) {
            BubblingScreen()
        }
        composable(route = Screen.Snowflakes.route) {
            SnowflakesScreen()
        }
        composable(route = Screen.Shake1.route) {
            Shake1Screen()
        }
        composable(route = Screen.Shake2.route) {
            Shake2Screen()
        }
        composable(route = Screen.Fillable.route) {
            FillableScreen()
        }
        composable(route = Screen.Heartbeat.route) {
            HeartbeatScreen()
        }
    }
}

sealed class Screen(val route: String) {
    data object Main : Screen("main_screen")
    data object ChristmasButton : Screen("christmas_button_screen")
    data object RememberRipple : Screen("remember_ripple_screen")
    data object Ripple : Screen("ripple_screen")
    data object CustomRipple1 : Screen("custom_ripple1_screen")
    data object CustomRipple2 : Screen("custom_ripple2_screen")
    data object NonClicking : Screen("non_clicking_screen")
    data object Bubbling : Screen("bubling_screen")
    data object Snowflakes : Screen("snowflakes_screen")
    data object Shake1 : Screen("shake1_screen")
    data object Shake2 : Screen("shake2_screen")
    data object Fillable : Screen("fillable_screen")
    data object Heartbeat : Screen("heartbeat_screen")
}