package com.example.bemajudar.presentation.volunteer.visitors

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bemajudar.data.firebase.getVisitorsFromFirestore
import com.example.bemajudar.data.firebase.updateVisitorData
import com.example.bemajudar.data.firebase.deleteVisitorFromFirestore
import androidx.compose.ui.graphics.SolidColor

@Composable
fun ManageVisitorsScreen() {
    var visitorsList by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    var selectedVisitor by remember { mutableStateOf<Map<String, Any>?>(null) }
    var searchText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        getVisitorsFromFirestore(
            onSuccess = { visitorsList = it },
            onFailure = { errorMessage = "Erro ao carregar os visitantes." }
        )
    }

    val filteredVisitors = visitorsList.filter {
        it["name"].toString().contains(searchText, ignoreCase = true)
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
            color = primaryColor,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        BasicTextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .border(1.dp, Color.Gray, RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { }),
            textStyle = TextStyle(color = Color.Black),
            cursorBrush = SolidColor(Color(0xFF025997)),
            decorationBox = { innerTextField ->
                if (searchText.isEmpty()) {
                    Text("Pesquisar por nome...", color = Color.Gray)
                }
                innerTextField()
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage != null) {
            Text(text = errorMessage!!, color = Color.Red)
        }

        LazyColumn {
            items(filteredVisitors) { visitor ->
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
            .border(1.dp, secondaryColor, RoundedCornerShape(16.dp))
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
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
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
            Button(
                onClick = {
                    val updatedData = mapOf(
                        "name" to name,
                        "nif" to nif,
                        "address" to address,
                        "contact" to contact
                    )
                    updateVisitorData(visitor["id"].toString(), updatedData) {
                        onUpdateSuccess(updatedData)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
            ) {
                Text("Atualizar", color = Color.White)
            }
        },
        dismissButton = {
            Button(
                onClick = onClose,
                colors = ButtonDefaults.buttonColors(containerColor = secondaryColor)
            ) {
                Text("Cancelar", color = Color.White)
            }
        },
        title = { Text("Atualizar Visitante", color = primaryColor) },
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
