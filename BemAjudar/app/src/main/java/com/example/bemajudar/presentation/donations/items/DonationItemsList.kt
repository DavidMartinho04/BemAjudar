package com.example.bemajudar.presentation.donations.items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bemajudar.domain.model.DonationItemDetail
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun DonationItemsListScreen() {
    val itemsList = remember { mutableStateListOf<DonationItemDetail>() }
    val db = FirebaseFirestore.getInstance()
    var searchQuery by remember { mutableStateOf("") }

    // Atualização em tempo real com SnapshotListener
    LaunchedEffect(Unit) {
        db.collection("donations").addSnapshotListener { snapshot, error ->
            if (error != null) {
                error.printStackTrace()
                return@addSnapshotListener
            }

            itemsList.clear()

            snapshot?.documents?.forEach { document ->
                val items = document.get("items") as? List<Map<String, Any>> ?: emptyList()
                items.forEach { item ->
                    val donationItemDetail = DonationItemDetail(
                        name = item["name"] as? String ?: "Sem Nome",
                        description = item["description"] as? String ?: "Sem Descrição",
                        quantity = (item["quantity"] as? Long)?.toInt() ?: 0,
                        type = item["type"] as? String ?: "Sem Tipo",
                        photoUri = item["photoUrl"] as? String
                    )
                    itemsList.add(donationItemDetail)
                }
            }
        }
    }

    // Filtrar itens
    val filteredItems = itemsList.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.description.contains(searchQuery, ignoreCase = true)
    }

    // Layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Título
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = "Gerir Itens",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(27.dp))

        // Barra de Pesquisa
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Procurar Itens...") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de Itens com Fotos e Ações
        LazyColumn {
            items(filteredItems) { item ->
                DonationItemDetailCard(
                    item = item,
                    onEdit = { updatedItem ->
                        // Corrigido para procurar o documento certo
                        db.collection("donations").get()
                            .addOnSuccessListener { documents ->
                                documents.forEach { document ->
                                    val items = document.get("items") as? List<Map<String, Any>> ?: emptyList()
                                    val updatedItems = items.map { original ->
                                        if (original["name"] == item.name) { // Comparar pelo nome
                                            mapOf(
                                                "name" to updatedItem.name,
                                                "description" to updatedItem.description,
                                                "quantity" to updatedItem.quantity,
                                                "type" to updatedItem.type,
                                                "photoUrl" to updatedItem.photoUri
                                            )
                                        } else original
                                    }

                                    // Atualizar apenas os itens no documento correto
                                    db.collection("donations").document(document.id)
                                        .update("items", updatedItems)
                                }
                            }
                    },
                    onDelete = {
                        db.collection("donations").whereArrayContains("items", item).get()
                            .addOnSuccessListener { documents ->
                                for (document in documents) {
                                    val updatedItems = (document.get("items") as? List<Map<String, Any>>)
                                        ?.filter { it["name"] != item.name } ?: listOf()

                                    db.collection("donations").document(document.id)
                                        .update("items", updatedItems)
                                }
                            }
                        itemsList.remove(item)
                    }
                )
            }
        }
    }
}
