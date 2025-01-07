package com.example.bemajudar.data.firebase

import android.content.Context
import android.widget.Toast
import com.example.bemajudar.data.AppDatabase
import com.example.bemajudar.data.local.UserEntity
import com.example.bemajudar.presentation.createaccount.hashPassword
import com.example.bemajudar.presentation.viewmodels.UserViewModel
import com.example.bemajudar.utils.isInternetAvailable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

fun registerUser(
    context: Context,
    userViewModel: UserViewModel,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val roomDb = AppDatabase.getDatabase(context)

    // Gera o authToken e faz o hash da password usando SHA-256
    val authToken = UUID.randomUUID().toString()
    val hashedPassword = hashPassword(userViewModel.password)
    val plainPassword = userViewModel.password // ✅ Guarda a password original também.

    if (isInternetAvailable(context)) {
        auth.createUserWithEmailAndPassword(userViewModel.email, plainPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = task.result?.user?.uid ?: return@addOnCompleteListener

                    // Dados para enviar ao Firestore (com a password hasheada)
                    val userData = mapOf(
                        "id" to userId,
                        "name" to userViewModel.name,
                        "email" to userViewModel.email,
                        "photoUrl" to (userViewModel.photoUrl ?: ""),
                        "phone" to userViewModel.phone,
                        "address" to userViewModel.address,
                        "postalCode" to userViewModel.postalCode,
                        "gender" to userViewModel.gender,
                        "userType" to userViewModel.userType,
                        "authToken" to hashedPassword
                    )

                    db.collection("users").document(userId).set(userData)
                        .addOnSuccessListener {
                            CoroutineScope(Dispatchers.IO).launch {
                                val userEntity = UserEntity(
                                    id = userId,
                                    name = userViewModel.name,
                                    email = userViewModel.email,
                                    photoUrl = userViewModel.photoUrl ?: "",
                                    phone = userViewModel.phone,
                                    address = userViewModel.address,
                                    postalCode = userViewModel.postalCode,
                                    gender = userViewModel.gender,
                                    userType = userViewModel.userType,
                                    authToken = hashedPassword,
                                    isSynced = true,
                                    plainPassword = plainPassword // ✅ Guarda a original no Room para sincronização futura
                                )
                                roomDb.userDao().insertUser(userEntity)
                                withContext(Dispatchers.Main) { onSuccess() }
                            }
                        }
                        .addOnFailureListener { e -> onFailure(e) }
                } else {
                    onFailure(task.exception ?: Exception("Erro ao criar utilizador no Firebase."))
                }
            }
    } else {
        // ✅ Caso offline, guarda a password original e hasheada no Room
        CoroutineScope(Dispatchers.IO).launch {
            val userEntity = UserEntity(
                id = authToken,
                name = userViewModel.name,
                email = userViewModel.email,
                photoUrl = userViewModel.photoUrl ?: "",
                phone = userViewModel.phone,
                address = userViewModel.address,
                postalCode = userViewModel.postalCode,
                gender = userViewModel.gender,
                userType = userViewModel.userType,
                authToken = hashedPassword,
                plainPassword = plainPassword, // ✅ Guarda a original
                isSynced = false
            )
            roomDb.userDao().insertUser(userEntity)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Utilizador guardado offline. Sincronizará quando online.", Toast.LENGTH_LONG).show()
                onSuccess()
            }
        }
    }
}

