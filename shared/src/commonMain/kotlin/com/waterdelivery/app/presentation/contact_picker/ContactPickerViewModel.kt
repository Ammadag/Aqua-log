package com.waterdelivery.app.presentation.contact_picker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waterdelivery.app.core.platform.ContactManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ContactPickerViewModel(
    private val contactManager: ContactManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContactPickerUiState())
    val uiState: StateFlow<ContactPickerUiState> = _uiState.asStateFlow()

    init {
        checkPermissionAndLoadContacts()
    }

    fun checkPermissionAndLoadContacts() {
        val hasPermission = contactManager.hasContactPermission()
        _uiState.update { it.copy(hasPermission = hasPermission) }
        
        if (hasPermission) {
            loadContacts()
        }
    }

    private fun loadContacts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val contacts = contactManager.getContacts()
            _uiState.update { 
                it.copy(
                    contacts = contacts,
                    allContacts = contacts,
                    isLoading = false
                ) 
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        val filtered = if (query.isBlank()) uiState.value.allContacts 
                       else uiState.value.allContacts.filter { 
                           it.name.contains(query, ignoreCase = true) || 
                           it.phoneNumber.contains(query) 
                       }
        _uiState.update { it.copy(searchQuery = query, contacts = filtered) }
    }

    fun onContactSelect(contact: ContactItem) {
        _uiState.update { it.copy(selectedContact = contact) }
    }
}
