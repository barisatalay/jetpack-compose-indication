package com.barisatalay.jetpackcomposeindication.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ripple
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RippleScreen() {
    Box(
        Modifier
            .background(Color.Cyan)
            .fillMaxSize()
            .clickable(
                onClick = {},
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple()
            )
    ) {
        Text(
            text = "Ripple Example",
            fontSize = 25.sp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 100.dp)
        )
        Text(
            text = "Click me",
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}