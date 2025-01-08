package com.example.bemajudar.domain.model

data class DonationItemDetail(
    val name: String = "",
    val description: String = "",
    val quantity: Int = 1,
    val type: String = "Alimentação",
    val photoUri: String? = null
)