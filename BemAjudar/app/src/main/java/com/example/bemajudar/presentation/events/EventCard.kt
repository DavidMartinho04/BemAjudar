package com.example.bemajudar.presentation.events

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventCard(
    event: EventItem,
    onEdit: (EventItem) -> Unit,
    onDelete: () -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var updatedEvent by remember { mutableStateOf(event.copy()) }
    val context = LocalContext.current

    // Configuração do DatePickerDialog
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                updatedEvent = updatedEvent.copy(date = "$dayOfMonth/${month + 1}/$year")
            },
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Data: ${event.date}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.weight(1f))

                // Ícones para Editar e Eliminar Evento
                IconButton(onClick = { isEditing = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color(0xFF025997))
                }
                IconButton(onClick = { onDelete() }) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFF025997))
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Nome: ${event.name}")
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Descrição: ${event.description}", fontWeight = FontWeight.SemiBold)

            // Diálogo para Edição de Evento
            if (isEditing) {
                AlertDialog(
                    onDismissRequest = { isEditing = false },
                    confirmButton = {
                        Button(
                            onClick = {
                                onEdit(updatedEvent)
                                isEditing = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF025997))
                        ) {
                            Text("Guardar", color = Color.White)
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { isEditing = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                        ) {
                            Text("Cancelar", color = Color.White)
                        }
                    },
                    title = { Text("Editar Evento") },
                    text = {
                        Column(modifier = Modifier.background(Color.White)) {
                            // Data do Evento com DatePickerDialog
                            OutlinedTextField(
                                value = updatedEvent.date,
                                onValueChange = { updatedEvent = updatedEvent.copy(date = it) },
                                label = { Text("Data do Evento") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            // Campo Nome do Evento
                            OutlinedTextField(
                                value = updatedEvent.name,
                                onValueChange = { updatedEvent = updatedEvent.copy(name = it) },
                                label = { Text("Nome do Evento") },
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                            // Campo Descrição do Evento
                            OutlinedTextField(
                                value = updatedEvent.description,
                                onValueChange = { updatedEvent = updatedEvent.copy(description = it) },
                                label = { Text("Descrição") },
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                    },
                    containerColor = Color.White
                )
            }
        }
    }
}