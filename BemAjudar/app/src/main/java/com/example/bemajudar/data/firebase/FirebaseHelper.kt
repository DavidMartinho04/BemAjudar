package com.example.bemajudar.data.firebase

import android.content.Context
import android.widget.Toast
import com.example.bemajudar.data.AppDatabase
import com.example.bemajudar.data.local.UserEntity
import com.example.bemajudar.domain.model.DonationItem
import com.example.bemajudar.domain.model.DonationItemDetail
import com.example.bemajudar.presentation.createaccount.hashPassword
import com.example.bemajudar.presentation.viewmodels.UserViewModel
import com.example.bemajudar.utils.isInternetAvailable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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

// Função para enviar os dados ao Firestore (corrigida)
fun sendDonationToFirestore(
    donorName: String,
    donationDescription: String,
    deliveryDate: String,
    items: List<DonationItem>,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
) {
    val db = FirebaseFirestore.getInstance()

    // Transformar os itens em um formato compatível com o Firestore
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
                "photoUrl" to (item.photoUri?.toString() ?: "") // Garantir string ou vazio
            )
        }
    )

    // Adicionar a doação ao Firestore
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

    // Gerar um novo documento com um ID automaticamente
    val newDocRef = db.collection("visitors").document()
    newDocRef.set(visitorData + ("id" to newDocRef.id)) // Garante que o ID seja armazenado
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { e -> onFailure(e) }
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
                visitorData + ("id" to document.id) // Adiciona o ID do documento ao mapa
            }
            onSuccess(visitorsList)
        }
        .addOnFailureListener { exception ->
            onFailure(exception)
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

fun registerLevantamento(visitorId: String, selectedItems: List<DonationItemDetail>) {
    val db = FirebaseFirestore.getInstance()
    val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

    selectedItems.forEach { item ->
        // Adicionar o levantamento à coleção 'levantamentos'
        val levantamento = mapOf(
            "visitorId" to visitorId,
            "name" to item.name,
            "description" to item.description,
            "quantity" to item.quantity,
            "type" to item.type,
            "dateLevantamento" to currentDate
        )

        db.collection("levantamentos")
            .add(levantamento)
            .addOnSuccessListener {
                println("Levantamento registado com sucesso!")
            }
            .addOnFailureListener { e ->
                println("Erro ao registar o levantamento: ${e.message}")
            }

        // **Remover o item correspondente da coleção 'donations'**
        db.collection("donations")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val itemsList = document.get("items") as? MutableList<Map<String, Any>> ?: mutableListOf()

                    // Remover o item específico da lista de itens
                    val updatedItems = itemsList.filterNot {
                        it["name"] == item.name &&
                                it["description"] == item.description &&
                                (it["quantity"] as Long).toInt() == item.quantity
                    }

                    // Atualizar a lista no Firestore
                    db.collection("donations").document(document.id)
                        .update("items", updatedItems)
                        .addOnSuccessListener {
                            println("Item removido com sucesso!")
                        }
                        .addOnFailureListener { e ->
                            println("Erro ao remover o item: ${e.message}")
                        }
                }
            }
            .addOnFailureListener { e ->
                println("Erro ao aceder à coleção 'donations': ${e.message}")
            }
    }
}
