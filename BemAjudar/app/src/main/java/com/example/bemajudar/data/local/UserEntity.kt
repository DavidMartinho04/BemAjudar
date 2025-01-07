package com.example.bemajudar.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val photoUrl: String?,
    val phone: String,
    val address: String,
    val postalCode: String,
    val gender: String,
    val userType: String,
    val authToken: String,           // ✅ Password hasheada para segurança
    val plainPassword: String?,      // ✅ Password original apenas para sincronização (nullable)
    val isSynced: Boolean
)
