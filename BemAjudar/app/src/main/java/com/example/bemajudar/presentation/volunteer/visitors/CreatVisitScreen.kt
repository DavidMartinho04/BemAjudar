package com.example.bemajudar.presentation.volunteer.visitors

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.border
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.TextStyle
import com.example.bemajudar.data.firebase.getVisitorsFromFirestore
import com.example.bemajudar.data.firebase.updateVisitTimeInFirestore
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CreateVisitScreen() {
    var visitorsList by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var selectedVisitors by remember { mutableStateOf<MutableSet<String>>(mutableSetOf()) }
    var isRegisteringVisit by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    // Carregar visitantes ao iniciar o ecrã
    LaunchedEffect(Unit) {
        getVisitorsFromFirestore(
            onSuccess = { visitorsList = it },
            onFailure = { println("Erro ao carregar visitantes.") }
        )
    }

    // Filtrar a lista de visitantes pelo campo de pesquisa
    val filteredVisitors = visitorsList.filter {
        it["name"].toString().contains(searchText, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Título
        Text(
            text = "Registar Visitas",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Campo de Pesquisa por Nome
        BasicTextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .border(1.dp, Color.Gray, RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { }),
            textStyle = TextStyle(color = Color.Black),
            decorationBox = { innerTextField ->
                if (searchText.isEmpty()) {
                    Text("Pesquisar por nome...", color = Color.Gray)
                }
                innerTextField()
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botão para iniciar ou concluir registo de visita
        Button(
            onClick = {
                isRegisteringVisit = !isRegisteringVisit
                if (!isRegisteringVisit) {
                    val currentTime = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                    selectedVisitors.forEach { visitorId ->
                        updateVisitTimeInFirestore(visitorId, currentTime)
                    }
                    selectedVisitors.clear()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isRegisteringVisit) Color.Red else Color(0xFF025997)
            )
        ) {
            Text(
                text = if (isRegisteringVisit) "Concluir Registo de Visita" else "Registar Visita",
                color = Color.White,
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de visitantes filtrada com Checkboxes
        LazyColumn {
            items(filteredVisitors) { visitor ->
                val visitorId = visitor["id"].toString()
                var isChecked by remember { mutableStateOf(selectedVisitors.contains(visitorId)) }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .border(1.dp, Color.LightGray, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isRegisteringVisit) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = {
                                isChecked = it
                                if (it) selectedVisitors.add(visitorId)
                                else selectedVisitors.remove(visitorId)
                            }
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = visitor["name"].toString().uppercase(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text("NIF: ${visitor["nif"] ?: "N/A"}")
                        Text("Morada: ${visitor["address"] ?: "N/A"}")
                        Text("Contacto: ${visitor["contact"] ?: "N/A"}")
                    }
                }
            }
        }
    }
}
