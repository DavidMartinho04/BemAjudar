package com.example.bemajudar.presentation.donations

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.bemajudar.domain.model.DonationItemSimple
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun DonationListScreen() {
    var searchQuery by remember { mutableStateOf("") }
    val donations = remember { mutableStateListOf<DonationItemSimple>() }
    val db = FirebaseFirestore.getInstance()

    // Busca as doações no Firestore ao carregar a página
    LaunchedEffect(Unit) {
        try {
            val result = db.collection("donations").get().await()
            donations.clear()
            for (document in result) {
                val donation = DonationItemSimple(
                    deliveryDate = document.getString("deliveryDate") ?: "Data Indisponível",
                    description = document.getString("description") ?: "Sem descrição",
                    donorName = document.getString("donorName") ?: "Doador Desconhecido"
                )
                donations.add(donation)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Título
        Text(
            text = "Gerir Doações",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Barra de pesquisa e ícones
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Procure Aqui...") },
                trailingIcon = {
                    IconButton(onClick = { /* Implementar filtro de pesquisa */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Procurar")
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .background(Color.White)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = { /* Implementar filtros */ }) {
                Icon(Icons.Default.FilterList, contentDescription = "Filtrar")
            }
        }

        // Lista de doações filtrada
        donations.filter {
            it.donorName.contains(searchQuery, ignoreCase = true) ||
                    it.description.contains(searchQuery, ignoreCase = true)
        }.forEach { donation ->
            DonationCard(donation)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}