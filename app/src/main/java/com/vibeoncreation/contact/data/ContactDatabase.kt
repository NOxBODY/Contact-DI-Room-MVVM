package com.vibeoncreation.contact.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ContactModel::class],
    version = 1
)
abstract class ContactDatabase: RoomDatabase() {
    abstract val dao: ContactDao
}