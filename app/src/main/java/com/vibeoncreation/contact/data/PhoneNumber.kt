package com.vibeoncreation.contact.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "PhoneNumbers",
    foreignKeys = [
        ForeignKey(entity = Contact::class, parentColumns = ["id"], childColumns = ["contactId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class PhoneNumber(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val number: String,
    val contactId: Int
): Serializable
