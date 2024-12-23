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
fun SocialAreaVolunteerScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), // Espaçamento interno da coluna
        horizontalAlignment = Alignment.CenterHorizontally // Alinha elementos horizontalmente
    ) {
        Text(
            text = "Área Social",
            fontSize = 30.sp, // Tamanho do texto do título principal
            fontWeight = FontWeight.Bold, // Texto em negrito para destaque
            color = Color.Black, // Cor preta para o título
            modifier = Modifier.align(Alignment.Start) // Alinha o texto à esquerda
        )

        Spacer(modifier = Modifier.height(32.dp)) // Espaçamento entre o título e os botões

        // Lista de ações disponíveis na área social
        listOf(
            "Registar Visitante" to "Registar um novo visitante",
            "Gerir Eventos" to "Verificar os eventos que irão decorrer",
            "Gerir Visitantes" to "Verificar os visitantes do sistema",
            "Criar Visita" to "Registar uma nova visita na loja social",
            "Consultar Visitas" to "Listagem de visitas diárias na loja"
        ).forEach { (title, subtitle) ->
            Button(
                onClick = { /* Implementar navegação ou funcionalidade futura */ },
                modifier = Modifier
                    .fillMaxWidth() // O botão ocupa toda a largura disponível
                    .height(100.dp) // Define a altura dos botões
                    .padding(vertical = 12.dp), // Espaçamento entre os botões
                shape = RoundedCornerShape(16.dp), // Cantos arredondados para suavidade visual
                colors = ButtonDefaults.buttonColors(containerColor = Color.White), // Fundo branco para contraste
                border = BorderStroke(2.dp, Color(0xFF025997)) // Borda azul para realce
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth() // O conteúdo do botão ocupa toda a largura disponível
                        .padding(start = 16.dp), // Espaçamento interno alinhado à esquerda
                    verticalArrangement = Arrangement.Center // Centraliza verticalmente o conteúdo do botão
                ) {
                    Text(
                        text = title, // Texto principal do botão
                        color = Color(0xFF025997), // Cor azul para o título
                        fontWeight = FontWeight.Bold, // Texto em negrito
                        fontSize = 20.sp, // Tamanho do texto principal
                        modifier = Modifier.padding(bottom = 4.dp) // Espaçamento entre o título e o subtítulo
                    )
                    Text(
                        text = subtitle, // Texto descritivo do botão
                        color = Color.Gray, // Cor cinza para o subtítulo
                        fontSize = 16.sp // Tamanho menor para o subtítulo
                    )
                }
            }
        }
    }
}
