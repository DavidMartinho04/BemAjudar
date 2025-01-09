package com.example.bemajudar.presentation.login

// Importações necessárias para os componentes Compose e Firebase
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bemajudar.R
import com.example.bemajudar.data.AppDatabase
import com.example.bemajudar.presentation.createaccount.hashPassword
import com.example.bemajudar.presentation.viewmodels.UserViewModel
import com.example.bemajudar.utils.isInternetAvailable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    userViewModel: UserViewModel,
    onLoginSuccess: (String) -> Unit, // Callback para navegação com base no tipo de utilizador
    onCreateAccountClick: () -> Unit // Callback para navegação ao ecrã de criação de conta
) {
    // Definições de cores para a interface
    val primaryColor = Color(0xFF025997)
    val secondaryColor = Color(0xFF6F6F6F)
    val textFieldBackground = Color(0xFFF2F2F2)
    val blackColor = Color(0xFF000000)

    // Estados para email e palavra-passe
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) } // Controla a visibilidade da palavra-passe

    // Inicializa Firebase Auth e Firestore
    val context = LocalContext.current
    val roomDb = AppDatabase.getDatabase(context)
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo da aplicação
        Image(
            painter = painterResource(id = R.drawable.bemajudar),
            contentDescription = "Logo Bem Ajudar",
            modifier = Modifier
                .size(300.dp)
                .padding(top = 24.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Mensagem de boas-vindas
        Text(
            text = "Bem Vindo",
            fontSize = 22.sp,
            fontWeight = FontWeight.Normal,
            color = blackColor,
            textAlign = TextAlign.Center
        )
        Text(
            text = "à Bem Ajudar!",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = primaryColor,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Campo de Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email", color = secondaryColor) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Ícone de Email",
                    tint = primaryColor
                )
            },
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = textFieldBackground,
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = textFieldBackground,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
            modifier = Modifier.fillMaxWidth(0.95f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de Palavra-Passe
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Palavra-Passe", color = secondaryColor) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Ocultar Palavra-Passe" else "Mostrar Palavra-Passe",
                        tint = primaryColor
                    )
                }
            },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = textFieldBackground,
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = textFieldBackground,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
            modifier = Modifier.fillMaxWidth(0.95f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botão de Iniciar Sessão
        Button(
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    val isConnected = isInternetAvailable(context)

                    CoroutineScope(Dispatchers.IO).launch {
                        val roomDb = AppDatabase.getDatabase(context)
                        val auth = FirebaseAuth.getInstance()
                        val db = FirebaseFirestore.getInstance()

                        if (!isConnected) {
                            // 🔒 Login OFFLINE usando a password inserida comparada com a hash no Room
                            val localUser = roomDb.userDao().getUserByEmail(email)
                            val hashedPassword = hashPassword(password)
                            withContext(Dispatchers.Main) {
                                if (localUser != null && localUser.authToken == hashedPassword) {
                                    onLoginSuccess(localUser.userType)
                                } else {
                                    Toast.makeText(context, "Palavra-passe incorreta (offline).", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            // 🔑 Login ONLINE usando Firebase Authentication
                            auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val uid = auth.currentUser?.uid
                                        if (uid != null) {
                                            db.collection("users").document(uid).get()
                                                .addOnSuccessListener { document ->
                                                    if (document.exists()) {
                                                        val userType = document.getString("userType") ?: "Voluntário"
                                                        val userName = document.getString("name") ?: "Voluntário"
                                                        userViewModel.name = userName
                                                        onLoginSuccess(userType)
                                                    } else {
                                                        Toast.makeText(context, "Utilizador não encontrado (online).", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                                .addOnFailureListener {
                                                    Toast.makeText(context, "Erro ao buscar utilizador no Firestore.", Toast.LENGTH_SHORT).show()
                                                }
                                        }
                                    } else {
                                        Toast.makeText(context, "Erro no login: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    }
                } else {
                    Toast.makeText(context, "Por favor, preencha todos os campos!", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .width(180.dp)
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF025997))
        ) {
            Text(
                text = "Iniciar Sessão",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Texto para redefinir a palavra-passe
        TextButton(
            onClick = {
                if (email.isNotEmpty()) {
                    auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    context,
                                    "Email para redefinição enviado para $email!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                val errorMessage = translateFirebaseError(task.exception?.message)
                                Toast.makeText(context, "Erro: $errorMessage", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(context, "Por favor, insira o email para redefinir a palavra-passe.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "Esqueci-me da palavra passe",
                color = primaryColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Texto para criar conta
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Ainda não tem conta?",
                color = secondaryColor,
                fontSize = 14.sp
            )
            TextButton(onClick = onCreateAccountClick) {
                Text(
                    text = "Criar Conta",
                    color = primaryColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

// Função para traduzir mensagens de erro do Firebase
fun translateFirebaseError(message: String?): String {
    val errorMessages = mapOf(
        "The email address is badly formatted." to "O formato do email é inválido.",
        "The password is invalid or the user does not have a password." to "A palavra-passe está incorreta.",
        "There is no user record corresponding to this identifier. The user may have been deleted." to "Este email não está registado.",
        "The email address is already in use by another account." to "O email já está em uso por outra conta.",
        "Password should be at least 6 characters" to "A palavra-passe deve ter pelo menos 6 caracteres.",
        "We have blocked all requests from this device due to unusual activity. Try again later." to "Bloqueamos os pedidos deste dispositivo devido a atividade incomum. Tente mais tarde."
    )
    return errorMessages[message] ?: "Ocorreu um erro inesperado. Tente novamente."
}