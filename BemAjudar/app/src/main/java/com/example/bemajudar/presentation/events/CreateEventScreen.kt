package com.example.bemajudar.presentation.events

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.bemajudar.data.firebase.fetchVolunteers
import com.example.bemajudar.data.firebase.saveEvent
import com.example.bemajudar.presentation.viewmodels.UserViewModel

val primaryColor = Color(0xFF025997)
val secondaryColor = Color.Gray
val textFieldBackground = Color(0xFFF0F0F0)

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
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        fetchVolunteers(
            userViewModel = userViewModel,
            onSuccess = {},
            onFailure = { exception ->
                println("Erro ao buscar voluntários: ${exception.message}")
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Criar Evento",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        FormField("Nome do Evento", eventName) { eventName = it }
        FormField("Descrição", eventDescription) { eventDescription = it }
        FormField("Data", eventDate) { eventDate = it }
        FormField("Hora", eventTime) { eventTime = it }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Selecionar Voluntários", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        LazyColumn {
            items(userViewModel.volunteers) { volunteer ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = selectedVolunteers.contains(volunteer.email),
                        onCheckedChange = {
                            if (it) selectedVolunteers.add(volunteer.email)
                            else selectedVolunteers.remove(volunteer.email)
                        },
                        colors = CheckboxDefaults.colors(checkedColor = primaryColor)
                    )
                    Text(volunteer.name)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (eventName.isBlank() || eventDescription.isBlank() || eventDate.isBlank() || eventTime.isBlank()) {
                    Toast.makeText(context, "Todos os campos devem ser preenchidos.", Toast.LENGTH_SHORT).show()
                } else {
                    saveEvent(eventName, eventDescription, "$eventDate $eventTime", selectedVolunteers)
                    Toast.makeText(context, "Evento criado com sucesso!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
        ) {
            Text("Criar Evento", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
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
                cursorColor = primaryColor
            ),
            textStyle = TextStyle(color = Color.Black)
        )
    }
}
