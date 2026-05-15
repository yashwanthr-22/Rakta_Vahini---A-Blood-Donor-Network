package com.example.raktaa_vahini.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface DonorRepository {
    fun getEligibleDonors(bloodGroup: BloodGroup): Flow<List<Donor>>
    suspend fun updateDonor(donor: Donor)
    suspend fun getDonorProfile(id: String): Donor?
}

class MockDonorRepository : DonorRepository {
    private val donors = mutableListOf(
        Donor("1", "John Doe", BloodGroup.A_POSITIVE, null, 12.9716, 77.5946, "1234567890"),
        Donor("2", "Jane Smith", BloodGroup.O_NEGATIVE, System.currentTimeMillis() - (100L * 24 * 60 * 60 * 1000), 12.9716, 77.5946, "9876543210"), // Eligible (>90 days)
        Donor("3", "Bob Wilson", BloodGroup.A_POSITIVE, System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000), 12.9716, 77.5946, "1122334455"), // Ineligible (<90 days)
        Donor("4", "Alice Brown", BloodGroup.B_POSITIVE, null, 12.9716, 77.5946, "5566778899")
    )

    override fun getEligibleDonors(bloodGroup: BloodGroup): Flow<List<Donor>> {
        return flowOf(donors.filter { it.bloodGroup == bloodGroup && it.isEligible })
    }

    override suspend fun updateDonor(donor: Donor) {
        val index = donors.indexOfFirst { it.id == donor.id }
        if (index != -1) donors[index] = donor else donors.add(donor)
    }

    override suspend fun getDonorProfile(id: String): Donor? {
        return donors.find { it.id == id }
    }
}
