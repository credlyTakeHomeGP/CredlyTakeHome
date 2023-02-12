package com.example.contactsapp.data

import com.example.contactsapp.model.Contact
import retrofit2.http.GET

/**
 * [ContactsService] defines the contract for getting contacts.
 * Applicable to any implementation for the source of truth for contacts.
 */
interface ContactsService {
    @GET("users")
    suspend fun getContacts(): List<Contact>
}