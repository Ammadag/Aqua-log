package com.waterdelivery.app.presentation.contact_picker

data class ContactPickerUiState(
    val searchQuery: String = "",
    val contacts: List<ContactItem> = emptyList(),
    val allContacts: List<ContactItem> = emptyList(),
    val selectedContact: ContactItem? = null,
    val isLoading: Boolean = false,
    val hasPermission: Boolean = true
)
