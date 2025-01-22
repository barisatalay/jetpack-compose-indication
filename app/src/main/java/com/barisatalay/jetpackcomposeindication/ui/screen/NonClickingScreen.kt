@file:Suppress("DEPRECATION_ERROR")

package com.barisatalay.jetpackcomposeindication.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun NonClickingScreen() {
    val configuration = LocalConfiguration.current
    val scope = rememberCoroutineScope()
    val interactionSource = remember { MutableInteractionSource() }
    val indication = rememberRipple(
        // if set false, the ripple effect will not be cropped to the button.
        bounded = true,
        // if you want to spread to stop right at the edges of your targeted composable,
        // take the longest side divided by two, in this case the row width is the screen width.
        radius = configuration.screenWidthDp.dp / 2,
    )
    // used later to change Dp unit to Pixels using .toPx()
    val density = LocalDensity.current

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column {
            Text(
                text = "Without Clicking Example",
                fontSize = 25.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(Color.Cyan)
                    .padding(top = 100.dp)
                    .weight(1f)
                    .fillMaxWidth()
                    .indication(interactionSource, indication)
            )

            Text(
                text = "Click Me! \nTriggering the ripple effect without content clicking",
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(0.25f)
                    .padding(top = 20.dp)
                    .fillMaxWidth()
                    .clickable(
                        indication = null,
                        interactionSource = null,
                    ) {
                        with(density) {
                            scope.launch {
                                // wait a little bit for user to focus on the screen
                                delay(800)
                                val centerX = configuration.screenWidthDp.dp / 2
                                val centerY = 64.dp / 2
                                // simulate a press for the targeted setting tile
                                val press = PressInteraction.Press(Offset(centerX.toPx(), centerY.toPx()))
                                interactionSource.emit(press)
                                // wait a little bit for the effect to animate
                                delay(400)
                                // release the effect
                                val release = PressInteraction.Release(press)
                                interactionSource.emit(release)
                            }
                        }
                    }
            )
        }
    }
}

@Preview
@Composable
fun NonClickingScreenPreview() {
    NonClickingScreen()
}