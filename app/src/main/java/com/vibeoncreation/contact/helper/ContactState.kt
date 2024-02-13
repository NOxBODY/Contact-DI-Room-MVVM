package com.vibeoncreation.contact.helper

import com.vibeoncreation.contact.data.ContactModel

data class ContactState(
    val contacts: List<ContactModel> = emptyList(),
    val contactInEdit: ContactModel? = null,
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val isAddingContact: Boolean = false,
    val isEditingContact: Boolean = false,
    val sortType: SortType = SortType.FIRST_NAME
)
