package com.example.bemajudar.domain.model

// Modelo de doação simplificado
data class DonationItemSimple(
    val id: String,
    val deliveryDate: String,
    val description: String,
    val donorName: String
)