package com.example.contactsapp.model

/**
 * Actions to be sent to the UI stream
 * [com.example.contactsapp.StateManager] is listening to
 * as a result of user interactions
 */
sealed class UiAction {
    object Refresh : UiAction()
}