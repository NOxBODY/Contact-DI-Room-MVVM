package com.vibeoncreation.contact.ui.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.vibeoncreation.contact.helper.ContactState
import com.vibeoncreation.contact.helper.UserEvent

@Composable
fun AddContactDialog(
    state: ContactState,
    onEvent: (UserEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = {
            onEvent(UserEvent.HideDialog)
        }
    ) {
        Card(
            modifier = modifier,
            shape = RoundedCornerShape(5.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(8.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Add Contact",
                    fontSize = 20.sp,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Bold
                )
                TextField(
                    value = state.firstName,
                    onValueChange = {
                        onEvent(UserEvent.SetFirstName(it))
                    },
                    placeholder = {
                        Text(text = "First Name")
                    }
                )
                TextField(
                    value = state.lastName,
                    onValueChange = {
                        onEvent(UserEvent.SetLastName(it))
                    },
                    placeholder = {
                        Text(text = "Last Name")
                    }
                )
                NewNumbersPart(
                    state = state,
                    onEvent = onEvent
                )
                Button(
                    onClick = {
                        onEvent(UserEvent.SaveContact)
                    }
                ) {
                    Text(
                        text = "Save"
                    )
                }
            }
        }
    }
}