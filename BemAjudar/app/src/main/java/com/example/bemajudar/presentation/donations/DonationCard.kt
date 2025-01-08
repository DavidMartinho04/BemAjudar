package com.example.bemajudar.presentation.donations

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bemajudar.domain.model.DonationItemSimple

@Composable
fun DonationCard(donation: DonationItemSimple) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Implementar navegação para detalhes */ }
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = donation.deliveryDate,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Descrição: ${donation.description}")
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Doador: ${donation.donorName}", fontWeight = FontWeight.SemiBold)
        }
    }
}