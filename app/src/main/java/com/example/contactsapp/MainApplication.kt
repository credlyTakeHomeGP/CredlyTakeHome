package com.example.contactsapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Entrypoint to the application. In this case, used
 * to bootstrap Hilt and eagerly instantiate [StateManager]
 */
@HiltAndroidApp
class MainApplication : Application() {
    @Inject
    lateinit var stateManager: StateManager
}