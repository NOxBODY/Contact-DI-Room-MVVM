package com.vibeoncreation.contact.helper

import com.vibeoncreation.contact.data.ContactModel
import java.io.Serializable

data class ContactState(
    val contacts: List<ContactModel> = emptyList(),
    val contactIdInEdit: Int = -1,
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val isAddingContact: Boolean = false,
    val isEditingContact: Boolean = false,
    val sortType: SortType = SortType.FIRST_NAME
): Serializable
