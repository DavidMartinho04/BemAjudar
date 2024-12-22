package com.example.bemajudar.data.firebase

import com.example.bemajudar.presentation.viewmodels.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

fun registerUser(
    userViewModel: UserViewModel,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    // Criação do user no Firebase Authentication
    auth.createUserWithEmailAndPassword(userViewModel.email, userViewModel.password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = task.result?.user?.uid

                // Salva os dados no Firestore com o UID do Authentication
                val userData = mapOf(
                    "id" to userId,
                    "photoUri" to (userViewModel.photoUri?.toString() ?: ""),
                    "name" to userViewModel.name,
                    "birthDate" to userViewModel.birthDate,
                    "phone" to userViewModel.phone,
                    "email" to userViewModel.email,
                    "address" to userViewModel.address,
                    "postalCode" to userViewModel.postalCode,
                    "gender" to userViewModel.gender,
                    "userType" to userViewModel.userType
                )

                userId?.let {
                    db.collection("users").document(it)
                        .set(userData)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { e -> onFailure(e) }
                }
            } else {
                onFailure(task.exception ?: Exception("Erro desconhecido ao criar utilizador."))
            }
        }
}
