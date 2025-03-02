package com.example.bemajudar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ProgressIndicators(currentStep: Int, totalSteps: Int, activeColor: Color, inactiveColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        for (step in 1..totalSteps) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .padding(4.dp)
                    .background(
                        color = if (step == currentStep) activeColor else inactiveColor,
                        shape = CircleShape
                    )
            )
        }
    }
}
