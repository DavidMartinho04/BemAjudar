package com.example.bemajudar.presentation.donations

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.bemajudar.domain.model.DonationItem

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun DonationItemForm(
    item: DonationItem,
    onUpdateItem: (DonationItem) -> Unit,
    onRemoveItem: () -> Unit
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    val itemTypes = listOf("Alimentação", "Vestuário", "Eletrónica", "Mobilia", "Higiene", "Brinquedos", "Outro")
    val blueColor = Color(0xFF025997)

    // Controlador para o seletor de imagens
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onUpdateItem(item.copy(photoUri = uri))
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Ícone de remover no canto superior direito
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = { onRemoveItem() },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remover Item",
                        tint = blueColor
                    )
                }
            }

            // Seletor de Imagem (imagem reduzida)
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .align(Alignment.CenterHorizontally)
                    .offset(y = (-5).dp) // Movendo a imagem ligeiramente para cima
                    .clickable { imagePickerLauncher.launch("image/*") }
                    .background(Color.LightGray, shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp))
            ) {
                if (item.photoUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(item.photoUri),
                        contentDescription = "Foto do Item",
                        modifier = Modifier.size(90.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Adicionar Imagem",
                        modifier = Modifier
                            .size(70.dp)
                            .align(Alignment.Center)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Campo Nome do Item
            OutlinedTextField(
                value = item.name,
                onValueChange = { onUpdateItem(item.copy(name = it)) },
                label = { Text("Nome do Item", fontSize = 14.sp, color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = blueColor
                )
            )

            // Campo Descrição
            OutlinedTextField(
                value = item.description,
                onValueChange = { onUpdateItem(item.copy(description = it)) },
                label = { Text("Descrição", fontSize = 14.sp, color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = blueColor
                )
            )

            // Picker de Quantidade (AUMENTADO)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Diminui a quantidade apenas se for maior que 1
                IconButton(
                    onClick = { if (item.quantity > 1) onUpdateItem(item.copy(quantity = item.quantity - 1)) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Diminuir Quantidade", modifier = Modifier.size(18.dp))
                }

                // Garante que o valor nunca seja zero
                Text(
                    text = "${item.quantity}",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                // Adiciona um novo card ao clicar no botão de aumentar quantidade
                IconButton(
                    onClick = { onUpdateItem(item.copy(quantity = item.quantity + 1)) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Aumentar Quantidade", modifier = Modifier.size(18.dp))
                }
            }

            // Seleção de Tipo (Dropdown corrigido com menuAnchor())
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = item.type,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Tipo", modifier = Modifier.size(20.dp))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .clickable { expanded = true },
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = blueColor
                    )
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    itemTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type, fontSize = 14.sp) },
                            onClick = {
                                onUpdateItem(item.copy(type = type))
                                expanded = false
                            }
                        )
                    }
                }
            }
            // **Espaço Adicional para Tornar o Card Mais Simétrico**
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}