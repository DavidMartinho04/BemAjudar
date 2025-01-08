package com.example.bemajudar.data.firebase


import com.example.bemajudar.presentation.events.EventItem
import android.content.Context
import android.widget.Toast
import com.example.bemajudar.data.AppDatabase
import com.example.bemajudar.data.local.UserEntity
import com.example.bemajudar.domain.model.DonationItem
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

    val authToken = UUID.randomUUID().toString()
    val hashedPassword = hashPassword(userViewModel.password)
    val plainPassword = userViewModel.password 

    if (isInternetAvailable(context)) {
        auth.createUserWithEmailAndPassword(userViewModel.email, plainPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = task.result?.user?.uid ?: return@addOnCompleteListener

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
                                    plainPassword = plainPassword 
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
                plainPassword = plainPassword, 
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


fun sendDonationToFirestore(
    donorName: String,
    donationDescription: String,
    deliveryDate: String,
    items: List<DonationItem>,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
) {
    val db = FirebaseFirestore.getInstance()

    
    val donationData = mapOf(
        "donorName" to donorName,
        "donationDescription" to donationDescription,
        "deliveryDate" to deliveryDate,
        "items" to items.map { item ->
            mapOf(
                "name" to item.name,
                "description" to item.description,
                "quantity" to item.quantity,
                "type" to item.type,
                "photoUrl" to (item.photoUri?.toString() ?: "") 
            )
        }
    )

    
    db.collection("donations")
        .add(donationData)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { exception -> onFailure(exception) }
}

fun addVisitorToFirestore(
    name: String,
    nif: String,
    address: String,
    visitDate: String,
    contact: String,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val visitorData = mapOf(
        "name" to name,
        "nif" to nif,
        "address" to address,
        "contact" to contact,
        "lastVisit" to visitDate
    )

    val newDocRef = db.collection("visitors").document()
    newDocRef.set(visitorData + ("id" to newDocRef.id)) 
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { e -> onFailure(e) }
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
  
    
fun getVisitorsFromFirestore(
    onSuccess: (List<Map<String, Any>>) -> Unit,
    onFailure: (Exception) -> Unit
) {
    val db = FirebaseFirestore.getInstance()

    db.collection("visitors")
        .get()
        .addOnSuccessListener { result ->
            val visitorsList = result.documents.map { document ->
                val visitorData = document.data ?: emptyMap()
                visitorData + ("id" to document.id) 
            }
            onSuccess(visitorsList)
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

fun updateVisitTimeInFirestore(visitorId: String, lastVisit: String) {
    if (visitorId.isBlank()) {
        println("Erro: visitorId inválido.")
        return
    }
    val db = FirebaseFirestore.getInstance()
    db.collection("visitors").document(visitorId)
        .update("lastVisit", lastVisit)
        .addOnSuccessListener {
            println("Hora da última visita atualizada com sucesso!")
        }
        .addOnFailureListener { e ->
            println("Erro ao atualizar a hora da última visita: $e")
        }
}

fun updateVisitorData(visitorId: String, updatedData: Map<String, Any>, onSuccess: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("visitors").document(visitorId)
        .update(updatedData)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { e -> println("Erro ao atualizar visitante: $e") }
}

fun deleteVisitorFromFirestore(visitorId: String, onSuccess: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("visitors").document(visitorId)
        .delete()
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { e -> println("Erro ao eliminar visitante: $e") }
}

