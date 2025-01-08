
package com.example.bemajudar.presentation.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.Icons
import androidx.navigation.NavHostController
import com.example.bemajudar.R
import com.example.bemajudar.presentation.viewmodels.UserViewModel


@Composable
fun AdminMenu(
    navController: NavHostController,
    userViewModel: UserViewModel
) {
    val adminName = userViewModel.name

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
            // Logo centralizado à esquerda
            Image(
                painter = painterResource(id = R.drawable.bemajudar),
                contentDescription = "Logo Bem-Ajudar",
                modifier = Modifier
                    .size(80.dp)
                    .weight(1f)
            )

            Spacer(modifier = Modifier.weight(0.5f))

            // Ícones de notificação e perfil
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Ícone de Notificação
                Icon(
                    painter = painterResource(id = R.drawable.notifications),
                    contentDescription = "Notificação",
                    tint = Color.Black,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(8.dp)
                )

                // Ícone de Perfil usando Icons.Default.Person
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
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mensagem de boas-vindas
        Text(
            text = "Bem-Vindo de Volta, $adminName!",
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
                onClick = { navController.navigate("createEvent") },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6F6DF7)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Criar Evento", color = Color.White, fontSize = 14.sp)
            }

            Button(
                onClick = { /* Navegar para Doações */ },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6F6DF7)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Nova Doação", color = Color.White, fontSize = 14.sp)
            }

            Button(
                onClick = { /* Navegar para Consultar Visitas */ },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6F6DF7)),
                shape = RoundedCornerShape(12.dp)
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
