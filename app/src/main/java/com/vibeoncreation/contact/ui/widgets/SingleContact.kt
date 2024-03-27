package com.vibeoncreation.contact.ui.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.vibeoncreation.contact.data.Contact
import com.vibeoncreation.contact.data.PhoneNumber
import com.vibeoncreation.contact.helper.UserEvent

@Composable
fun SingleContact(
    contact: Contact,
    numbers: List<PhoneNumber>,
    onEvent: (UserEvent) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .weight(1F)
        ) {
            Text(
                text = "${contact.firstName} ${contact.lastName}",
                fontSize = 20.sp
            )
            if (numbers.isEmpty()) {
                Text(
                    text = "No number!",
                    fontSize = 12.sp
                )
            }
            else {
                numbers.forEach { phoneNumber ->
                    Text(
                        text = phoneNumber.number,
                        fontSize = 12.sp
                    )
                }
            }

        }
        IconButton(
            onClick = {
                onEvent(UserEvent.ShowEditDialog(contact, numbers))
            }
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Contact"
            )
        }
        IconButton(
            onClick = {
                onEvent(UserEvent.DeleteContact(contact.id))
            }
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Contact"
            )
        }
    }
}