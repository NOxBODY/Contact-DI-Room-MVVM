package com.vibeoncreation.contact.ui.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.vibeoncreation.contact.helper.ContactState
import com.vibeoncreation.contact.helper.UserEvent

@Composable
fun NewNumbersPart(
    state: ContactState,
    onEvent: (UserEvent) -> Unit
) {
    val newNumbers = remember {
        mutableStateListOf<String>().apply {
            addAll(state.newNumbers)
        }
    }
    for (i in 0..<newNumbers.size) {
        Row {
            TextField(
                value = state.newNumbers[i],
                onValueChange = {
                    onEvent(UserEvent.EditNewNumber(i, it))
                    newNumbers[i] = it
                },
                modifier = Modifier.weight(1F),
                placeholder = {
                    Text(text = "Phone Number")
                }
            )
            IconButton(onClick = {
                onEvent(UserEvent.DeleteNewNumber(i))
                newNumbers.removeAt(i)
            }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Contact"
                )
            }
        }
    }
    Text(
        text = "Add another number",
        color = Color.Blue,
        modifier = Modifier.clickable {
            onEvent(UserEvent.AddAnotherNumber)
            newNumbers.add("")
        }
    )
}