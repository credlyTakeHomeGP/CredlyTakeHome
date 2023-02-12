package com.example.contactsapp

import com.example.contactsapp.di.IoDispatcherScope
import com.example.contactsapp.di.MainDispatcherScope
import com.example.contactsapp.model.AppState
import com.example.contactsapp.model.UiAction
import com.example.contactsapp.usecase.GetContacts
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * [StateManager] manages the app state via accepting UI events
 * and emitting view models for the UI to render.
 */
class StateManager @Inject constructor(
    // rx streams
    private val uiActions: Channel<UiAction>,
    private val appState: MutableStateFlow<AppState>,

    // use-cases
    private val getContacts: GetContacts,

    // coroutine scopes
    @IoDispatcherScope private val ioScope: CoroutineScope,
    @MainDispatcherScope private val mainScope: CoroutineScope,
) {
    init {
        // start getting contacts
        loadContacts()

        // listen for UI events
        mainScope.launch {
            uiActions.consumeEach {
                when (it) {
                    is UiAction.Refresh -> {
                        loadContacts()
                    }
                }
            }
        }
    }

    private fun loadContacts() {
        mainScope.launch {
            appState.emit(AppState.Loading)
            withContext(ioScope.coroutineContext) {
                getContacts.apply()
            }.fold({
                mainScope.launch {
                    appState.emit(AppState.ContactsLoaded(it))
                }
            }) {
                mainScope.launch {
                    appState.emit(AppState.Error(it))
                }
            }
        }
    }
}