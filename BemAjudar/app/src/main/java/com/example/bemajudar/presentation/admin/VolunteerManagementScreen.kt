package com.example.bemajudar.presentation.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
    var searchQuery by remember { mutableStateOf("") }

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
                // Exibir mensagem de erro, se necessário
            }
    }

    fun deleteVolunteer(volunteer: Volunteer) {
        FirebaseFirestore.getInstance().collection("users")
            .document(volunteer.id)
            .delete()
            .addOnSuccessListener {
                volunteers.remove(volunteer)
            }
            .addOnFailureListener {
                // Tratar o erro, se necessário
            }
    }

    // Filtrar voluntários com base na pesquisa
    val filteredVolunteers = volunteers.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.email.contains(searchQuery, ignoreCase = true)
    }

    // Layout da tela
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 30.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Título
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = "Gerir Voluntários",
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

        Spacer(modifier = Modifier.height(13.dp))
        // Lista de voluntários filtrados
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(filteredVolunteers.size) { index ->
                val volunteer = filteredVolunteers[index]

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                        .clickable {
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
                        // Foto de perfil
                        if (!volunteer.photoUrl.isNullOrEmpty()) {
                            Image(
                                painter = rememberAsyncImagePainter(volunteer.photoUrl),
                                contentDescription = "Foto de ${volunteer.name}",
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.ic_default_avatar),
                                contentDescription = "Avatar padrão",
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Nome e dados do voluntário com ícone alinhado
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
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

                            // Ícone de eliminação ao lado do nome
                            IconButton(onClick = { deleteVolunteer(volunteer) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar Voluntário",
                                    tint = Color(0xFF025997)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
