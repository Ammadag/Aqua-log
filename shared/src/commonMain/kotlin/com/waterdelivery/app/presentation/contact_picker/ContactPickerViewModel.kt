package com.waterdelivery.app.presentation.contact_picker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waterdelivery.app.core.platform.ContactFetchResult
import com.waterdelivery.app.core.platform.ContactManager
import com.waterdelivery.app.presentation.ui.components.BottomSheetType
import com.waterdelivery.app.presentation.ui.components.BottomSheetUiState
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

    fun onPermissionDenied(onRequestPermissionAgain: (() -> Unit)? = null) {
        _uiState.update {
            it.copy(
                hasPermission = false,
                bottomSheetState = BottomSheetUiState(
                    isVisible = true,
                    title = "Permission Denied",
                    message = "Access to contacts was denied. Contact permission is required to import contacts into AquaLog.",
                    type = BottomSheetType.PERMISSION,
                    primaryButtonText = "GRANT PERMISSION",
                    secondaryButtonText = "DISMISS",
                    onPrimaryClick = onRequestPermissionAgain,
                    onSecondaryClick = { dismissBottomSheet() }
                )
            )
        }
    }

    fun loadContacts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = contactManager.getContacts()) {
                is ContactFetchResult.Success -> {
                    _uiState.update {
                        it.copy(
                            contacts = result.contacts,
                            allContacts = result.contacts,
                            isLoading = false,
                            hasPermission = true
                        )
                    }
                }
                is ContactFetchResult.PermissionDenied -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            hasPermission = false,
                            bottomSheetState = BottomSheetUiState(
                                isVisible = true,
                                title = "Permission Required",
                                message = "Contact permission is required to view and import your contacts.",
                                type = BottomSheetType.PERMISSION,
                                primaryButtonText = "GRANT PERMISSION",
                                secondaryButtonText = "DISMISS"
                            )
                        )
                    }
                }
                is ContactFetchResult.SecurityError -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message,
                            bottomSheetState = BottomSheetUiState(
                                isVisible = true,
                                title = "Security Error",
                                message = result.message,
                                type = BottomSheetType.ERROR,
                                primaryButtonText = "RETRY",
                                secondaryButtonText = "DISMISS",
                                onPrimaryClick = { loadContacts() },
                                onSecondaryClick = { dismissBottomSheet() }
                            )
                        )
                    }
                }
                is ContactFetchResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message,
                            bottomSheetState = BottomSheetUiState(
                                isVisible = true,
                                title = "Unable to Load Contacts",
                                message = result.message,
                                type = BottomSheetType.ALERT,
                                primaryButtonText = "RETRY",
                                secondaryButtonText = "DISMISS",
                                onPrimaryClick = { loadContacts() },
                                onSecondaryClick = { dismissBottomSheet() }
                            )
                        )
                    }
                }
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

    fun onContinueClick(onContactConfirmed: (ContactItem) -> Unit) {
        val selected = uiState.value.selectedContact ?: return
        _uiState.update {
            it.copy(
                bottomSheetState = BottomSheetUiState(
                    isVisible = true,
                    title = "Confirm Contact",
                    message = "Selected ${selected.name} (${selected.phoneNumber}) as customer contact.",
                    type = BottomSheetType.SUCCESS,
                    primaryButtonText = "CONFIRM & CONTINUE",
                    secondaryButtonText = "CANCEL",
                    onPrimaryClick = {
                        onContactConfirmed(selected)
                    },
                    onSecondaryClick = { dismissBottomSheet() }
                )
            )
        }
    }

    fun dismissBottomSheet() {
        _uiState.update { it.copy(bottomSheetState = it.bottomSheetState.copy(isVisible = false)) }
    }
}
