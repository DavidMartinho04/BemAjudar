package com.example.bemajudar.presentation.donations

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bemajudar.domain.model.DonationItemSimple
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationCard(
    donation: DonationItemSimple,
    onEdit: (DonationItemSimple) -> Unit,
    onDelete: () -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var updatedDonation by remember { mutableStateOf(donation.copy()) }
    val context = LocalContext.current

    // Configuração do DatePickerDialog
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                updatedDonation = updatedDonation.copy(deliveryDate = "$dayOfMonth/${month + 1}/$year")
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
                    text = donation.deliveryDate,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.weight(1f))

                // Ícones de Editar e Eliminar (alinhados ao título)
                IconButton(onClick = { isEditing = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color(0xFF025997))
                }
                IconButton(onClick = { onDelete() }) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFF025997))
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Descrição: ${donation.description}")
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Doador: ${donation.donorName}", fontWeight = FontWeight.SemiBold)

            // Diálogo de Edição com DatePickerDialog
            if (isEditing) {
                AlertDialog(
                    onDismissRequest = { isEditing = false },
                    confirmButton = {
                        Button(
                            onClick = {
                                onEdit(updatedDonation)
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
                    title = { Text("Editar Doação") },
                    text = {
                        Column(modifier = Modifier.background(Color.White)) {
                            // Data de Entrega com DatePickerDialog
                            OutlinedTextField(
                                value = updatedDonation.deliveryDate,
                                onValueChange = {},
                                label = { Text("Data de Entrega", color = Color.Gray) },
                                readOnly = true,
                                singleLine = true,
                                trailingIcon = {
                                    IconButton(onClick = { datePickerDialog.show() }) {
                                        Icon(Icons.Default.DateRange, contentDescription = "Selecionar Data")
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { datePickerDialog.show() },
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    containerColor = Color.White,
                                    focusedBorderColor = Color(0xFF025997)
                                )
                            )
                            // Campo Descrição
                            OutlinedTextField(
                                value = updatedDonation.description,
                                onValueChange = { updatedDonation = updatedDonation.copy(description = it) },
                                label = { Text("Descrição") },
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                            // Campo Doador
                            OutlinedTextField(
                                value = updatedDonation.donorName,
                                onValueChange = { updatedDonation = updatedDonation.copy(donorName = it) },
                                label = { Text("Doador") },
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


