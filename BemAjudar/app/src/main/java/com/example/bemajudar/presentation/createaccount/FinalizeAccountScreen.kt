package com.example.bemajudar.presentation.createaccount

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bemajudar.data.firebase.registerUser
import com.example.bemajudar.presentation.viewmodels.UserViewModel
import com.example.bemajudar.ui.components.ProgressIndicators

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinalizeAccountScreen(
    onCreateAccountClick: () -> Unit,
    userViewModel: UserViewModel = viewModel()
) {
    val primaryColor = Color(0xFF625BFF)
    val textFieldBackground = Color(0xFFF3F3F3)
    val secondaryColor = Color(0xFF6F6F6F)

    // Estados locais para os campos desta tela
    var address by remember { mutableStateOf("") }
    var postalCode by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Masculino") }
    var userType by remember { mutableStateOf("Gestor") }
    var expanded by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Quase Lá!",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Indicadores de progresso
        ProgressIndicators(
            currentStep = 2, // Segunda etapa
            totalSteps = 2,  // Total de etapas é fixado em 2
            activeColor = primaryColor, // Cor da etapa atual
            inactiveColor = textFieldBackground // Cor das etapas inativas
        )
        Spacer(modifier = Modifier.height(35.dp))

        // Campo Morada
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Morada", color = secondaryColor) },
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = textFieldBackground,
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = textFieldBackground
            ),
            modifier = Modifier.fillMaxWidth(0.9f)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Campo Código Postal
        OutlinedTextField(
            value = postalCode,
            onValueChange = { input ->
                // Permite apenas números e o caractere "-"
                if (input.matches(Regex("^[0-9-]*$"))) {
                    postalCode = input
                }
            },
            label = { Text("Código Postal", color = secondaryColor) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text), // Altera para permitir "-"
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = textFieldBackground,
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = textFieldBackground
            ),
            modifier = Modifier.fillMaxWidth(0.9f)
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Seção de Género
        Text(
            text = "Género",
            fontSize = 16.sp,
            color = secondaryColor,
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(start = 5.dp)
        )
        Row(
            Modifier
                .fillMaxWidth(1f)
                .padding(vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf("Masculino", "Feminino", "Outro").forEach { option ->
                Row(
                    Modifier
                        .selectable(
                            selected = (gender == option),
                            onClick = { gender = option }
                        )
                        .padding(horizontal = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (gender == option),
                        onClick = { gender = option },
                        colors = RadioButtonDefaults.colors(selectedColor = primaryColor)
                    )
                    Text(text = option, fontSize = 13.sp, color = secondaryColor)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tipo Utilizador
        Text(
            text = "Tipo Utilizador",
            fontSize = 16.sp,
            color = secondaryColor,
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(start = 5.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .background(
                        textFieldBackground,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                    )
                    .border(
                        1.dp,
                        Color.Black,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                    )
                    .clickable { expanded = !expanded }
                    .padding(12.dp)
            ) {
                Text(
                    text = userType.ifEmpty { "Gestor" },
                    color = if (userType.isEmpty()) secondaryColor else Color.Black,
                    fontSize = 16.sp
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .zIndex(1f)
                .padding(start = 20.dp)
        ) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                listOf("Gestor", "Voluntário").forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            userType = option
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botão "Criar Conta"
        Button(
            onClick = {
                if (postalCode.matches(Regex("^\\d{4}-\\d{3}$"))) { // Valida o formato ####-###
                    userViewModel.updateFinalizeData(
                        address = address,
                        postalCode = postalCode,
                        gender = gender,
                        userType = userType
                    )

                    registerUser(
                        userViewModel = userViewModel,
                        onSuccess = {
                            Toast.makeText(context, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show()
                            onCreateAccountClick()
                        },
                        onFailure = { e ->
                            Toast.makeText(context, "Erro ao criar conta: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    )
                } else {
                    Toast.makeText(context, "Por favor, insira um código postal no formato ####-###.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .width(150.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
        ) {
            Text("Criar Conta", color = Color.White, fontSize = 16.sp)
        }
    }
}
