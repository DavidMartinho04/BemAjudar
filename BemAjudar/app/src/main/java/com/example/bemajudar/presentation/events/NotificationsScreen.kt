package com.example.bemajudar.presentation.events

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bemajudar.data.firebase.fetchPendingNotifications
import com.example.bemajudar.data.firebase.updateNotificationState


@Composable
fun NotificationsScreen(userEmail: String) {
    val notificationList = remember { mutableStateListOf<Map<String, Any>>() }
    var filterState by remember { mutableStateOf("Pendente") }
    val borderColor = Color(0xFF025997)

    LaunchedEffect(userEmail) {
        fetchPendingNotifications(userEmail) { notifications ->
            notificationList.clear()
            notificationList.addAll(notifications)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Título Centralizado
        Text(
            "Notificações",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botões de Filtro
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { filterState = "Pendente" },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                border = BorderStroke(2.dp, borderColor)
            ) {
                Text("Pendente", color = borderColor)
            }
            Button(
                onClick = { filterState = "Aceite" },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                border = BorderStroke(2.dp, borderColor)
            ) {
                Text("Aceite", color = borderColor)
            }
            Button(
                onClick = { filterState = "Recusado" },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                border = BorderStroke(2.dp, borderColor)
            ) {
                Text("Recusado", color = borderColor)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filtrar notificações com base no botão selecionado
        val filteredNotifications = notificationList.filter { it["state"] == filterState }

        if (filteredNotifications.isEmpty()) {
            Text("Sem notificações $filterState.", fontWeight = FontWeight.SemiBold)
        } else {
            LazyColumn {
                items(filteredNotifications) { notification ->
                    val notificationId = notification["notificationId"].toString()
                    val notificationState = remember { mutableStateOf(notification["state"].toString()) }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.LightGray)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = notification["title"].toString(),
                                fontWeight = FontWeight.Bold
                            )
                            Text(notification["message"].toString())
                            Text(notification["date"].toString())

                            Spacer(modifier = Modifier.height(8.dp))

                            // Exibir os botões apenas para notificações pendentes
                            if (notificationState.value == "Pendente") {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Button(onClick = {
                                        updateNotificationState(notificationId, "Aceite")
                                        notificationState.value = "Aceite"
                                    }) {
                                        Text("Aceitar")
                                    }
                                    Button(onClick = {
                                        updateNotificationState(notificationId, "Recusado")
                                        notificationState.value = "Recusado"
                                    }) {
                                        Text("Recusar")
                                    }
                                }
                            } else {
                                // Exibir o estado atualizado se já foi alterado
                                Text(
                                    text = "Estado: ${notificationState.value}",
                                    fontWeight = FontWeight.Bold,
                                    color = when (notificationState.value) {
                                        "Aceite" -> Color.Green
                                        "Recusado" -> Color.Red
                                        else -> Color.Black
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

