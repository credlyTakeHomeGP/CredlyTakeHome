package com.example.contactsapp

import app.cash.turbine.test
import arrow.core.Either
import com.example.contactsapp.model.AppState
import com.example.contactsapp.model.Contact
import com.example.contactsapp.model.UiAction
import com.example.contactsapp.usecase.GetContacts
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Assert.assertEquals
import org.junit.Test

@kotlinx.coroutines.ExperimentalCoroutinesApi
class StateManagerTest {

    private val testScope = TestScope(UnconfinedTestDispatcher())

    @Test
    fun testSuccessfulStateManagerStartup() = runBlocking {
        // Pre-conditions
        val userActions = Channel<UiAction>()
        val appStateFlow = MutableStateFlow<AppState>(AppState.None)
        val mockGetContacts = mockk<GetContacts>()
        val dummyResponse = listOf(Contact("", "", ""))
        coEvery { mockGetContacts.apply() }.returns(Either.Left(dummyResponse))

        appStateFlow.test {
            // Execution
            StateManager(
                uiActions = userActions,
                appState = appStateFlow,
                getContacts = mockGetContacts,
                ioScope = testScope,
                mainScope = testScope
            )

            // Post-conditions
            assertEquals(AppState.None, awaitItem())
            assertEquals(AppState.Loading, awaitItem())
            assertEquals(AppState.ContactsLoaded(dummyResponse), awaitItem())
        }
    }

    @Test
    fun testErrorOnStateManagerStartup() = runBlocking {
        // Pre-conditions
        val userActions = Channel<UiAction>()
        val appStateFlow = MutableStateFlow<AppState>(AppState.None)
        val mockGetContacts = mockk<GetContacts>()
        val dummyError = Throwable("dummy error")
        coEvery { mockGetContacts.apply() }.returns(Either.Right(dummyError))

        appStateFlow.test {
            // Execution
            StateManager(
                uiActions = userActions,
                appState = appStateFlow,
                getContacts = mockGetContacts,
                ioScope = testScope,
                mainScope = testScope
            )

            // Post-conditions
            assertEquals(AppState.None, awaitItem())
            assertEquals(AppState.Loading, awaitItem())
            assertEquals(AppState.Error(dummyError), awaitItem())
        }
    }

    @Test
    fun testLoadContactsOnRefresh() = runBlocking {
        // Pre-conditions
        val userActions = Channel<UiAction>()
        val appStateFlow = MutableStateFlow<AppState>(AppState.None)
        val mockGetContacts = mockk<GetContacts>()
        val dummyResponse = listOf(Contact("", "", ""))
        coEvery { mockGetContacts.apply() }.returns(Either.Left(dummyResponse))

        appStateFlow.test {
            // Execution
            StateManager(
                uiActions = userActions,
                appState = appStateFlow,
                getContacts = mockGetContacts,
                ioScope = testScope,
                mainScope = testScope
            )
            userActions.send(UiAction.Refresh)

            // Post-conditions
            assertEquals(AppState.None, awaitItem())
            assertEquals(AppState.Loading, awaitItem())
            assertEquals(AppState.ContactsLoaded(dummyResponse), awaitItem())
            assertEquals(AppState.Loading, awaitItem())
            assertEquals(AppState.ContactsLoaded(dummyResponse), awaitItem())
        }
    }
}