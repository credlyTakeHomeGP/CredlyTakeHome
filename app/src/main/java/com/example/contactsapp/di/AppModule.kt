package com.example.contactsapp.di

import com.example.contactsapp.StateManager
import com.example.contactsapp.data.ContactsRepository
import com.example.contactsapp.data.ContactsService
import com.example.contactsapp.model.AppState
import com.example.contactsapp.model.UiAction
import com.example.contactsapp.ui.ContactsAdapter
import com.example.contactsapp.usecase.GetContacts
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class IoDispatcherScope

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class MainDispatcherScope

/**
 * Hilt Module defining dependencies to inject within
 * the scope of the [SingletonComponent]
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @MainDispatcherScope
    @Provides
    fun providesMainScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.Main)
    }

    @Singleton
    @IoDispatcherScope
    @Provides
    fun providesIoScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }

    @Provides
    fun provideContactsService(): ContactsService {
        return Retrofit.Builder()
            .baseUrl(CONTACTS_SERVICE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ContactsService::class.java)
    }

    @Provides
    fun provideGetContactsUseCase(contactsRepository: ContactsRepository): GetContacts {
        return GetContacts(contactsRepository)
    }

    @Singleton
    @Provides
    fun provideUserActions(): Channel<UiAction> {
        return Channel()
    }

    @Singleton
    @Provides
    fun provideStateManager(
        userActions: Channel<UiAction>,
        appStateFlow: MutableStateFlow<AppState>,
        getContacts: GetContacts,
        @IoDispatcherScope ioScope: CoroutineScope,
        @MainDispatcherScope mainScope: CoroutineScope
    ): StateManager {
        return StateManager(
            uiActions = userActions,
            appState = appStateFlow,
            getContacts = getContacts,
            ioScope = ioScope,
            mainScope = mainScope,
        )
    }

    @Provides
    fun provideContactsAdapter(): ContactsAdapter {
        return ContactsAdapter()
    }

    @Singleton
    @Provides
    fun provideAppStateFlow(): MutableStateFlow<AppState> {
        return MutableStateFlow(AppState.None)
    }

    const val CONTACTS_SERVICE_BASE_URL = "https://jsonplaceholder.typicode.com/"
}
