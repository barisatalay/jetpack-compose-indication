package com.barisatalay.jetpackcomposeindication.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomRipple2Screen() {
    Box(
        Modifier
            .background(Color.White)
            .fillMaxSize()
            .clickableWithOpaqueText {}
    ) {
        Text(
            text = "Custom Ripple 2 Example \n\n (ios UIButton)",
            textAlign = TextAlign.Center,
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