package com.vibeoncreation.contact.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Insert(entity = Contact::class)
    suspend fun addContact(contact: Contact)

    @Insert(entity = PhoneNumber::class)
    suspend fun addNumber(phoneNumber: PhoneNumber)

    @Update(entity = Contact::class)
    suspend fun updateContact(contact: Contact)

    @Update(entity = PhoneNumber::class)
    suspend fun updateNumber(phoneNumber: PhoneNumber)

    @Query("DELETE FROM Contacts WHERE id = :id")
    suspend fun deleteContact(id: Int)

    @Query("SELECT id FROM Contacts ORDER BY id DESC LIMIT 1")
    suspend fun getLastContactId(): Int

    @Query("DELETE FROM PhoneNumbers WHERE id = :id")
    suspend fun deleteNumber(id: Int)

    @Query("SELECT firstName, lastName, number, Contacts.id as contactId, PhoneNumbers.id AS phoneId FROM Contacts " +
            "LEFT JOIN PhoneNumbers ON Contacts.id = PhoneNumbers.contactId " +
            "ORDER BY firstName, lastName")
    fun getContactsAscByFirstName(): Flow<List<ContactNumber>>

    @Query("SELECT firstName, lastName, number, Contacts.id as contactId, PhoneNumbers.id AS phoneId FROM Contacts " +
            "LEFT JOIN PhoneNumbers ON Contacts.id = PhoneNumbers.contactId " +
            "ORDER BY lastName, firstName")
    fun getContactsAscByLastName(): Flow<List<ContactNumber>>


}