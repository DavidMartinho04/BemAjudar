package com.example.bemajudar.data.firebase

import com.example.bemajudar.presentation.viewmodels.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

fun registerUser(
    userViewModel: UserViewModel, // ViewModel que contém os dados do utilizador
    onSuccess: () -> Unit, // Callback para executar em caso de sucesso
    onFailure: (Exception) -> Unit // Callback para executar em caso de falha
) {
    val auth = FirebaseAuth.getInstance() // Obtem a instância do Firebase Authentication
    val db = FirebaseFirestore.getInstance() // Obtem a instância do Firestore

    // Criação do utilizador no Firebase Authentication
    auth.createUserWithEmailAndPassword(userViewModel.email, userViewModel.password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = task.result?.user?.uid // Obtem o UID do utilizador criado

                // Dados do utilizador a serem guardados no Firestore
                val userData = mapOf(
                    "id" to userId, // ID único do utilizador
                    "photoUrl" to (userViewModel.photoUrl ?: ""), // URL da imagem de perfil
                    "name" to userViewModel.name, // Nome do utilizador
                    "birthDate" to userViewModel.birthDate, // Data de nascimento
                    "phone" to userViewModel.phone, // Número de telemóvel
                    "email" to userViewModel.email, // Endereço de email
                    "address" to userViewModel.address, // Morada do utilizador
                    "postalCode" to userViewModel.postalCode, // Código postal
                    "gender" to userViewModel.gender, // Género do utilizador
                    "userType" to userViewModel.userType // Tipo de utilizador (Gestor ou Voluntário)
                )

                userId?.let {
                    // Guarda os dados no Firestore, na coleção "users", com o UID como documento
                    db.collection("users").document(it)
                        .set(userData) // Adiciona os dados do utilizador
                        .addOnSuccessListener { onSuccess() } // Chama o callback de sucesso
                        .addOnFailureListener { e -> onFailure(e) } // Chama o callback de falha com a exceção
                }
            } else {
                // Caso a criação do utilizador falhe, chama o callback de falha com a exceção
                onFailure(task.exception ?: Exception("Erro desconhecido ao criar utilizador."))
            }
        }
}

fun saveEventToFirebase(
    name: String,
    description: String,
    date: String,
    volunteers: List<String>
) {
    val db = FirebaseFirestore.getInstance()
    val eventId = db.collection("events").document().id

    val event = hashMapOf(
        "id" to eventId,
        "name" to name,
        "description" to description,
        "date" to date,
        "state" to "Ativo"
    )

    db.collection("events").document(eventId).set(event)
        .addOnSuccessListener {
            // Criar notificações para os voluntários
            volunteers.forEach { volunteerId ->
                val notification = hashMapOf(
                    "eventId" to eventId,
                    "volunteerId" to volunteerId,
                    "state" to "Pendente"
                )
                db.collection("notifications").add(notification)
            }
        }
        .addOnFailureListener { e ->
            // Log de erro
            println("Erro ao salvar evento: $e")
        }
}

fun fetchVolunteers(
    userViewModel: UserViewModel,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
) {
    val db = FirebaseFirestore.getInstance()

    db.collection("users").whereEqualTo("userType", "Voluntário")
        .get()
        .addOnSuccessListener { documents ->
            for (document in documents) {
                val id = document.id
                val name = document.getString("name") ?: "Desconhecido"
                val email = document.getString("email") ?: ""

                // Verificar se o voluntário já está na lista
                if (userViewModel.volunteers.none { it.id == id }) {
                    userViewModel.addVolunteer(name, email, id)
                }
            }
            onSuccess()
        }
        .addOnFailureListener { exception ->
            onFailure(exception)
        }
}
