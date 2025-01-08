package com.example.bemajudar.presentation.admin

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
import com.example.bemajudar.presentation.donations.DonationCard
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun DonationListScreen() {
    var searchQuery by remember { mutableStateOf("") }
    val donations = remember { mutableStateListOf<DonationItemSimple>() }
    val db = FirebaseFirestore.getInstance()

    // Atualização em tempo real com SnapshotListener
    LaunchedEffect(Unit) {
        db.collection("donations")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    error.printStackTrace()
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    donations.clear()
                    for (document in snapshot.documents) {
                        val donation = DonationItemSimple(
                            id = document.id,
                            deliveryDate = document.getString("deliveryDate") ?: "Data Indisponível",
                            description = document.getString("donationDescription") ?: "Sem descrição",
                            donorName = document.getString("donorName") ?: "Doador Desconhecido"
                        )
                        donations.add(donation)
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
            text = "Gerir Doações",
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
                placeholder = { Text("Procure Aqui...") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .background(Color.White)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de doações filtrada em tempo real
        LazyColumn {
            items(donations.filter {
                it.donorName.contains(searchQuery, ignoreCase = true) ||
                        it.description.contains(searchQuery, ignoreCase = true)
            }) { donation ->
                DonationCard(
                    donation = donation,
                    onEdit = { updatedData ->
                        db.collection("donations").document(donation.id)
                            .update(
                                mapOf(
                                    "deliveryDate" to updatedData.deliveryDate,
                                    "donationDescription" to updatedData.description,
                                    "donorName" to updatedData.donorName
                                )
                            )
                    },
                    onDelete = {
                        db.collection("donations").document(donation.id).delete()
                        donations.remove(donation)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
