package com.example.bemajudar.domain.model

import android.net.Uri

data class DonationItem(
    val name: String = "",
    val quantity: Int = 1,
    val description: String = "",
    val type: String = "Alimentação",
    val photoUri: Uri? = null
)
