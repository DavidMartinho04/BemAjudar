package com.example.bemajudar.data.firebase

import com.example.bemajudar.presentation.events.EventItem
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

fun saveEvent(
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
            volunteers.forEach { volunteerEmail  ->
                val notificationRef = db.collection("notifications").document()
                val notification = hashMapOf(
                    "notificationId" to notificationRef.id,
                    "eventId" to eventId,
                    "volunteerEmail" to volunteerEmail,
                    "state" to "Pendente"
                )
                notificationRef.set(notification)
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

fun fetchPendingNotifications(userEmail: String, onResult: (List<Map<String, Any>>) -> Unit) {
    val db = FirebaseFirestore.getInstance()

    db.collection("notifications")
        .whereEqualTo("volunteerEmail", userEmail)
        .get()
        .addOnSuccessListener { result ->
            val notifications = mutableListOf<Map<String, Any>>()

            val pendingFetches = result.documents.size
            if (pendingFetches == 0) {
                onResult(emptyList())
                return@addOnSuccessListener
            }

            result.documents.forEach { doc ->
                val notificationId = doc.id
                val eventId = doc.getString("eventId") ?: ""
                db.collection("events").document(eventId).get()
                    .addOnSuccessListener { eventDoc ->
                        val eventName = eventDoc.getString("name") ?: "Evento Desconhecido"
                        val eventDate = eventDoc.getString("date")
                        notifications.add(
                            mapOf(
                                "notificationId" to notificationId,
                                "title" to "Evento: $eventName",
                                "message" to "Foi convidado a participar neste evento.",
                                "date" to "Data: $eventDate",
                                "state" to (doc.getString("state") ?: "Desconhecido")
                            )
                        )

                        if (notifications.size == pendingFetches) {
                            onResult(notifications)
                        }
                    }
                    .addOnFailureListener {
                        println("Erro ao buscar detalhes do evento: ${it.message}")
                    }
            }
        }
        .addOnFailureListener {
            println("Erro ao buscar notificações: ${it.message}")
        }
}

fun updateNotificationState(notificationId: String, newState: String) {
    val db = FirebaseFirestore.getInstance()

    db.collection("notifications").document(notificationId)
        .update("state", newState)
        .addOnSuccessListener {
            println("Estado da notificação atualizado para: $newState")
        }
        .addOnFailureListener { e ->
            println("Erro ao atualizar notificação: ${e.message}")
        }
}

fun fetchEvents(onEventsFetched: (List<EventItem>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("events").get()
        .addOnSuccessListener { result ->
            val events = result.map { document ->
                EventItem(
                    id = document.id,
                    name = document.getString("name") ?: "Sem nome",
                    description = document.getString("description") ?: "Sem descrição",
                    date = document.getString("date") ?: "Sem data"
                )
            }
            onEventsFetched(events)
        }
        .addOnFailureListener { exception ->
            println("Erro ao buscar eventos: ${exception.message}")
        }
}
