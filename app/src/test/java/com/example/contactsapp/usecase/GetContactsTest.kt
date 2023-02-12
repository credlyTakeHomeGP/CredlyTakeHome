package com.example.contactsapp.usecase

import com.example.contactsapp.data.ContactsRepository
import com.example.contactsapp.model.Contact
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class GetContactsTest {
    @Test
    fun testSuccessfulGetContacts() = runBlocking {
        // Pre-conditions
        val dummyResponse = listOf(mockk<Contact>())
        val mockContactsRepository = mockk<ContactsRepository>()
        val subject = GetContacts(mockContactsRepository)
        coEvery { mockContactsRepository.getContacts() }.returns(dummyResponse)

        // Execution
        val result = subject.apply()

        // Post-conditions
        result.fold({
            assertEquals(dummyResponse, it)
        }, {
            fail()
        })
    }

    @Test
    fun testGetContactsError() = runBlocking {
        // Pre-conditions
        val dummyException = Exception()
        val mockContactsRepository = mockk<ContactsRepository>()
        val subject = GetContacts(mockContactsRepository)
        coEvery { mockContactsRepository.getContacts() }.throws(dummyException)

        // Execution
        val result = subject.apply()

        // Post-conditions
        result.fold({
            fail()
        }, {
            assertEquals(dummyException, it)
        })
    }
}
