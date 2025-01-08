package com.example.bemajudar.presentation.events

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.example.bemajudar.data.firebase.fetchEvents
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


data class EventItem(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val date: String = ""
)


@Composable
fun ManageEventsScreen() {
    val events = remember { mutableStateListOf<EventItem>() }

    fetchEvents{ fetchedEvents ->
        events.clear()
        events.addAll(fetchedEvents)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Título centralizado
        Text(
            text = "Gerir Eventos",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp)
        )

        // Lista de Eventos
        LazyColumn {
            items(events.size) { index ->
                val event = events[index]
                EventCard(
                    event = event,
                    onEdit = { updatedEvent ->
                        events[index] = updatedEvent
                    },
                    onDelete = {
                        events.removeAt(index)
                    }
                )
            }
        }
    }
}