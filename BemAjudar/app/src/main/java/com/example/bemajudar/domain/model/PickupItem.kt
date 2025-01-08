package com.example.bemajudar.domain.model

data class PickupItem(
    val dateLevantamento: String = "",
    val name: String = "",
    val description: String = "",
    val quantity: Int = 0,
    val type: String = "",
    val visitorId: String = ""
)