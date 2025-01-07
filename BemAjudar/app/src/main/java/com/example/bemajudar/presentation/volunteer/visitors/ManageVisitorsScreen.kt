package com.example.bemajudar.presentation.volunteer.visitors

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bemajudar.data.firebase.getVisitorsFromFirestore
import com.example.bemajudar.data.firebase.updateVisitorData
import com.example.bemajudar.data.firebase.deleteVisitorFromFirestore

@Composable
fun ManageVisitorsScreen() {
    var visitorsList by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    var selectedVisitor by remember { mutableStateOf<Map<String, Any>?>(null) }

    // Carregar dados ao iniciar o ecrã
    LaunchedEffect(Unit) {
        getVisitorsFromFirestore(
            onSuccess = { visitorsList = it },
            onFailure = { errorMessage = "Erro ao carregar os visitantes." }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Gerir Visitantes",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (errorMessage != null) {
            Text(text = errorMessage!!, color = Color.Red)
        }

        LazyColumn {
            items(visitorsList) { visitor ->
                VisitorCard(
                    visitor = visitor,
                    onUpdateClick = {
                        selectedVisitor = visitor
                        showUpdateDialog = true
                    },
                    onDeleteClick = {
                        val visitorId = visitor["id"].toString()
                        deleteVisitorFromFirestore(visitorId) {
                            visitorsList = visitorsList.filter { it["id"] != visitorId }
                        }
                    }
                )
            }
        }
    }

    // Formulário de atualização
    if (showUpdateDialog) {
        selectedVisitor?.let { visitor ->
            UpdateVisitorForm(
                visitor = visitor,
                onClose = { showUpdateDialog = false },
                onUpdateSuccess = { updatedVisitor ->
                    visitorsList = visitorsList.map { if (it["id"] == visitor["id"]) updatedVisitor else it }
                    showUpdateDialog = false
                }
            )
        }
    }
}

@Composable
fun VisitorCard(
    visitor: Map<String, Any>,
    onUpdateClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(1.dp, Color.LightGray, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = visitor["name"].toString().uppercase(),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Text("NIF: ${visitor["nif"] ?: "N/A"}")
        Text("Morada: ${visitor["address"] ?: "N/A"}")
        Text("Contacto: ${visitor["contact"] ?: "N/A"}")

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = onUpdateClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF025997))
            ) {
                Text("Atualizar", color = Color.White)
            }
            Button(
                onClick = onDeleteClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Eliminar", color = Color.White)
            }
        }
    }
}

@Composable
fun UpdateVisitorForm(
    visitor: Map<String, Any>,
    onClose: () -> Unit,
    onUpdateSuccess: (Map<String, Any>) -> Unit
) {
    var name by remember { mutableStateOf(visitor["name"].toString()) }
    var nif by remember { mutableStateOf(visitor["nif"].toString()) }
    var address by remember { mutableStateOf(visitor["address"].toString()) }
    var contact by remember { mutableStateOf(visitor["contact"].toString()) }

    AlertDialog(
        onDismissRequest = onClose,
        confirmButton = {
            Button(onClick = {
                val updatedData = mapOf(
                    "name" to name,
                    "nif" to nif,
                    "address" to address,
                    "contact" to contact
                )
                updateVisitorData(visitor["id"].toString(), updatedData) {
                    onUpdateSuccess(updatedData)
                }
            }) {
                Text("Atualizar")
            }
        },
        dismissButton = {
            Button(onClick = onClose) {
                Text("Cancelar")
            }
        },
        title = { Text("Atualizar Visitante") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nome") })
                OutlinedTextField(value = nif, onValueChange = { nif = it }, label = { Text("NIF") })
                OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Morada") })
                OutlinedTextField(value = contact, onValueChange = { contact = it }, label = { Text("Contacto") })
            }
        }
    )
}
