package com.example.bemajudar

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bemajudar.presentation.createaccount.CreateAccountScreen
import com.example.bemajudar.presentation.createaccount.FinalizeAccountScreen
import com.example.bemajudar.presentation.login.LoginScreen
import com.example.bemajudar.presentation.viewmodels.UserViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Inicialização do controlador de navegação
            val navController = rememberNavController()
            val userViewModel: UserViewModel by viewModels() // ViewModel compartilhado entre telas

            // Configuração do NavHost
            NavHost(navController = navController, startDestination = "login") {
                // Tela de Login
                composable("login") {
                    LoginScreen(
                        onLoginClick = { email, password ->
                            // Adicione aqui a lógica para o login do usuário
                            Toast.makeText(
                                this@MainActivity,
                                "Login com email: $email",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        onCreateAccountClick = {
                            // Navega para a tela de criação de conta
                            navController.navigate("createAccount")
                        }
                    )
                }

                // Tela de Criação de Conta Inicial
                composable("createAccount") {
                    CreateAccountScreen(
                        onNextClick = {
                            // Os valores já foram armazenados no UserViewModel.
                            // Navega para a tela de finalização
                            navController.navigate("finalizeAccount")
                        },
                        userViewModel = userViewModel // Passa o ViewModel para o CreateAccountScreen
                    )
                }

                // Tela de Finalização da Conta
                composable("finalizeAccount") {
                    FinalizeAccountScreen(
                        onCreateAccountClick = {
                            navController.navigate("login") // Apenas navega para a tela de login
                        },
                        userViewModel = userViewModel // Passa o ViewModel para o FinalizeAccountScreen
                    )
                }
            }
        }
    }
}
