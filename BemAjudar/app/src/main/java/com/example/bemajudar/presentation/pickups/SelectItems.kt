package com.example.bemajudar.presentation.pickups

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.bemajudar.domain.model.DonationItemDetail
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun SelectItemsScreen(visitorId: String, onSubmit: (List<DonationItemDetail>) -> Unit) {
    val itemsList = remember { mutableStateListOf<DonationItemDetail>() }
    val selectedItems = remember { mutableStateListOf<DonationItemDetail>() }
    val db = FirebaseFirestore.getInstance()

    LaunchedEffect(Unit) {
        db.collection("donations").get()
            .addOnSuccessListener { result ->
                itemsList.clear()
                result.documents.forEach { document ->
                    val items = document.get("items") as? List<Map<String, Any>> ?: emptyList()
                    items.forEach { item ->
                        itemsList.add(
                            DonationItemDetail(
                                name = item["name"] as? String ?: "Sem Nome",
                                description = item["description"] as? String ?: "Sem Descrição",
                                quantity = (item["quantity"] as? Long)?.toInt() ?: 0,
                                type = item["type"] as? String ?: "Sem Tipo",
                                photoUri = item["photoUrl"] as? String
                            )
                        )
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
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Selecionar Itens",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(27.dp))

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de Itens com Cards e Imagens
        LazyColumn {
            items(itemsList) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Imagem do Item
                        if (!item.photoUri.isNullOrEmpty()) {
                            Image(
                                painter = rememberAsyncImagePainter(item.photoUri),
                                contentDescription = "Foto de ${item.name}",
                                modifier = Modifier
                                    .size(80.dp)
                                    .padding(end = 12.dp),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            // Imagem Padrão se não houver foto
                            Image(
                                painter = painterResource(id = com.example.bemajudar.R.drawable.ic_default_avatar),
                                contentDescription = "Imagem Padrão",
                                modifier = Modifier
                                    .size(80.dp)
                                    .padding(end = 12.dp),
                                contentScale = ContentScale.Crop
                            )
                        }

                        // Checkbox e Detalhes do Item
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Nome: ${item.name}", fontWeight = FontWeight.Bold)
                            Text("Descrição: ${item.description}")
                            Text("Quantidade: ${item.quantity}")
                            Text("Tipo: ${item.type}")
                        }

                        // Checkbox no Final do Card
                        Checkbox(
                            checked = selectedItems.contains(item),
                            onCheckedChange = { isChecked ->
                                if (isChecked) selectedItems.add(item)
                                else selectedItems.remove(item)
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Botão de Confirmar ao Centro
        Button(
            onClick = { onSubmit(selectedItems) },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(54.dp)
                .align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF025997),
                disabledContainerColor = Color(0xFF025997),
                disabledContentColor = Color.White
            )
        ) {
            Text(
                text = "Confirmar Levantamento",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}
