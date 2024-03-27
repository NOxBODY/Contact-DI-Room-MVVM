package com.vibeoncreation.contact.ui.widgets

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
import com.vibeoncreation.contact.data.PhoneNumber
import com.vibeoncreation.contact.helper.ContactState
import com.vibeoncreation.contact.helper.UserEvent

@Composable
fun ExistingNumbersPart(
    state: ContactState,
    onEvent: (UserEvent) -> Unit
) {
    val existingNumbers = remember {
        mutableStateListOf<PhoneNumber>().apply {
            addAll(state.phoneNumbers)
        }
    }
    for (i in 0..<existingNumbers.size) {
        Row {
            TextField(
                value = existingNumbers[i].number,
                onValueChange = {
                    onEvent(UserEvent.EditExistingNumber(i, it))
                    existingNumbers[i] = existingNumbers[i].copy(
                        number = it
                    )
                },
                modifier = Modifier.weight(1F),
                placeholder = {
                    Text(text = "Phone Number")
                }
            )
            IconButton(onClick = {
                onEvent(UserEvent.DeleteExistingNumber(i))
                existingNumbers.removeAt(i)
            }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Contact"
                )
            }
        }
    }
}