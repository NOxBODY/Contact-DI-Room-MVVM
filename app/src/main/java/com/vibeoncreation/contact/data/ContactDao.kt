package com.vibeoncreation.contact.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Upsert
    suspend fun addContact(contact: ContactModel)

    @Delete
    suspend fun deleteContact(contact: ContactModel)

    @Query("SELECT * FROM Contacts ORDER BY firstName")
    fun getContactsAscByFirstName(): Flow<List<ContactModel>>

    @Query("SELECT * FROM Contacts ORDER BY lastName")
    fun getContactsAscByLastName(): Flow<List<ContactModel>>

    @Query("SELECT * FROM Contacts ORDER BY phoneNumber")
    fun getContactsAscByPhoneNumber(): Flow<List<ContactModel>>
}