package com.example.bemajudar.presentation.createaccount

import java.security.MessageDigest

fun hashPassword(password: String): String {
    val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}

// Função para verificar a password usando SHA-256
fun verifyPassword(inputPassword: String, storedHash: String): Boolean {
    val inputHash = hashPassword(inputPassword)
    return inputHash == storedHash
}
