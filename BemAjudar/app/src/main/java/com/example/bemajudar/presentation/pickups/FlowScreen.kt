package com.example.bemajudar.presentation.pickups

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.bemajudar.data.firebase.registerLevantamento

@Composable
fun LevantamentoFlowScreen() {
    var currentStep by remember { mutableStateOf(1) }
    var selectedVisitorId by remember { mutableStateOf<String?>(null) }

    when (currentStep) {
        1 -> SelectVisitorScreen(onNextClick = { visitorId ->
            selectedVisitorId = visitorId
            currentStep = 2
        })

        2 -> selectedVisitorId?.let { visitorId ->
            SelectItemsScreen(visitorId = visitorId) { selectedItems ->
                registerLevantamento(visitorId, selectedItems)
                currentStep = 1 // Voltar ao início após o registo
            }
        }
    }
}
