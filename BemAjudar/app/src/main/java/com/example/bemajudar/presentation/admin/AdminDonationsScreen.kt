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
fun DonationsAreaAdminScreen(navController: NavHostController) {
    // Estrutura principal da interface para a área de doações do administrador
    Column(
        modifier = Modifier
            .fillMaxSize() // Preenche todo o espaço disponível
            .padding(16.dp), // Margem de 16dp em toda a volta
        horizontalAlignment = Alignment.CenterHorizontally // Alinha os itens horizontalmente ao centro
    ) {
        // Título principal
        Text(
            text = "Área Doações", // Texto do título
            fontSize = 30.sp, // Tamanho grande para o título
            fontWeight = FontWeight.Bold, // Texto em negrito
            color = Color.Black, // Cor preta
            modifier = Modifier.align(Alignment.Start) // Alinha o título ao início (esquerda)
        )

        Spacer(modifier = Modifier.height(32.dp)) // Espaçamento entre o título e o próximo elemento

        // Lista de opções com botões
        listOf(
            "Registar Doação" to "Registar uma nova doação", // Título e descrição do botão
            "Gerir Doações" to "Verificar as doações do inventário",
            "Gerir Itens" to "Verificar os itens do inventário",
            "Consultar Levantamentos" to "Listagem de levantamentos efetuados"
        ).forEach { (title, subtitle) -> // Itera por cada par (título e subtítulo) da lista
            Button(
                onClick = {
                    when (title) {
                        "Registar Doação" -> navController.navigate("registerDonation")
                        // Outros casos podem ser adicionados aqui
                    }
                }, // Ação executada ao clicar no botão
                modifier = Modifier
                    .fillMaxWidth() // Botão ocupa toda a largura disponível
                    .height(100.dp) // Altura do botão
                    .padding(vertical = 12.dp), // Espaçamento vertical entre os botões
                shape = RoundedCornerShape(16.dp), // Cantos arredondados com raio de 16dp
                colors = ButtonDefaults.buttonColors(containerColor = Color.White), // Cor de fundo branca
                border = BorderStroke(2.dp, Color(0xFF025997)) // Borda com espessura de 2dp e cor azul
            ) {
                // Conteúdo do botão
                Column(
                    modifier = Modifier
                        .fillMaxWidth() // Preenche a largura do botão
                        .padding(start = 16.dp), // Margem interna no início (esquerda) do botão
                    verticalArrangement = Arrangement.Center // Alinha verticalmente ao centro
                ) {
                    // Título do botão
                    Text(
                        text = title, // Texto do título
                        color = Color(0xFF025997), // Cor azul
                        fontWeight = FontWeight.Bold, // Texto em negrito
                        fontSize = 20.sp, // Tamanho grande do texto
                        modifier = Modifier.padding(bottom = 4.dp) // Espaçamento abaixo do título
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
