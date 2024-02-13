package com.vibeoncreation.contact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vibeoncreation.contact.data.ContactDao
import com.vibeoncreation.contact.data.ContactModel
import com.vibeoncreation.contact.helper.ContactState
import com.vibeoncreation.contact.helper.SortType
import com.vibeoncreation.contact.helper.UserEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor(
    private val dao: ContactDao
): ViewModel() {
    private val _sortType = MutableStateFlow(SortType.FIRST_NAME)
    private val _contacts = _sortType
        .flatMapLatest {
            when(it) {
                SortType.FIRST_NAME -> dao.getContactsAscByFirstName()
                SortType.LAST_NAME -> dao.getContactsAscByLastName()
                SortType.PHONE_NUMBER -> dao.getContactsAscByPhoneNumber()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    private val _state = MutableStateFlow(ContactState())
    val state = combine(_state, _sortType, _contacts) { state, sortType, contacts ->
        state.copy(
            sortType = sortType,
            contacts = contacts
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ContactState())

    fun onEvent(event: UserEvent) {
        when(event) {
            is UserEvent.DeleteContact -> {
                viewModelScope.launch {
                    dao.deleteContact(event.contact)
                }
            }
            UserEvent.HideDialog -> {
                resetState()
            }
            UserEvent.SaveContact -> {
                val firstName = state.value.firstName
                val lastName = state.value.lastName
                val phoneNumber = state.value.phoneNumber
                if (firstName.isBlank() || lastName.isBlank() || phoneNumber.isBlank()) {
                    return
                }
                val contact = ContactModel(
                    firstName = firstName,
                    lastName = lastName,
                    phoneNumber = phoneNumber
                )
                viewModelScope.launch {
                    dao.addContact(contact)
                }
                resetState()
            }
            is UserEvent.SetFirstName -> {
                _state.update {
                    it.copy(
                        firstName = event.firstName
                    )
                }
            }
            is UserEvent.SetLastName -> {
                _state.update {
                    it.copy(
                        lastName = event.lastName
                    )
                }
            }
            is UserEvent.SetPhoneNumber -> {
                _state.update {
                    it.copy(
                        phoneNumber = event.phoneNumber
                    )
                }
            }
            UserEvent.ShowAddDialog -> {
                _state.update {
                    it.copy(
                        isAddingContact = true
                    )
                }
            }
            is UserEvent.SortContacts -> {
                _sortType.value = event.sortType
            }

            is UserEvent.ShowEditDialog -> {
                _state.update {
                    it.copy(
                        firstName = event.contact.firstName,
                        lastName = event.contact.lastName,
                        phoneNumber = event.contact.phoneNumber,
                        contactInEdit = event.contact,
                        isEditingContact = true
                    )
                }
            }

            is UserEvent.EditContact -> {
                val firstName = state.value.firstName
                val lastName = state.value.lastName
                val phoneNumber = state.value.phoneNumber
                if (firstName.isBlank() || lastName.isBlank() || phoneNumber.isBlank()) {
                    return
                }
                viewModelScope.launch {
                    dao.addContact(event.contact.copy(
                        firstName = state.value.firstName,
                        lastName = state.value.lastName,
                        phoneNumber = state.value.phoneNumber
                    ))
                }
                resetState()
            }
        }
    }

    private fun resetState() {
        _state.update {
            it.copy(
                isAddingContact = false,
                isEditingContact = false,
                firstName = "",
                lastName = "",
                phoneNumber = "",
                contactInEdit = null
            )
        }
    }
}