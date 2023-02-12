package com.example.contactsapp.usecase

import arrow.core.Either
import com.example.contactsapp.data.ContactsRepository
import com.example.contactsapp.model.Contact
import javax.inject.Inject

/**
 * [GetContacts] is a use case (also know as an "Interactor")
 * that encapsulates obtaining contacts and wrapping any success or
 * failure outcomes in an Either monad.
 */
class GetContacts @Inject constructor(
    private val contactsRepository: ContactsRepository
) {
    suspend fun apply(): Either<List<Contact>, Throwable> =
        try {
            Either.Left(contactsRepository.getContacts())
        } catch (exception: Exception) {
            Either.Right(exception)
        }
}