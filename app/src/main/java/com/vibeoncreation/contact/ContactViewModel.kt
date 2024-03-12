package com.vibeoncreation.contact

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vibeoncreation.contact.data.ContactDao
import com.vibeoncreation.contact.data.ContactModel
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
            when(it) {
                SortType.FIRST_NAME -> dao.getContactsAscByFirstName()
                SortType.LAST_NAME -> dao.getContactsAscByLastName()
                SortType.PHONE_NUMBER -> dao.getContactsAscByPhoneNumber()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    private val _state = savedStateHandle.getStateFlow(StateKey.SAVED_STATE, ContactState())

    val state = combine(
        _state, _sortType, _contacts
    ) { state, sortType, contacts ->
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
            is UserEvent.SetPhoneNumber -> {
                val saveState = _state.value.copy(
                    phoneNumber = event.phoneNumber
                )
                savedStateHandle[StateKey.SAVED_STATE] = saveState
            }
            UserEvent.ShowAddDialog -> {
                val saveState = _state.value.copy(
                    isAddingContact = true
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
                    phoneNumber = event.contact.phoneNumber,
                    contactIdInEdit = event.contact.id,
                    isEditingContact = true
                )
                savedStateHandle[StateKey.SAVED_STATE] = saveState
            }

            is UserEvent.EditContact -> {
                val firstName = state.value.firstName
                val lastName = state.value.lastName
                val phoneNumber = state.value.phoneNumber
                if (firstName.isBlank() || lastName.isBlank() || phoneNumber.isBlank()) {
                    return
                }
                val contact = ContactModel(
                    id = event.contactId,
                    firstName = firstName,
                    lastName = lastName,
                    phoneNumber = phoneNumber
                )
                viewModelScope.launch {
                    dao.addContact(contact)
                }
                resetState()
            }
        }
    }

    private fun resetState() {
        val saveState = _state.value.copy(
            isAddingContact = false,
            isEditingContact = false,
            firstName = "",
            lastName = "",
            phoneNumber = "",
            contactIdInEdit = -1
        )
        savedStateHandle[StateKey.SAVED_STATE] = saveState
    }
}