package com.example.bemajudar.presentation.donations.items

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.bemajudar.R
import com.example.bemajudar.domain.model.DonationItemDetail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationItemDetailCard(
    item: DonationItemDetail,
    onEdit: (DonationItemDetail) -> Unit,
    onDelete: () -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var updatedItem by remember { mutableStateOf(item.copy()) }
    var expanded by remember { mutableStateOf(false) } // Estado do Dropdown
    val itemTypes = listOf("Alimentação", "Vestuário", "Eletrónica", "Mobilia", "Higiene", "Brinquedos", "Outro")

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
                // Exibir a foto (se existir)
                if (!item.photoUri.isNullOrEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(item.photoUri),
                        contentDescription = "Foto do item ${item.name}",
                        modifier = Modifier
                            .size(60.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.default_featured_image),
                        contentDescription = "Imagem Padrão",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(Modifier.width(16.dp))

                // Nome e Ícones de Edição e Eliminação
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = item.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Descrição: ${item.description}")
                    Text(text = "Quantidade: ${item.quantity}")
                    Text(text = "Tipo: ${item.type}")
                }

                // Ícones de Editar e Eliminar
                IconButton(onClick = { isEditing = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color(0xFF025997))
                }
                IconButton(onClick = { onDelete() }) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFF025997))
                }
            }

            // Diálogo de Edição
            if (isEditing) {
                AlertDialog(
                    onDismissRequest = { isEditing = false },
                    confirmButton = {
                        Button(
                            onClick = {
                                onEdit(updatedItem)
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
                    title = { Text("Editar Item de Doação") },
                    text = {
                        Column(modifier = Modifier.background(Color.White)) {
                            OutlinedTextField(
                                value = updatedItem.name,
                                onValueChange = { updatedItem = updatedItem.copy(name = it) },
                                label = { Text("Nome do Item") },
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                            OutlinedTextField(
                                value = updatedItem.description,
                                onValueChange = { updatedItem = updatedItem.copy(description = it) },
                                label = { Text("Descrição") },
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                            OutlinedTextField(
                                value = updatedItem.quantity.toString(),
                                onValueChange = { updatedItem = updatedItem.copy(quantity = it.toIntOrNull() ?: 0) },
                                label = { Text("Quantidade") },
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                            // Dropdown de Seleção de Tipo
                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = !expanded }
                            ) {
                                OutlinedTextField(
                                    value = updatedItem.type,
                                    onValueChange = {},
                                    label = { Text("Tipo") },
                                    readOnly = true,
                                    trailingIcon = {
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Selecionar Tipo")
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor()
                                        .padding(vertical = 6.dp)
                                        .clickable { expanded = true }
                                )

                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    itemTypes.forEach { type ->
                                        DropdownMenuItem(
                                            text = { Text(type) },
                                            onClick = {
                                                updatedItem = updatedItem.copy(type = type)
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    },
                    containerColor = Color.White
                )
            }
        }
    }
}