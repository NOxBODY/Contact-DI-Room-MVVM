package com.vibeoncreation.contact.helper

import com.vibeoncreation.contact.data.Contact
import com.vibeoncreation.contact.data.PhoneNumber

sealed interface UserEvent {
    data object SaveContact: UserEvent
    data class SetFirstName( val firstName: String): UserEvent
    data class SetLastName(val lastName: String): UserEvent
    data object AddAnotherNumber: UserEvent
    data class DeleteNewNumber(val pos: Int): UserEvent
    data class DeleteExistingNumber(val pos: Int): UserEvent
    data class EditExistingNumber(val pos: Int, val changedValue: String): UserEvent
    data class EditNewNumber(val pos: Int, val changedValue: String): UserEvent
    data object ShowAddDialog: UserEvent
    data class ShowEditDialog(val contact: Contact, val numbers: List<PhoneNumber>) : UserEvent
    data object HideDialog: UserEvent
    data class SortContacts(val sortType: SortType): UserEvent
    data class DeleteContact(val contactId: Int): UserEvent
    data object EditContact: UserEvent
}