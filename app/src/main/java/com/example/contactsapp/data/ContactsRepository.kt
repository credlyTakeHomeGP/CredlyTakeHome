package com.example.contactsapp.data

import com.example.contactsapp.model.Contact
import javax.inject.Inject

/**
 * [ContactsRepository] coordinates data from data sources(s) for getting contacts.
 * This class can potentially pull from a cache for said data source(s) as well.
 */
class ContactsRepository @Inject constructor(
    private val contactsService: ContactsService
) : ContactsService {
    override suspend fun getContacts(): List<Contact> {
        return contactsService.getContacts()
    }
}