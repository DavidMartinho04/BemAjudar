package com.example.bemajudar.presentation.donations

import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bemajudar.data.firebase.sendDonationToFirestore
import com.example.bemajudar.domain.model.DonationItem
import com.example.bemajudar.presentation.createaccount.uploadPhotoToFirebaseStorage
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationFormScreen(context: Context) {
    var donorName by remember { mutableStateOf("") }
    var donationDescription by remember { mutableStateOf("") }
    var deliveryDate by remember { mutableStateOf("") }
    var items = remember { mutableStateListOf(DonationItem()) } // Começa com um item padrão

    val primaryColor = Color(0xFF333333)
    val textFieldBackground = Color(0xFFF3F3F3)
    val blueColor = Color(0xFF025997)
    val whiteColor = Color(0xFFFFFFFF)    // Branco
    val secondaryColor = Color(0xFF6F6F6F)

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            // Verificação para impedir datas futuras
            if (selectedDate.after(Calendar.getInstance())) {
                Toast.makeText(context, "A data de entrega não pode ser no futuro.", Toast.LENGTH_SHORT).show()
            } else {
                deliveryDate = "$dayOfMonth/${month + 1}/$year"
            }
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        // Título Principal
        Text(
            text = "Registar Doação",
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            color = primaryColor,
            modifier = Modifier.padding(start = 16.dp)
        )

        Spacer(modifier = Modifier.height(25.dp))

        // Campo Nome do Doador
        OutlinedTextField(
            value = donorName,
            onValueChange = { donorName = it },
            label = { Text("Doador", color = secondaryColor) },
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = whiteColor,
                focusedBorderColor = blueColor
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo Descrição (Maior)
        OutlinedTextField(
            value = donationDescription,
            onValueChange = { donationDescription = it },
            label = { Text("Descrição", color = secondaryColor) },
            maxLines = 5,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = whiteColor,
                focusedBorderColor = blueColor
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Data de Entrega
        OutlinedTextField(
            value = deliveryDate,
            onValueChange = {},
            label = { Text("Data de Entrega", color = secondaryColor) },
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
                containerColor = whiteColor,
                focusedBorderColor = blueColor
            )
        )

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Adicionar Itens",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = primaryColor,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 4.dp, bottom = 8.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))
        // Exibição dos itens com espaçamento adequado
        items.forEachIndexed { index, item ->
            DonationItemForm(
                item = item,
                onUpdateItem = { updatedItem -> items[index] = updatedItem },
                onRemoveItem = {
                    if (items.size > 1) {
                        items.removeAt(index)
                    } else {
                        Toast.makeText(context, "É necessário ter pelo menos um item.", Toast.LENGTH_SHORT).show()
                    }
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Botão de Adicionar Novo Item
        IconButton(
            onClick = { items.add(DonationItem()) },
            modifier = Modifier
                .size(40.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Adicionar Item", tint = blueColor, modifier = Modifier.size(25.dp))
        }

        Spacer(modifier = Modifier.height(25.dp))

        // Botão de Submeter Doação com validação, upload, envio e reset dos cards
        Button(
            onClick = {
                if (donorName.isBlank() || donationDescription.isBlank() || deliveryDate.isBlank() || items.any { it.name.isBlank() || it.description.isBlank() }) {
                    Toast.makeText(context, "Por favor, preencha todos os campos corretamente.", Toast.LENGTH_LONG).show()
                } else {
                    // Upload de imagens e envio para o Firestore
                    items.forEachIndexed { index, item ->
                        if (item.photoUri != null) {
                            uploadPhotoToFirebaseStorage(
                                item.photoUri!!,
                                onSuccess = { photoUrl ->
                                    items[index] = item.copy(photoUri = Uri.parse(photoUrl))
                                    // Se for o último item, enviar para o Firestore
                                    if (index == items.size - 1) {
                                        sendDonationToFirestore(
                                            donorName,
                                            donationDescription,
                                            deliveryDate,
                                            items,
                                            onSuccess = {
                                                Toast.makeText(context, "Doação registada com sucesso!", Toast.LENGTH_LONG).show()
                                                // Resetando os campos e deixando apenas um card vazio
                                                donorName = ""
                                                donationDescription = ""
                                                deliveryDate = ""
                                                items.clear()
                                                items.add(DonationItem()) // Mantém um card vazio
                                            },
                                            onFailure = { exception ->
                                                Toast.makeText(context, "Erro ao enviar a doação: ${exception.message}", Toast.LENGTH_LONG).show()
                                            }
                                        )
                                    }
                                },
                                onFailure = { exception ->
                                    Toast.makeText(context, "Erro ao carregar a imagem: ${exception.message}", Toast.LENGTH_LONG).show()
                                }
                            )
                        } else {
                            // Se não houver foto, enviar diretamente
                            if (index == items.size - 1) {
                                sendDonationToFirestore(
                                    donorName,
                                    donationDescription,
                                    deliveryDate,
                                    items,
                                    onSuccess = {
                                        Toast.makeText(context, "Doação registada com sucesso!", Toast.LENGTH_LONG).show()
                                        // Resetando os campos e mantendo apenas um card vazio
                                        donorName = ""
                                        donationDescription = ""
                                        deliveryDate = ""
                                        items.clear()
                                        items.add(DonationItem()) // Reinicia com um card vazio
                                    },
                                    onFailure = { exception ->
                                        Toast.makeText(context, "Erro ao enviar a doação: ${exception.message}", Toast.LENGTH_LONG).show()
                                    }
                                )
                            }
                        }
                    }
                }
            },
            modifier = Modifier
                .width(180.dp)
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = blueColor)
        ) {
            Text(
                text = "Registar Doação",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

