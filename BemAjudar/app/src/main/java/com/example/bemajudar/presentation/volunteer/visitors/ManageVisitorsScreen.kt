package com.example.bemajudar.presentation.volunteer.visitors

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.border
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.TextStyle
import com.example.bemajudar.data.firebase.getVisitorsFromFirestore
import com.example.bemajudar.data.firebase.updateVisitorData
import com.example.bemajudar.data.firebase.deleteVisitorFromFirestore
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit

@Composable
fun ManageVisitorsScreen() {
    var visitorsList by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    var selectedVisitor by remember { mutableStateOf<Map<String, Any>?>(null) }
    var searchText by remember { mutableStateOf("") }
    var showConfirmDeleteDialog by remember { mutableStateOf(false) }
    var visitorToDelete by remember { mutableStateOf<Map<String, Any>?>(null) }
    val context = LocalContext.current

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
            color = Color.Black,
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
            keyboardActions = KeyboardActions(onSearch = {}),
            textStyle = TextStyle(color = Color.Black),
            cursorBrush = SolidColor(primaryColor),
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
                        visitorToDelete = visitor
                        showConfirmDeleteDialog = true
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
                    Toast.makeText(context, "Visitante atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                    showUpdateDialog = false
                }
            )
        }
    }

    if (showConfirmDeleteDialog) {
        visitorToDelete?.let { visitor ->
            AlertDialog(
                onDismissRequest = { showConfirmDeleteDialog = false },
                confirmButton = {
                    Button(
                        onClick = {
                            val visitorId = visitor["id"].toString()
                            deleteVisitorFromFirestore(visitorId) {
                                visitorsList = visitorsList.filter { it["id"] != visitorId }
                                Toast.makeText(context, "Visitante eliminado com sucesso!", Toast.LENGTH_SHORT).show()
                            }
                            showConfirmDeleteDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                    ) {
                        Text("Confirmar", color = Color.White)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showConfirmDeleteDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = secondaryColor)
                    ) {
                        Text("Cancelar", color = Color.White)
                    }
                },
                title = { Text("Confirmar Eliminação") },
                text = { Text("Tem a certeza que deseja eliminar este visitante?") }
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = visitor["name"].toString().uppercase(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onUpdateClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = primaryColor)
                }

                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = primaryColor)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("NIF: ${visitor["nif"] ?: "N/A"}")
            Text("Morada: ${visitor["address"] ?: "N/A"}")
            Text("Contacto: ${visitor["contact"] ?: "N/A"}")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text("Cancelar", color = Color.White)
            }
        },
        title = { Text("Atualizar Visitante", color = Color.Black) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome", color = Color.Black) },
                    textStyle = TextStyle(color = Color.Black),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Black,
                        cursorColor = primaryColor
                    )
                )
                OutlinedTextField(
                    value = nif,
                    onValueChange = { nif = it },
                    label = { Text("NIF", color = Color.Black) },
                    textStyle = TextStyle(color = Color.Black),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Black,
                        cursorColor = primaryColor
                    )
                )
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Morada", color = Color.Black) },
                    textStyle = TextStyle(color = Color.Black),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Black,
                        cursorColor = primaryColor
                    )
                )
                OutlinedTextField(
                    value = contact,
                    onValueChange = { contact = it },
                    label = { Text("Contacto", color = Color.Black) },
                    textStyle = TextStyle(color = Color.Black),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Black,
                        cursorColor = primaryColor
                    )
                )
            }
        }
    )
}
