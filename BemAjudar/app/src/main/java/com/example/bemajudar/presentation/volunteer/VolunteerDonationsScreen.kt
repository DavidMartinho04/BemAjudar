package com.example.bemajudar.presentation.volunteer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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

@Composable
fun DonationsAreaVolunteerScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Área Doações",
            fontSize = 30.sp, // Título maior
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(32.dp)) // Espaçamento maior abaixo do título

        listOf(
            "Registar Visitante" to "Registar um novo visitante",
            "Gerir Eventos" to "Verificar os eventos que irão decorrer",
            "Gerir Visitantes" to "Verificar os visitantes do sistema",
            "Criar Visita" to "Registar uma nova visita na loja social",
            "Consultar Visitas" to "Listagem de visitas diárias na loja"
        ).forEach { (title, subtitle) ->
            Button(
                onClick = { /* Navegação futura */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp) // Altura maior para os botões
                    .padding(vertical = 12.dp), // Espaçamento entre botões
                shape = RoundedCornerShape(16.dp), // Cantos mais arredondados
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                border = BorderStroke(2.dp, Color(0xFF025997)) // Borda mais espessa
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp), // Alinha os textos à esquerda
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = title,
                        color = Color(0xFF025997),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp, // Tamanho maior para o título
                        modifier = Modifier.padding(bottom = 4.dp) // Espaçamento entre título e subtítulo
                    )
                    Text(
                        text = subtitle,
                        color = Color.Gray,
                        fontSize = 16.sp // Tamanho maior para o subtítulo
                    )
                }
            }
        }
    }
}
