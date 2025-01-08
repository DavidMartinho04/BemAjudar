package com.example.bemajudar.presentation.volunteer.visitors

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bemajudar.data.firebase.addVisitorToFirestore

val primaryColor = Color(0xFF025997)
val secondaryColor = Color.Gray
val textFieldBackground = Color(0xFFF0F0F0)

@Composable
fun CreateVisitorScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var nif by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Registar Novo Visitante",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        FormField("Nome", name) { name = it }
        FormField("NIF", nif) { nif = it }
        FormField("Morada", address) { address = it }
        FormField("Contacto", contact) { contact = it }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (name.isNotBlank() && nif.isNotBlank()) {
                    addVisitorToFirestore(name, nif, address, "", contact,
                        onSuccess = { navController.popBackStack() },
                        onFailure = { errorMessage = "Erro ao guardar visitante no Firebase" }
                    )
                } else {
                    errorMessage = "Preencha todos os campos obrigatórios."
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
        ) {
            Text("Guardar Visitante", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
        }

        errorMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, color = secondaryColor) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = textFieldBackground,
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = secondaryColor,
                focusedLabelColor = secondaryColor,
                unfocusedLabelColor = secondaryColor,
                cursorColor = primaryColor
            ),
            textStyle = TextStyle(color = Color.Black) // Definindo o texto como sempre preto
        )
    }
}
