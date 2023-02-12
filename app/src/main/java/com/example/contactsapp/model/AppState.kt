package com.example.contactsapp.model

/**
 * Various states of the app that are to be
 * rendered by [com.example.contactsapp.ui.MainActivity]
 */
sealed class AppState {
    object None : AppState()

    object Loading : AppState()

    data class ContactsLoaded(
        val contacts: List<Contact>
    ) : AppState()

    data class Error(
        val throwable: Throwable
    ) : AppState()
}