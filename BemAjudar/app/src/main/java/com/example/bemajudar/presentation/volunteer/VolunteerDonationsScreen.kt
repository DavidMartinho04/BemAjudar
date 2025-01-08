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
            fontSize = 30.sp, // Tamanho grande para o título principal
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(32.dp)) // Espaçamento entre o título e os botões

        // Lista de opções da área de doações
        listOf(
            "Registar Levantamento" to "Registar um novo levantamento",
            "Consultar Levantamentos" to "Listagem de levantamentos efetuados",
            "Gerir Doações" to "Verificar as doações do inventário",
            "Gerir Itens" to "Verificar os itens do inventário"
        ).forEach { (title, subtitle) ->
            Button(
                onClick = { /* Aqui será implementada a navegação ou funcionalidade correspondente */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp) // Altura personalizada para os botões
                    .padding(vertical = 12.dp), // Espaçamento vertical entre os botões
                shape = RoundedCornerShape(16.dp), // Cantos arredondados nos botões
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                border = BorderStroke(2.dp, Color(0xFF025997)) // Define uma borda azul ao redor dos botões
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp), // Alinha os textos à esquerda dentro do botão
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = title,
                        color = Color(0xFF025997), // Cor azul para o título do botão
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp, // Tamanho maior para o título do botão
                        modifier = Modifier.padding(bottom = 4.dp) // Espaçamento entre título e subtítulo
                    )
                    Text(
                        text = subtitle,
                        color = Color.Gray, // Cor cinza para o subtítulo
                        fontSize = 16.sp // Tamanho menor para o subtítulo
                    )
                }
            }
        }
    }
}
