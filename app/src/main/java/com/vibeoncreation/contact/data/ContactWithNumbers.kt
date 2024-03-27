package com.vibeoncreation.contact.data

import java.io.Serializable

data class ContactWithNumbers(
    val contact: Contact,
    val phoneNumbers: List<PhoneNumber>
): Serializable
