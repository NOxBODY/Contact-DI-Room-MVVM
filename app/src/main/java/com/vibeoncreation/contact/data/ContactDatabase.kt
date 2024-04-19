package com.vibeoncreation.contact.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        Contact::class,
        PhoneNumber::class
               ],
    version = 4,
    autoMigrations = [
        AutoMigration(
            from = 2,
            to = 3,
            spec = ContactDatabase.RemoveColumnMigration::class
        )
    ]
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

            }

        }
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE INDEX index_PhoneNumbers_contactId " +
                            "ON PhoneNumbers('contactId')"
                )
            }
        }
    }
    @DeleteColumn(
        tableName = "Contacts",
        columnName = "phoneNumber"
    )
    class RemoveColumnMigration: AutoMigrationSpec
}