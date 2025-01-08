package com.example.bemajudar.presentation.pickups

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun SelectVisitorScreen(onNextClick: (String) -> Unit) {
    val visitorsList = remember { mutableStateListOf<Map<String, Any>>() }
    var selectedVisitorId by remember { mutableStateOf<String?>(null) }
    var searchText by remember { mutableStateOf("") }
    val db = FirebaseFirestore.getInstance()

    // Carregar os visitantes do Firestore
    LaunchedEffect(Unit) {
        db.collection("visitors").get()
            .addOnSuccessListener { result ->
                visitorsList.clear()
                for (document in result) {
                    visitorsList.add(document.data + ("id" to document.id))
                }
            }
    }

    // Filtrar visitantes
    val filteredVisitors = visitorsList.filter {
        it["name"].toString().contains(searchText, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Título
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = "Selecionar Visitante",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(27.dp))

        // Barra de pesquisa
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = { Text("Procurar Visitante...") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de visitantes com RadioButton
        LazyColumn {
            items(filteredVisitors) { visitor ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedVisitorId == visitor["id"],
                        onClick = { selectedVisitorId = visitor["id"] as String }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = visitor["name"].toString(), fontWeight = FontWeight.Bold)
                        Text("NIF: ${visitor["nif"] ?: "N/A"}")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { selectedVisitorId?.let { onNextClick(it) } },
            modifier = Modifier
                .width(180.dp)
                .height(54.dp)
                .align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF025997), // Azul sempre ativo
                disabledContainerColor = Color(0xFF025997), // Azul mesmo desativado
                disabledContentColor = Color.White // Texto branco quando desativado
            )
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
