package com.example.bemajudar.presentation.pickups

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
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
import com.example.bemajudar.domain.model.PickupItem
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun LevantamentoListScreen() {
    var searchQuery by remember { mutableStateOf("") }
    val pickups = remember { mutableStateListOf<PickupItem>() }
    val db = FirebaseFirestore.getInstance()

    // Atualização em tempo real com SnapshotListener
    LaunchedEffect(Unit) {
        db.collection("levantamentos")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    error.printStackTrace()
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    pickups.clear()
                    for (document in snapshot.documents) {
                        val pickup = PickupItem(
                            dateLevantamento = document.getString("dateLevantamento") ?: "Data Indisponível",
                            name = document.getString("name") ?: "Sem Nome",
                            description = document.getString("description") ?: "Sem Descrição",
                            quantity = (document.getLong("quantity") ?: 0).toInt(),
                            type = document.getString("type") ?: "Sem Tipo",
                            visitorId = document.getString("visitorId") ?: "N/A"
                        )
                        pickups.add(pickup)
                    }
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Título
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = "Levantamentos Realizados",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(27.dp))

        // Barra de pesquisa
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Procurar Levantamento...") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .background(Color.White)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de levantamentos com filtro em tempo real
        LazyColumn {
            items(pickups.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.description.contains(searchQuery, ignoreCase = true) ||
                        it.visitorId.contains(searchQuery, ignoreCase = true)
            }) { pickup ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Text("Data: ${pickup.dateLevantamento}", fontWeight = FontWeight.Bold)
                        Text("Nome: ${pickup.name}")
                        Text("Descrição: ${pickup.description}")
                        Text("Quantidade: ${pickup.quantity}")
                        Text("Tipo: ${pickup.type}")
                    }
                }
            }
        }
    }
}