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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.bemajudar.presentation.admin.AdminMenu
import com.example.bemajudar.presentation.admin.DonationListScreen
import com.example.bemajudar.presentation.admin.DonationsAreaAdminScreen
import com.example.bemajudar.presentation.admin.SocialAreaAdminScreen
import com.example.bemajudar.presentation.admin.VolunteerDetailScreen
import com.example.bemajudar.presentation.admin.VolunteerManagementScreen
import com.example.bemajudar.presentation.createaccount.CreateAccountScreen
import com.example.bemajudar.presentation.createaccount.FinalizeAccountScreen
import com.example.bemajudar.presentation.events.CreateEventScreen
import com.example.bemajudar.presentation.events.NotificationsScreen
import com.example.bemajudar.presentation.donations.DonationFormScreen
import com.example.bemajudar.presentation.donations.items.DonationItemsListScreen
import com.example.bemajudar.presentation.login.LoginScreen
import com.example.bemajudar.presentation.pickups.LevantamentoFlowScreen
import com.example.bemajudar.presentation.pickups.LevantamentoListScreen
import com.example.bemajudar.presentation.viewmodels.UserViewModel
import com.example.bemajudar.presentation.volunteer.DonationsAreaVolunteerScreen
import com.example.bemajudar.presentation.events.ManageEventsScreen
import com.example.bemajudar.presentation.volunteer.SocialAreaVolunteerScreen
import com.example.bemajudar.presentation.volunteer.VolunteerMenu
import com.example.bemajudar.presentation.volunteer.visitors.CreateVisitScreen
import com.example.bemajudar.presentation.volunteer.visitors.CreateVisitorScreen
import com.example.bemajudar.presentation.volunteer.visitors.ManageVisitorsScreen
import com.example.bemajudar.presentation.volunteer.visitors.ViewVisitorsScreen

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
            // Ecrã de Login
            composable("login") {
                showBottomNav.value = false
                LoginScreen(

                    onLoginSuccess = { userType, userEmail ->
                        userViewModel.userType = userType
                        userViewModel.email = userEmail

                        if (userType == "Gestor") {
                            navController.navigate("menuAdmin")
                        } else {
                            navController.navigate("menuVolunteer")
                        }
                        showBottomNav.value = true
                    },
                    userViewModel = userViewModel,
                    onCreateAccountClick = {
                        // Navega para o ecrã de criação de conta
                        navController.navigate("createAccount")
                    }
                )
            }

            // Ecrã de Criação de Conta
            composable("createAccount") {
                showBottomNav.value = false
                CreateAccountScreen(
                    onNextClick = {
                        navController.navigate("finalizeAccount")
                    },
                    userViewModel = userViewModel
                )
            }

            // Fluxo de Levantamentos com múltiplos ecrãs
            composable("levantamentos") {
                LevantamentoFlowScreen()
            }

            // Ecrã de Finalização da Conta
            composable("finalizeAccount") {
                showBottomNav.value = false
                FinalizeAccountScreen(
                    onCreateAccountClick = {
                        navController.navigate("login")
                    },
                    userViewModel = userViewModel
                )
            }
            // Ecrãs do Gestor
            composable("menuAdmin") {
                showBottomNav.value = true
                AdminMenu(navController = navController, userViewModel)
            }
            composable("socialAdmin") {
                showBottomNav.value = true
                SocialAreaAdminScreen(navController = navController)
            }
            composable("donationsAdmin") {
                showBottomNav.value = true
                DonationsAreaAdminScreen(navController = navController)
            }
            // Gerir Voluntários
            composable("volunteerManagement") {
                showBottomNav.value = true
                VolunteerManagementScreen(navController = navController) // Chama o ecrã de gestão de voluntários
            }
            composable("volunteerDetail/{volunteerId}") { backStackEntry ->
                val volunteerId = backStackEntry.arguments?.getString("volunteerId") ?: ""
                VolunteerDetailScreen(volunteerId = volunteerId)
            }
            // Gerir Doações
            composable("listDonations") {
                showBottomNav.value = true
                DonationListScreen()
            }
            // Gerir Itens
            composable("listItems") {
                showBottomNav.value = true
                DonationItemsListScreen()
            }
            // Registar Doações
            composable("registerDonation") {
                val context = LocalContext.current
                showBottomNav.value = true
                DonationFormScreen(context = context)
            }
            // Ecrãs do Voluntário
            composable("menuVolunteer") {
                showBottomNav.value = true
                val userEmail = userViewModel.email
                VolunteerMenu(navController = navController, userEmail = userEmail, userViewModel)
            }
            composable(route = "socialVolunteer") {
                showBottomNav.value = true
                SocialAreaVolunteerScreen(navController = navController)
            }
            composable("donationsVolunteer") {
                showBottomNav.value = true
                DonationsAreaVolunteerScreen(navController = navController)
            }

            composable("createEvent") {
                showBottomNav.value = true
                CreateEventScreen(
                    navController = navController,
                    userViewModel = userViewModel
                )
            }

            composable(
                route = "notificationsScreen/{userEmail}",
                arguments = listOf(navArgument("userEmail") { type = NavType.StringType })
            ) { backStackEntry ->
                val userEmail = backStackEntry.arguments?.getString("userEmail") ?: ""
                NotificationsScreen(userEmail = userEmail)
            }

            composable("manageEventsScreen") {
                showBottomNav.value = true
                ManageEventsScreen()
            }


            composable("createVisitor") {
                showBottomNav.value = true
                CreateVisitorScreen(navController = navController)
            }
            composable("viewVisitors") {
                showBottomNav.value = true
                ViewVisitorsScreen(navController)
            }
            // ✅ Rota para a Listagem de Levantamentos
            composable("levantamentos") {
                LevantamentoListScreen()
            }
            composable("createVisit") {
                showBottomNav.value = true
                CreateVisitScreen()
            }
            composable("manageVisitors") {
                showBottomNav.value = true
                ManageVisitorsScreen()
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