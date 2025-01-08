package com.example.bemajudar.presentation.volunteer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun SocialAreaVolunteerScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Área Social",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Lista de botões com ações corrigidas
        listOf(
            "Registar Visitante" to "Registar um novo visitante",
            "Gerir Visitantes" to "Verificar os visitantes do sistema",
            "Registar Visitas" to "Listar visitantes e registar visitas",
            "Consultar Visitas" to "Listar visitas com data de visita"
        ).forEach { (title, subtitle) ->
            Button(
                onClick = {
                    when (title) {
                        "Registar Visitante" -> navController.navigate("createVisitor")
                        "Gerir Visitantes" -> navController.navigate("manageVisitors")
                        "Registar Visitas" -> navController.navigate("createVisit")
                        "Consultar Visitas" -> navController.navigate("viewVisitors")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                border = BorderStroke(2.dp, Color(0xFF025997))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = title,
                        color = Color(0xFF025997),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = subtitle,
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
