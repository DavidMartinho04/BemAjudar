package com.example.bemajudar.presentation.donations

import com.example.bemajudar.domain.model.DonationItem
import com.google.firebase.firestore.FirebaseFirestore

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
