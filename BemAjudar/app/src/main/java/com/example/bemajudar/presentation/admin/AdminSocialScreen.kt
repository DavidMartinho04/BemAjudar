package com.example.bemajudar.presentation.admin

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
import androidx.navigation.NavHostController

@Composable
fun SocialAreaAdminScreen(navController: NavHostController) {
    // Interface para a área social do administrador
    Column(
        modifier = Modifier
            .fillMaxSize() // Preenche todo o espaço disponível
            .padding(16.dp), // Margem interna de 16dp em toda a volta
        horizontalAlignment = Alignment.CenterHorizontally // Alinha os itens horizontalmente ao centro
    ) {
        // Título principal
        Text(
            text = "Área Social", // Texto do título
            fontSize = 30.sp, // Tamanho maior do texto
            fontWeight = FontWeight.Bold, // Texto em negrito
            color = Color.Black, // Cor preta para o texto
            modifier = Modifier.align(Alignment.Start) // Alinha o título ao início (esquerda)
        )

        Spacer(modifier = Modifier.height(32.dp)) // Espaçamento entre o título e o próximo elemento

        // Lista de opções com botões
        listOf(
            "Criar Evento" to "Criar um novo evento de voluntariado", // Opção: título e subtítulo
            "Gerir Eventos" to "Verificar os eventos que irão decorrer",
            "Gerir Voluntários" to "Verificar os voluntários do sistema",
            "Registar Visitante" to "Registar um novo visitante",
            "Consultar Visitas" to "Listagem de visitas diárias na loja"
        ).forEach { (title, subtitle) -> // Itera por cada par (título e subtítulo) da lista
            Button(
                onClick = {
                    when (title) {
                        "Gerir Voluntários" -> navController.navigate("volunteerManagement")
                        "Registar Visitante" -> navController.navigate("createVisitor")
                        "Consultar Visitas" -> navController.navigate("viewVisitors")
                        "Criar Evento" -> navController.navigate("createEvent")
                        "Gerir Eventos" -> navController.navigate("manageEventsScreen")
                    }
                }, // Ação executada ao clicar no botão
                modifier = Modifier
                    .fillMaxWidth() // Botão ocupa toda a largura disponível
                    .height(100.dp) // Altura do botão definida para 100dp
                    .padding(vertical = 12.dp), // Espaçamento vertical entre os botões
                shape = RoundedCornerShape(16.dp), // Cantos arredondados do botão com raio de 16dp
                colors = ButtonDefaults.buttonColors(containerColor = Color.White), // Cor de fundo branca
                border = BorderStroke(2.dp, Color(0xFF025997)) // Borda azul com espessura de 2dp
            ) {
                // Conteúdo do botão
                Column(
                    modifier = Modifier
                        .fillMaxWidth() // Preenche toda a largura do botão
                        .padding(start = 16.dp), // Margem interna no início (esquerda)
                    verticalArrangement = Arrangement.Center // Alinha verticalmente ao centro
                ) {
                    // Título do botão
                    Text(
                        text = title, // Texto do título
                        color = Color(0xFF025997), // Cor azul
                        fontWeight = FontWeight.Bold, // Texto em negrito
                        fontSize = 20.sp, // Tamanho maior para o texto
                        modifier = Modifier.padding(bottom = 4.dp) // Espaçamento inferior entre título e subtítulo
                    )
                    // Subtítulo do botão
                    Text(
                        text = subtitle, // Texto do subtítulo
                        color = Color.Gray, // Cor cinzenta
                        fontSize = 16.sp // Tamanho médio do texto
                    )
                }
            }
        }
    }
}
