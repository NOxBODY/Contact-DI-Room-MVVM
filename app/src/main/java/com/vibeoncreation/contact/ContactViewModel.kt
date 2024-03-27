package com.vibeoncreation.contact

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vibeoncreation.contact.data.ContactDao
import com.vibeoncreation.contact.data.Contact
import com.vibeoncreation.contact.data.ContactWithNumbers
import com.vibeoncreation.contact.data.PhoneNumber
import com.vibeoncreation.contact.helper.ContactState
import com.vibeoncreation.contact.helper.SortType
import com.vibeoncreation.contact.helper.StateKey
import com.vibeoncreation.contact.helper.UserEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val dao: ContactDao
): ViewModel() {
    private val _sortType = savedStateHandle.getStateFlow(StateKey.SORT_TYPE, SortType.FIRST_NAME)
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _contacts = _sortType
        .flatMapLatest {
            Log.wtf("SortType", _sortType.value.name)
            when(it) {
                SortType.FIRST_NAME -> dao.getContactsAscByFirstName()
                SortType.LAST_NAME -> dao.getContactsAscByLastName()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    private val _state = savedStateHandle.getStateFlow(StateKey.SAVED_STATE, ContactState())

    val state = combine(
        _state, _sortType, _contacts
    ) { state, sortType, contacts ->
        state.copy(
            sortType = sortType,
            contacts = contacts.run {
                val contactList = mutableListOf<ContactWithNumbers>()
                var lastId = -1
                var lastContact: Contact? = null
                val tempList = mutableListOf<PhoneNumber>()
                this.forEach { contactNumber ->
                    val contact = Contact(
                        id = contactNumber.contactId,
                        firstName = contactNumber.firstName,
                        lastName = contactNumber.lastName
                    )
                    if (lastId == -1) {
                        lastContact = contact
                        lastId = contact.id
                    }
                    else if (lastId != contact.id) {
                        contactList.add(
                            ContactWithNumbers(
                                contact = lastContact!!,
                                phoneNumbers = tempList.toList()
                            )
                        )
                        tempList.clear()
                        lastContact = contact
                        lastId = contact.id
                    }
                    contactNumber.number?.let {
                        tempList.add(
                            PhoneNumber(
                                id = contactNumber.phoneId!!,
                                number = it,
                                contactId = contact.id
                            )
                        )
                    }
                }
                lastContact?.let {
                    contactList.add(
                        ContactWithNumbers(
                            contact = it,
                            phoneNumbers = tempList.toList()
                        )
                    )
                }
                tempList.clear()
                contactList.toList()
            }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ContactState())

    fun onEvent(event: UserEvent) {
        when(event) {
            is UserEvent.DeleteContact -> {
                viewModelScope.launch {
                    dao.deleteContact(event.contactId)
                }
            }
            UserEvent.HideDialog -> {
                resetState()
            }
            UserEvent.SaveContact -> {
                val firstName = _state.value.firstName
                val lastName = _state.value.lastName
                val newNumbers = _state.value.newNumbers.filter {
                    it != ""
                }
                if (firstName.isBlank() || lastName.isBlank() || newNumbers.isEmpty()) {
                    return
                }
                val contact = Contact(
                    firstName = firstName,
                    lastName = lastName
                )
                viewModelScope.launch {
                    dao.addContact(contact)
                    val id = dao.getLastContactId()
                    newNumbers.forEach {
                        val phoneNumber = PhoneNumber(
                            number = it,
                            contactId = id
                        )
                        dao.addNumber(phoneNumber)
                    }
                }
                resetState()
            }
            is UserEvent.SetFirstName -> {
                val saveState = _state.value.copy(
                    firstName = event.firstName
                )
                savedStateHandle[StateKey.SAVED_STATE] = saveState
            }
            is UserEvent.SetLastName -> {
                val saveState = _state.value.copy(
                    lastName = event.lastName
                )
                savedStateHandle[StateKey.SAVED_STATE] = saveState
            }
            UserEvent.ShowAddDialog -> {
                val saveState = _state.value.copy(
                    isAddingContact = true,
                    newNumbers = mutableListOf("")
                )
                savedStateHandle[StateKey.SAVED_STATE] = saveState
            }
            is UserEvent.SortContacts -> {
                savedStateHandle[StateKey.SORT_TYPE] = event.sortType
            }

            is UserEvent.ShowEditDialog -> {
                val saveState = _state.value.copy(
                    firstName = event.contact.firstName,
                    lastName = event.contact.lastName,
                    phoneNumbers = event.numbers.toMutableList(),
                    newNumbers = if (event.numbers.isEmpty()) mutableListOf("") else mutableListOf(),
                    contactIdInEdit = event.contact.id,
                    isEditingContact = true
                )
                savedStateHandle[StateKey.SAVED_STATE] = saveState
            }

            UserEvent.EditContact -> {
                val firstName = _state.value.firstName
                val lastName = _state.value.lastName
                val newNumbers = _state.value.newNumbers.filter {
                    it != ""
                }
                if (firstName.isBlank() || lastName.isBlank()) {
                    return
                }
                val id = _state.value.contactIdInEdit
                println(id)
                val contact = Contact(
                    id = id,
                    firstName = firstName,
                    lastName = lastName,
                )
                val editedExistingNumbers = _state.value.phoneNumbers
                val toBeDeletedNumbers = _state.value.toBeDeletedExistingNumbers
                viewModelScope.launch {
                    dao.updateContact(contact)
                    toBeDeletedNumbers.forEach {
                        dao.deleteNumber(it.id)
                    }
                    editedExistingNumbers.forEach {
                        dao.updateNumber(it)
                    }
                    newNumbers.forEach {
                        val newNumber = PhoneNumber(
                            number = it,
                            contactId = id
                        )
                        println(newNumber)
                        dao.addNumber(newNumber)
                    }
                }
                resetState()
            }

            UserEvent.AddAnotherNumber -> {
                val newNumbers = _state.value.newNumbers.apply {
                    add("")
                }
                val saveState = _state.value.copy(
                    newNumbers = newNumbers
                )
                savedStateHandle[StateKey.SAVED_STATE] = saveState
            }
            is UserEvent.DeleteExistingNumber -> {
                val toBeDeletedExistingNumber = _state.value.phoneNumbers[event.pos]
                val toBeDeletedExistingNumbers = _state.value.toBeDeletedExistingNumbers.apply {
                    add(toBeDeletedExistingNumber)
                }
                val phoneNumbers = _state.value.phoneNumbers.apply {
                    removeAt(event.pos)
                }
                val saveState = _state.value.copy(
                    toBeDeletedExistingNumbers = toBeDeletedExistingNumbers,
                    phoneNumbers = phoneNumbers
                )
                savedStateHandle[StateKey.SAVED_STATE] = saveState
            }
            is UserEvent.DeleteNewNumber -> {
                val newNumbers = _state.value.newNumbers.apply {
                    removeAt(event.pos)
                }
                val saveState = _state.value.copy(
                    newNumbers = newNumbers
                )
                savedStateHandle[StateKey.SAVED_STATE] = saveState
            }
            is UserEvent.EditExistingNumber -> {
                val phoneNumbers = _state.value.phoneNumbers.apply {
                    this[event.pos] = this[event.pos].copy(
                        number = event.changedValue
                    )
                }
                val saveState = _state.value.copy(
                    phoneNumbers = phoneNumbers
                )
                savedStateHandle[StateKey.SAVED_STATE] = saveState
            }
            is UserEvent.EditNewNumber -> {
                Log.wtf("EditNewNumber", event.changedValue)
                val newNumbers = _state.value.newNumbers.apply {
                    this[event.pos] = event.changedValue
                }
                val saveState = _state.value.copy(
                    newNumbers = newNumbers
                )
                savedStateHandle[StateKey.SAVED_STATE] = saveState
            }
        }
    }

    private fun resetState() {
        val saveState = _state.value.copy(
            isAddingContact = false,
            isEditingContact = false,
            firstName = "",
            lastName = "",
            contactIdInEdit = -1,
            phoneNumbers = mutableListOf(),
            phoneIdInEdit= -1,
            newNumbers = mutableListOf(),
            toBeDeletedExistingNumbers = mutableListOf(),
        )
        savedStateHandle[StateKey.SAVED_STATE] = saveState
    }
}