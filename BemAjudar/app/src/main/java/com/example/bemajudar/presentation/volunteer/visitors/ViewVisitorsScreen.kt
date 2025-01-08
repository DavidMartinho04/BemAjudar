package com.example.bemajudar.presentation.volunteer.visitors

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.navigation.NavController
import com.example.bemajudar.data.firebase.getVisitorsFromFirestore
import androidx.compose.ui.graphics.SolidColor

@Composable
fun ViewVisitorsScreen(navController: NavController) {
    var visitorsList by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var searchText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Carregar dados do Firebase e filtrar visitantes com data de última visita
    LaunchedEffect(Unit) {
        getVisitorsFromFirestore(
            onSuccess = { visitors ->
                // Filtra apenas visitantes com "lastVisit" presente e não vazio
                visitorsList = visitors.filter { it["lastVisit"] != null && it["lastVisit"].toString().isNotEmpty() }
            },
            onFailure = { errorMessage = "Erro ao carregar os visitantes." }
        )
    }

    // Filtrar pelo texto digitado na barra de pesquisa
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
            text = "Consultar Visitas",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

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
            cursorBrush = SolidColor(Color(0xFF025997)),
            decorationBox = { innerTextField ->
                if (searchText.isEmpty()) {
                    Text("Pesquisar por nome...", color = Color.Gray)
                }
                innerTextField()
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de Visitantes com LazyColumn (Apenas os com última visita)
        LazyColumn {
            items(filteredVisitors) { visitor ->
                VisitorCard(visitor)
            }
        }

        // Mensagem de erro, se houver
        errorMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun VisitorCard(visitor: Map<String, Any>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(1.dp, Color.LightGray, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = visitor["name"].toString().uppercase(),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text("NIF: ${visitor["nif"] ?: "N/A"}")
        Text("Morada: ${visitor["address"] ?: "N/A"}")
        Text("Contacto: ${visitor["contact"] ?: "N/A"}")
        Text("Última Visita: ${visitor["lastVisit"] ?: "N/A"}", fontWeight = FontWeight.SemiBold)
    }
}
