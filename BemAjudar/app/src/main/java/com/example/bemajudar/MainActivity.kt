package com.example.bemajudar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.bemajudar.data.local.syncPendingUsers
import com.example.bemajudar.data.local.syncUserData
import com.example.bemajudar.presentation.navigation.AppNavigation
import com.example.bemajudar.presentation.viewmodels.UserViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Tenta sincronizar os dados pendentes e sincronizar com o Firebase
        lifecycleScope.launch {
            try {
                // Sincroniza os dados pendentes primeiro
                syncPendingUsers(applicationContext)
                // Sincroniza os dados do Firebase com o Room
                syncUserData(applicationContext)
            } catch (e: Exception) {
                e.printStackTrace()
                // Log para rastrear falhas
            }
        }

        // Configura o conteúdo da aplicação
        setContent {
            val navController = rememberNavController()
            val userViewModel: UserViewModel by viewModels()

            // Passa o `navController` e `userViewModel` para o sistema de navegação
            AppNavigation(navController, userViewModel)
        }
    }
}
