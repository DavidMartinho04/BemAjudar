package com.example.bemajudar.presentation.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.firestore.FirebaseFirestore

data class Volunteer(
    val id: String,
    val name: String,
    val email: String,
    val photoUrl: String?,
    val address: String,
    val phone: String
)

@Composable
fun VolunteerManagementScreen(navController: NavHostController) {
    val volunteers = remember { mutableStateListOf<Volunteer>() }

    // Busca os dados no Firestore
    LaunchedEffect(Unit) {
        FirebaseFirestore.getInstance().collection("users")
            .whereEqualTo("userType", "Voluntário")
            .get()
            .addOnSuccessListener { result ->
                val fetchedVolunteers = result.map { document ->
                    Volunteer(
                        id = document.id,
                        name = document.getString("name") ?: "Sem Nome",
                        email = document.getString("email") ?: "Sem Email",
                        photoUrl = document.getString("photoUrl"),
                        address = document.getString("address") ?: "Sem Morada",
                        phone = document.getString("phone") ?: "Sem Telemóvel"
                    )
                }
                volunteers.addAll(fetchedVolunteers)
            }
            .addOnFailureListener {
                // Exibir mensagem de erro se necessário
            }
    }

    // Layout do ecrã
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 30.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título principal
        Text(
            text = "Gerir Voluntários",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(25.dp)) // Espaçamento abaixo do título

        // Lista de voluntários
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(volunteers.size) { index ->
                val volunteer = volunteers[index]

                Card(
                    modifier = Modifier
                        .fillMaxWidth() // Ocupa 100% da largura do ecrã
                        .padding(vertical = 10.dp)
                        .clickable { // Navegar para a página de detalhes ao clicar
                            navController.navigate("volunteerDetail/${volunteer.id}")
                        },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Foto de perfil com crop e estilo
                        if (volunteer.photoUrl != null) {
                            Image(
                                painter = rememberAsyncImagePainter(volunteer.photoUrl),
                                contentDescription = "Foto de ${volunteer.name}",
                                modifier = Modifier
                                    .size(50.dp) // Tamanho ajustado da imagem
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop // Faz o crop da imagem
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Dados do voluntário
                        Column {
                            Text(
                                text = volunteer.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = "Email: ${volunteer.email}",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "Morada: ${volunteer.address}",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            Text(
                                text = "Telemóvel: ${volunteer.phone}",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}
