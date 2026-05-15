package com.example.raktaa_vahini.data

import com.google.firebase.firestore.Exclude
import java.time.LocalDate
import java.time.temporal.ChronoUnit

enum class BloodGroup(val displayName: String) {
    A_POSITIVE("A+"),
    A_NEGATIVE("A-"),
    B_POSITIVE("B+"),
    B_NEGATIVE("B-"),
    AB_POSITIVE("AB+"),
    AB_NEGATIVE("AB-"),
    O_POSITIVE("O+"),
    O_NEGATIVE("O-")
}

data class Donor(
    val id: String = "",
    val name: String = "",
    val bloodGroup: BloodGroup = BloodGroup.O_POSITIVE,
    val email: String = "",
    @get:Exclude val password: String = "",
    val area: String = "",
    val district: String = "",
    val pincode: String = "",
    val phoneNumber: String = "",
    val lastDonationDate: Long? = null, // Epoch milli
    val donationHistory: List<Long> = emptyList(),
    val isReadyToDonate: Boolean = true
) {
    val isEligible: Boolean
        get() {
            if (!isReadyToDonate) return false
            if (lastDonationDate == null) return true
            val lastDate = java.time.Instant.ofEpochMilli(lastDonationDate)
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate()
            val today = LocalDate.now()
            return ChronoUnit.DAYS.between(lastDate, today) >= 90
        }
}
