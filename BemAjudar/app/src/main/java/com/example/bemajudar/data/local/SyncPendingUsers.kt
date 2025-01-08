package com.example.bemajudar.data.local

import android.content.Context
import com.example.bemajudar.data.AppDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

suspend fun syncPendingUsers(context: Context) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val roomDb = AppDatabase.getDatabase(context)

    val unsyncedUsers = roomDb.userDao().getUnsyncedUsers()
    for (user in unsyncedUsers) {
        try {
            // ✅ Usa a password original armazenada para o Firebase Auth
            val result = auth.createUserWithEmailAndPassword(user.email, user.plainPassword!!).await()
            val userId = result.user?.uid ?: continue

            // ✅ Atualiza no Firestore com a password hasheada
            val userData = mapOf(
                "id" to userId,
                "name" to user.name,
                "email" to user.email,
                "photoUrl" to (user.photoUrl ?: ""),
                "phone" to user.phone,
                "address" to user.address,
                "postalCode" to user.postalCode,
                "gender" to user.gender,
                "userType" to user.userType,
                "authToken" to user.authToken
            )
            db.collection("users").document(userId).set(userData).await()

            // ✅ Marca como sincronizado e remove a plainPassword
            roomDb.userDao().markUserAsSynced(user.id)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

