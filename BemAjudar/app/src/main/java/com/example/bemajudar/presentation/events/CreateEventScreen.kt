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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.*
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.bemajudar.data.firebase.saveEventToFirebase
import com.example.bemajudar.data.firebase.fetchVolunteers
import com.example.bemajudar.presentation.viewmodels.UserViewModel


@Composable
fun CreateEventScreen(
    navController: NavHostController,
    userViewModel: UserViewModel
) {
    var eventName by remember { mutableStateOf("") }
    var eventDescription by remember { mutableStateOf("") }
    var eventDate by remember { mutableStateOf("") }
    var eventTime by remember { mutableStateOf("") }
    val selectedVolunteers = remember { mutableStateListOf<String>() }

    // Buscar voluntários ao carregar a tela
    LaunchedEffect(Unit) {
        fetchVolunteers(
            userViewModel = userViewModel,
            onSuccess = { },
            onFailure = { exception ->
                println("Erro ao buscar voluntários: ${exception.message}")
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Criar Evento", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        // Campo Nome
        Text("Nome", fontWeight = FontWeight.SemiBold)
        OutlinedTextField(
            value = eventName,
            onValueChange = { eventName = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo Descrição
        Text("Descrição", fontWeight = FontWeight.SemiBold)
        OutlinedTextField(
            value = eventDescription,
            onValueChange = { eventDescription = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo Data e Hora
        Text("Data", fontWeight = FontWeight.SemiBold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                value = eventDate,
                onValueChange = { eventDate = it },
                label = { Text("Data ")},
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            )
            OutlinedTextField(
                value = eventTime,
                onValueChange = { eventTime = it },
                label = { Text("Horário") },
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Seleção de Voluntários
        Text("Selecionar Voluntários", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        LazyColumn {
            items(userViewModel.volunteers) { volunteer ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = selectedVolunteers.contains(volunteer.id),
                        onCheckedChange = {
                            if (it) selectedVolunteers.add(volunteer.id)
                            else selectedVolunteers.remove(volunteer.id)
                        }
                    )
                    Text(volunteer.name)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botão para Criar Evento
        Button(
            onClick = {
                saveEventToFirebase(eventName, eventDescription, "$eventDate $eventTime", selectedVolunteers)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Criar Evento")
        }
    }
}