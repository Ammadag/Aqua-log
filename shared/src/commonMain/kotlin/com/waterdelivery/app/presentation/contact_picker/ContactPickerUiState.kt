package com.waterdelivery.app.presentation.contact_picker

import com.waterdelivery.app.presentation.ui.components.BottomSheetUiState

data class ContactPickerUiState(
    val searchQuery: String = "",
    val contacts: List<ContactItem> = emptyList(),
    val allContacts: List<ContactItem> = emptyList(),
    val selectedContact: ContactItem? = null,
    val isLoading: Boolean = false,
    val hasPermission: Boolean = true,
    val errorMessage: String? = null,
    val bottomSheetState: BottomSheetUiState = BottomSheetUiState()
)
