package com.example.bemajudar.utils

import android.content.Context
import android.widget.Toast
import com.example.bemajudar.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

suspend fun clearAllUsers(context: Context) {
    val roomDb = AppDatabase.getDatabase(context)
    CoroutineScope(Dispatchers.IO).launch {
        roomDb.userDao().deleteAllUsers()
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Todos os dados do Room foram apagados.", Toast.LENGTH_SHORT).show()
        }
    }
}
