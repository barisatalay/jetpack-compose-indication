package com.barisatalay.jetpackcomposeindication.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.barisatalay.jetpackcomposeindication.ui.navigation.Screen

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Button(
                modifier = Modifier.height(65.dp),
                onClick = { navController.navigate(route = Screen.RememberRipple.route) }
            ) {
                Text(text = "RememberRipple Screen", textAlign = TextAlign.Center)
            }
        }
        item {
            Button(
                modifier = Modifier.height(65.dp),
                onClick = { navController.navigate(route = Screen.Ripple.route) }
            ) {
                Text(text = "Ripple Screen", textAlign = TextAlign.Center)
            }
        }
        item {
            Button(
                modifier = Modifier.height(65.dp),
                onClick = { navController.navigate(route = Screen.CustomRipple1.route) }
            ) {
                Text(text = "Custom Ripple 1 Screen", textAlign = TextAlign.Center)
            }
        }
        item {
            Button(
                modifier = Modifier.height(65.dp),
                onClick = { navController.navigate(route = Screen.CustomRipple2.route) }
            ) {
                Text(text = "Custom Ripple 2 Screen", textAlign = TextAlign.Center)
            }
        }

        item {
            Button(
                modifier = Modifier.height(65.dp),
                onClick = { navController.navigate(route = Screen.NonClicking.route) }
            ) {
                Text(text = "NonClicking Screen", textAlign = TextAlign.Center)
            }
        }
        item {
            Button(
                modifier = Modifier.height(65.dp),
                onClick = { navController.navigate(route = Screen.Bubbling.route) }
            ) {
                Text(text = "Bubbling Screen", textAlign = TextAlign.Center)
            }
        }
        item {
            Button(
                modifier = Modifier.height(65.dp),
                onClick = { navController.navigate(route = Screen.Snowflakes.route) }
            ) {
                Text(text = "Snowflakes Screen", textAlign = TextAlign.Center)
            }
        }
        item {
            Button(
                modifier = Modifier.height(65.dp),
                onClick = { navController.navigate(route = Screen.Shake1.route) }
            ) {
                Text(text = "Shake 1 Screen", textAlign = TextAlign.Center)
            }
        }
        item {
            Button(
                modifier = Modifier.height(65.dp),
                onClick = { navController.navigate(route = Screen.Shake2.route) }
            ) {
                Text(text = "Shake 2 Screen", textAlign = TextAlign.Center)
            }
        }
        item {
            Button(
                modifier = Modifier.height(65.dp),
                onClick = { navController.navigate(route = Screen.Fillable.route) }
            ) {
                Text(text = "Fillable Screen", textAlign = TextAlign.Center)
            }
        }
        item {
            Button(
                modifier = Modifier.height(65.dp),
                onClick = { navController.navigate(route = Screen.Heartbeat.route) }
            ) {
                Text(text = "Heartbeat Screen", textAlign = TextAlign.Center)
            }
        }
    }
}