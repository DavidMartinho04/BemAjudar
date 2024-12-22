package com.example.bemajudar.presentation.navigation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.bemajudar.presentation.admin.AdminMenu
import com.example.bemajudar.presentation.admin.DonationsAreaAdminScreen
import com.example.bemajudar.presentation.admin.SocialAreaAdminScreen
import com.example.bemajudar.presentation.createaccount.CreateAccountScreen
import com.example.bemajudar.presentation.createaccount.FinalizeAccountScreen
import com.example.bemajudar.presentation.login.LoginScreen
import com.example.bemajudar.presentation.viewmodels.UserViewModel
import com.example.bemajudar.presentation.volunteer.DonationsAreaVolunteerScreen
import com.example.bemajudar.presentation.volunteer.SocialAreaVolunteerScreen
import com.example.bemajudar.presentation.volunteer.VolunteerMenu

@Composable
fun AppNavigation(navController: NavHostController, userViewModel: UserViewModel) {
    val showBottomNav = remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            if (showBottomNav.value) {
                BottomNavigationBar(navController, userViewModel.userType)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(innerPadding)
        ) {
            // Tela de Login
            composable("login") {
                showBottomNav.value = false
                LoginScreen(
                    onLoginSuccess = { userType ->
                        userViewModel.userType = userType
                        if (userType == "Gestor") {
                            navController.navigate("menuAdmin")
                        } else {
                            navController.navigate("menuVolunteer")
                        }
                        showBottomNav.value = true
                    },
                    onCreateAccountClick = {
                        navController.navigate("createAccount")
                    }
                )
            }

            // Tela de Criação de Conta
            composable("createAccount") {
                showBottomNav.value = false
                CreateAccountScreen(
                    onNextClick = {
                        navController.navigate("finalizeAccount")
                    },
                    userViewModel = userViewModel
                )
            }

            // Tela de Finalização da Conta
            composable("finalizeAccount") {
                showBottomNav.value = false
                FinalizeAccountScreen(
                    onCreateAccountClick = {
                        navController.navigate("login")
                    },
                    userViewModel = userViewModel
                )
            }
            // Telas do Gestor
            composable("menuAdmin") {
                showBottomNav.value = true
                AdminMenu()
            }
            composable("socialAdmin") {
                showBottomNav.value = true
                SocialAreaAdminScreen()
            }
            composable("donationsAdmin") {
                showBottomNav.value = true
                DonationsAreaAdminScreen()
            }
            // Telas do Voluntário
            composable("menuVolunteer") {
                showBottomNav.value = true
                VolunteerMenu()
            }
            composable("socialVolunteer") {
                showBottomNav.value = true
                SocialAreaVolunteerScreen()
            }
            composable("donationsVolunteer") {
                showBottomNav.value = true
                DonationsAreaVolunteerScreen()
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController, userType: String) {
    NavigationBar(
        containerColor = Color.White,
        modifier = Modifier.border(1.dp, Color.LightGray)
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color(0xFF025997)) },
            label = { Text("Menu", color = Color(0xFF025997)) },
            selected = false,
            onClick = {
                if (userType == "Gestor") {
                    navController.navigate("menuAdmin")
                } else {
                    navController.navigate("menuVolunteer")
                }
            }
        )
        if (userType == "Gestor") {
            // Menu do Gestor
            NavigationBarItem(
                icon = { Icon(Icons.Default.Person, contentDescription = "Social Area", tint = Color(0xFF025997)) },
                label = { Text("Social", color = Color(0xFF025997)) },
                selected = false,
                onClick = { navController.navigate("socialAdmin") }
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.Bookmark, contentDescription = "Donations Area", tint = Color(0xFF025997)) },
                label = { Text("Doações", color = Color(0xFF025997)) },
                selected = false,
                onClick = { navController.navigate("donationsAdmin") }
            )
        } else {
            // Menu do Voluntário
            NavigationBarItem(
                icon = { Icon(Icons.Default.Person, contentDescription = "Social Area", tint = Color(0xFF025997)) },
                label = { Text("Social", color = Color(0xFF025997)) },
                selected = false,
                onClick = { navController.navigate("socialVolunteer") }
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.Bookmark, contentDescription = "Donations Area", tint = Color(0xFF025997)) },
                label = { Text("Doações", color = Color(0xFF025997)) },
                selected = false,
                onClick = { navController.navigate("donationsVolunteer") }
            )
        }
    }
}
