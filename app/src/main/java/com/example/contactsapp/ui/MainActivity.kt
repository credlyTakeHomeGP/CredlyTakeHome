package com.example.contactsapp.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.contactsapp.R
import com.example.contactsapp.model.AppState
import com.example.contactsapp.model.UiAction
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * [MainActivity] is the primary view for this application.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var appState: MutableStateFlow<AppState>

    @Inject
    lateinit var userActions: Channel<UiAction>

    @Inject
    lateinit var contactsAdapter: ContactsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUi()
        observeAppState()
    }

    override fun onDestroy() {
        super.onDestroy()
        MainScope().cancel()
    }

    private fun observeAppState() {
        appState.onEach {
            resetUi()
            when (it) {
                is AppState.None -> {
                    // No op
                }
                is AppState.Error -> {
                    showErrorState(it)
                }
                is AppState.Loading -> {
                    showLoadingState()
                }
                is AppState.ContactsLoaded -> {
                    showContactsState(it)
                }
            }
        }.launchIn(MainScope())
    }

    private fun setupUi() {
        setContentView(R.layout.activity_main)
        findViewById<RecyclerView>(R.id.contacts_recycler_view).adapter = contactsAdapter
        val swipeRefreshLayout =
            findViewById<SwipeRefreshLayout>(R.id.contacts_recycler_view_container)
        swipeRefreshLayout.setOnRefreshListener {
            refresh()
            swipeRefreshLayout.isRefreshing = false
        }
        findViewById<TextView>(R.id.error_dialog_retry).setOnClickListener {
            refresh()
        }
    }

    private fun resetUi() {
        findViewById<View>(R.id.loader).visibility = View.GONE
        findViewById<View>(R.id.error_dialog).visibility = View.GONE
        findViewById<View>(R.id.contacts_recycler_view_container).visibility = View.GONE
    }

    private fun showErrorState(errorState: AppState.Error) {
        findViewById<View>(R.id.error_dialog).visibility = View.VISIBLE
        findViewById<TextView>(R.id.error_dialog_text_view).text =
            errorState.throwable.localizedMessage
    }

    private fun showLoadingState() {
        findViewById<View>(R.id.loader).visibility = View.VISIBLE
    }

    private fun showContactsState(contactsState: AppState.ContactsLoaded) {
        findViewById<View>(R.id.contacts_recycler_view_container).visibility =
            View.VISIBLE
        contactsAdapter.updateContacts(contactsState.contacts)
    }

    private fun refresh() {
        MainScope().launch {
            userActions.send(UiAction.Refresh)
        }
    }
}