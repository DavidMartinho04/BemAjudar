package com.example.bemajudar.presentation.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun VolunteerDetailScreen(volunteerId: String) {
    var volunteer by remember { mutableStateOf<Volunteer?>(null) }

    // Buscar dados do voluntário pelo ID
    LaunchedEffect(volunteerId) {
        FirebaseFirestore.getInstance().collection("users")
            .document(volunteerId)
            .get()
            .addOnSuccessListener { document ->
                volunteer = Volunteer(
                    id = document.id,
                    name = document.getString("name") ?: "Sem Nome",
                    email = document.getString("email") ?: "Sem Email",
                    photoUrl = document.getString("photoUrl"),
                    address = document.getString("address") ?: "Sem Morada",
                    phone = document.getString("phone") ?: "Sem Telemóvel"
                )
            }
    }

    // Layout do ecrã de detalhes
    volunteer?.let {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, top = 30.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título
            Text(
                text = "Detalhes do Voluntário",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Foto ampliada
            if (it.photoUrl != null) {
                Image(
                    painter = rememberAsyncImagePainter(it.photoUrl),
                    contentDescription = "Foto de ${it.name}",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nome do voluntário (em destaque)
            Text(
                text = it.name,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF000000) // Preto
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Dados do voluntário (formatados com ícones)
            VolunteerDetailRow(icon = Icons.Default.Email, label = "Email", value = it.email)
            VolunteerDetailRow(icon = Icons.Default.Home, label = "Morada", value = it.address)
            VolunteerDetailRow(icon = Icons.Default.Phone, label = "Telemóvel", value = it.phone)
        }
    }
}

@Composable
fun VolunteerDetailRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp), // Espaçamento entre as linhas
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Ícone à esquerda
        Icon(
            imageVector = icon,
            contentDescription = "$label Icon",
            tint = Color(0xFF000000), // Preto
            modifier = Modifier
                .size(24.dp)
                .padding(end = 8.dp)
        )
        // Texto descritivo
        Column {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black
            )
        }
    }
}
