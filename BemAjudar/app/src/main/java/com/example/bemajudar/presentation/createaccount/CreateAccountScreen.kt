package com.example.bemajudar.presentation.createaccount

import android.app.DatePickerDialog
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.bemajudar.presentation.viewmodels.UserViewModel
import com.example.bemajudar.ui.components.ProgressIndicators
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccountScreen(
    onNextClick: () -> Unit, // Modificamos para não passar dados diretamente
    userViewModel: UserViewModel = viewModel() // Injeta o ViewModel no ecrã
) {
    // Definições de cores utilizadas
    val primaryColor = Color(0xFF625BFF)
    val pointColor = Color(0xFF025997)
    val avatarBackground = Color(0xFFEADDFF)
    val textFieldBackground = Color(0xFFF3F3F3)
    val secondaryColor = Color(0xFF6F6F6F)

    // Estados para os valores do formulário
    var name by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Configuração do diálogo para seleção de data
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            birthDate = "$dayOfMonth/${month + 1}/$year"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // Configuração do launcher para seleção de imagem
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        photoUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Criar Conta",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Indicadores de progresso
        ProgressIndicators(
            currentStep = 1, // Primeira etapa
            totalSteps = 2,  // Total de etapas é fixado em 2
            activeColor = primaryColor, // Cor da etapa atual
            inactiveColor = textFieldBackground // Cor das etapas inativas
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Foto de perfil
        Box(
            modifier = Modifier
                .size(90.dp)
                .background(avatarBackground, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (photoUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(model = photoUri),
                    contentDescription = "Foto de perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                )
            }
        }

        TextButton(
            onClick = { launcher.launch("image/*") },
        ) {
            Text(
                text = "Selecionar Foto",
                color = primaryColor,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Campos de texto do formulário
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nome", color = secondaryColor) },
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = textFieldBackground,
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = textFieldBackground
            ),
            modifier = Modifier.fillMaxWidth(0.9f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = birthDate,
            onValueChange = { },
            label = { Text("Data Nascimento", color = secondaryColor) },
            singleLine = true,
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clickable { datePickerDialog.show() },
            trailingIcon = {
                IconButton(onClick = { datePickerDialog.show() }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Abrir calendário",
                        tint = primaryColor
                    )
                }
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = textFieldBackground,
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = textFieldBackground
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Nº Telemóvel", color = secondaryColor) },
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = textFieldBackground,
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = textFieldBackground
            ),
            modifier = Modifier.fillMaxWidth(0.9f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email", color = secondaryColor) },
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = textFieldBackground,
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = textFieldBackground
            ),
            modifier = Modifier.fillMaxWidth(0.9f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Palavra-Passe", color = secondaryColor) },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = primaryColor
                    )
                }
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = textFieldBackground,
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = textFieldBackground
            ),
            modifier = Modifier.fillMaxWidth(0.9f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                when {
                    name.isEmpty() -> Toast.makeText(
                        context,
                        "O nome não pode estar vazio!",
                        Toast.LENGTH_SHORT
                    ).show()

                    birthDate.isEmpty() -> Toast.makeText(
                        context,
                        "Por favor, selecione uma data de nascimento!",
                        Toast.LENGTH_SHORT
                    ).show()

                    phoneNumber.length != 9 || !phoneNumber.all { it.isDigit() } ->
                        Toast.makeText(
                            context,
                            "O número de telemóvel deve ter 9 dígitos!",
                            Toast.LENGTH_SHORT
                        ).show()

                    !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                        Toast.makeText(
                            context,
                            "Por favor, insira um email válido!",
                            Toast.LENGTH_SHORT
                        ).show()

                    password.length < 8 || !password.any { it.isDigit() } ->
                        Toast.makeText(
                            context,
                            "A palavra-passe deve ter pelo menos 8 caracteres e incluir um número!",
                            Toast.LENGTH_SHORT
                        ).show()

                    else -> {
                        // Atualiza os dados no ViewModel
                        userViewModel.updateUserData(
                            photoUri = photoUri,
                            name = name,
                            birthDate = birthDate,
                            phone = phoneNumber,
                            email = email,
                            password = password
                        )
                        // Navega para o próximo ecrã
                        onNextClick()
                    }
                }
            },
            modifier = Modifier
                .width(150.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
        ) {
            Text(
                text = "Seguinte",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}
