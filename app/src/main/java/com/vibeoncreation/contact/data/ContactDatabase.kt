package com.vibeoncreation.contact.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        Contact::class,
        PhoneNumber::class
               ],
    version = 2
)
abstract class ContactDatabase: RoomDatabase() {
    abstract val dao: ContactDao
    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                //db.execSQL("PRAGMA foreign_keys = ON")
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS 'PhoneNumbers' " +
                            "(" +
                            " 'id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                            " 'number' TEXT NOT NULL," +
                            " 'contactId' INTEGER NOT NULL," +
                            " FOREIGN KEY (contactId) REFERENCES Contacts(id) ON DELETE CASCADE" +
                            ")"
                )
                db.execSQL("INSERT INTO PhoneNumbers (number, contactId) " +
                        "SELECT phoneNumber, id FROM Contacts")
                db.execSQL(
                    "ALTER TABLE Contacts DROP COLUMN phoneNumber"
                )
            }

        }
    }
}