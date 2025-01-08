package com.example.bemajudar.presentation.volunteer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.bemajudar.R
import com.example.bemajudar.data.firebase.fetchPendingNotifications
import com.example.bemajudar.presentation.viewmodels.UserViewModel

@Composable
fun VolunteerMenu(
    navController: NavHostController,
    userEmail: String,
    userViewModel: UserViewModel
) {
    val notificationList = remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    val volunteerName = userViewModel.name

    // Carregar o número de notificações pendentes
    LaunchedEffect(userEmail) {
        fetchPendingNotifications(userEmail) { notifications ->
            notificationList.value = notifications.filter { it["state"] == "Pendente" }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Cabeçalho com Logo e Ícones
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
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.weight(0.5f))

            // Ícones de Notificação com Contador
            BadgedBox(
                badge = {
                    if (notificationList.value.isNotEmpty()) {
                        Badge { Text(notificationList.value.size.toString()) }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notificações",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(8.dp)
                        .clickable { navController.navigate("notificationsScreen/$userEmail") }
                )
            }

            Spacer(modifier = Modifier.width(24.dp))


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

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Bem-Vindo de Volta, $volunteerName!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botões de Ações
        Text("Opções Bem-Ajudar", fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { /* Navegar para Visitas */},
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF025997))
            ) {
                Text("Criar Visita", color = Color.White, fontSize = 14.sp)
            }

            Button(
                onClick = { /* Navegar para Doações */ },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF025997))
            ) {
                Text("Levantamento", color = Color.White, fontSize = 14.sp)
            }

            Button(
                onClick = { /* Navegar para Consultar Visitas */ },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF025997))
            ) {
                Text("Consultar Visitas", color = Color.White, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Últimas Doações
        Text("Últimas Doações", fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))
        RecentDonationsSection()
    }
}

@Composable
fun RecentDonationsSection() {
    val donations = listOf(
        "CHUTEIRAS ADIDAS N41 - Data: 11/12/2024",
        "CASACO SÃO PAULO XL - Data: 13/12/2024"
    )

    Column {
        donations.forEach { donation ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.donation),
                    contentDescription = "Donation Icon",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(donation)
            }
        }
    }
}