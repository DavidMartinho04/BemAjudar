package com.example.bemajudar.presentation.volunteer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.bemajudar.R
import com.example.bemajudar.data.firebase.fetchPendingNotifications
import com.example.bemajudar.domain.model.DonationItemDetail
import com.example.bemajudar.presentation.viewmodels.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun VolunteerMenu(
    navController: NavHostController,
    userEmail: String,
    userViewModel: UserViewModel
) {
    val notificationList = remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    val volunteerName = userViewModel.name
    // Estado para controlar o menu de perfil
    var expanded by remember { mutableStateOf(false) }
    val donationsList = remember { mutableStateListOf<DonationItemDetail>() }
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    // Carregar o número de notificações pendentes
    LaunchedEffect(userEmail) {
        fetchPendingNotifications(userEmail) { notifications ->
            notificationList.value = notifications.filter { it["state"] == "Pendente" }
        }
    }

    // Buscar as doações ao abrir a tela
    LaunchedEffect(Unit) {
        db.collection("donations").get()
            .addOnSuccessListener { result ->
                donationsList.clear()
                result.documents.forEach { document ->
                    val items = document.get("items") as? List<Map<String, Any>> ?: emptyList()
                    items.forEach { item ->
                        donationsList.add(
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
        // Cabeçalho
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.bemajudar),
                contentDescription = "Logo Bem-Ajudar",
                modifier = Modifier
                    .size(80.dp)
                    .weight(1f)
            )

            Spacer(modifier = Modifier.weight(0.5f))

            // Ícones com Menu Dropdown
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.notifications),
                    contentDescription = "Notificação",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(8.dp)
                )

                Box {
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Perfil",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .padding(8.dp)
                        )
                    }

                    // Dropdown Menu com opções
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(
                            text = { Text("Criar Evento") },
                            onClick = {
                                expanded = false
                                navController.navigate("createEvent")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Nova Doação") },
                            onClick = {
                                expanded = false
                                navController.navigate("registerDonation")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Consultar Visitas") },
                            onClick = {
                                expanded = false
                                navController.navigate("viewVisitors")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Terminar Sessão", color = Color.Red) },
                            onClick = {
                                expanded = false
                                auth.signOut()
                                navController.navigate("login") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Bem-Vindo de Volta, $volunteerName!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botões de Ações
        Text("Opções Bem-Ajudar", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { navController.navigate("createVisit") },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF025997)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Criar Visita", color = Color.White, fontSize = 14.sp)
            }

            Button(
                onClick = { navController.navigate("levantamentos") },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF025997)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Levantamento", color = Color.White, fontSize = 14.sp)
            }

            Button(
                onClick = { navController.navigate("viewVisitors") },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF025997)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Consultar Visitas", color = Color.White, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Últimas Doações
        Text("Últimas Doações", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(10.dp))

        // ✅ Listagem das Doações em Cards com Imagem e Detalhes
        LazyColumn {
            items(donationsList) { donation ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
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
                        if (!donation.photoUri.isNullOrEmpty()) {
                            Image(
                                painter = rememberAsyncImagePainter(donation.photoUri),
                                contentDescription = "Foto de ${donation.name}",
                                modifier = Modifier
                                    .size(80.dp)
                                    .padding(end = 12.dp),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.ic_default_avatar),
                                contentDescription = "Imagem Padrão",
                                modifier = Modifier
                                    .size(80.dp)
                                    .padding(end = 12.dp),
                                contentScale = ContentScale.Crop
                            )
                        }

                        // Detalhes do Item
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Nome: ${donation.name}", fontWeight = FontWeight.Bold)
                            Text("Descrição: ${donation.description}")
                            Text("Quantidade: ${donation.quantity}")
                            Text("Tipo: ${donation.type}")
                        }
                    }
                }
            }
        }
    }
}

