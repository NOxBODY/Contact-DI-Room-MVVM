package com.vibeoncreation.contact.helper

import com.vibeoncreation.contact.data.ContactWithNumbers
import com.vibeoncreation.contact.data.PhoneNumber
import java.io.Serializable

data class ContactState(
    val contacts: List<ContactWithNumbers> = emptyList(),
    val contactIdInEdit: Int = -1,
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumbers: MutableList<PhoneNumber> = mutableListOf(),
    val phoneIdInEdit: Int = -1,
    val newNumbers: MutableList<String> = mutableListOf(),
    val toBeDeletedExistingNumbers: MutableList<PhoneNumber> = mutableListOf(),
    val isAddingContact: Boolean = false,
    val isEditingContact: Boolean = false,
    val sortType: SortType = SortType.FIRST_NAME
): Serializable
