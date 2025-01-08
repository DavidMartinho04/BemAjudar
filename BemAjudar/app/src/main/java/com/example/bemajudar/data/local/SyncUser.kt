package com.example.bemajudar.data.local

import android.content.Context
import com.example.bemajudar.data.AppDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

suspend fun syncUserData(context: Context) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val roomDb = AppDatabase.getDatabase(context)

    val currentUser = auth.currentUser
    if (currentUser != null) {
        val userId = currentUser.uid
        try {
            // Obtém a password em texto plano a partir do FirebaseAuth (necessário para autenticação)
            val plainPassword = "defaultPassword" // Valor temporário, pois não é possível obter a password original diretamente

            // Busca os dados do utilizador no Firestore usando await
            val document = db.collection("users").document(userId).get().await()
            if (document.exists()) {
                // Obtém a password hasheada armazenada no Firestore
                val storedPasswordHash = document.getString("authToken") ?: "Sem Token"

                // Verifica se o utilizador já existe no Room
                val existingUser = roomDb.userDao().getUserById(userId)

                // Cria a entidade para o Room, usando a hash e a plainPassword
                val userToSave = UserEntity(
                    id = userId,
                    name = document.getString("name") ?: "Sem Nome",
                    email = document.getString("email") ?: "Sem Email",
                    photoUrl = document.getString("photoUrl"),
                    phone = document.getString("phone") ?: "Sem Telemóvel",
                    address = document.getString("address") ?: "Sem Morada",
                    postalCode = document.getString("postalCode") ?: "Sem Código Postal",
                    gender = document.getString("gender") ?: "Não especificado",
                    userType = document.getString("userType") ?: "Voluntário",
                    authToken = storedPasswordHash,  // Hashed
                    plainPassword = plainPassword,  // Texto plano (precisa ser guardado para o Auth)
                    isSynced = true
                )

                // Atualiza ou insere apenas se os dados forem diferentes
                withContext(Dispatchers.IO) {
                    if (existingUser == null) {
                        roomDb.userDao().insertUser(userToSave)
                    } else if (existingUser != userToSave) {
                        roomDb.userDao().updateUser(userToSave)
                    }
                }
            } else {
                println("Documento não encontrado no Firestore para o UID: $userId")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    } else {
        println("Nenhum utilizador autenticado encontrado.")
    }
}
