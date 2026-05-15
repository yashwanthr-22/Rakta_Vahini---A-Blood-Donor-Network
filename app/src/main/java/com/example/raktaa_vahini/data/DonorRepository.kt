package com.example.raktaa_vahini.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

interface DonorRepository {
    fun getEligibleDonors(bloodGroup: BloodGroup): Flow<List<Donor>>
    fun searchDonors(bloodGroup: BloodGroup, area: String, district: String, pincode: String): Flow<List<Donor>>
    suspend fun updateDonor(donor: Donor)
    suspend fun getDonorProfile(id: String): Donor?
    fun getAllDonors(): Flow<List<Donor>>
    suspend fun login(identifier: String, password: String): Donor?
    suspend fun register(donor: Donor): Donor
}

class MockDonorRepository : DonorRepository {
    private val _donors = MutableStateFlow(listOf(
        Donor("1", "John Doe", BloodGroup.A_POSITIVE, "john@example.com", "password", "Area 1", "District 1", "123456", "1234567890"),
        Donor(
            id = "2",
            name = "Jane Smith",
            bloodGroup = BloodGroup.O_NEGATIVE,
            email = "jane@example.com",
            password = "password",
            area = "Area 2",
            district = "District 2",
            pincode = "654321",
            phoneNumber = "9876543210",
            lastDonationDate = System.currentTimeMillis() - (100L * 24 * 60 * 60 * 1000),
            donationHistory = emptyList()
        ),
        Donor(
            id = "3",
            name = "Bob Wilson",
            bloodGroup = BloodGroup.A_POSITIVE,
            email = "bob@example.com",
            password = "password",
            area = "Area 3",
            district = "District 3",
            pincode = "112233",
            phoneNumber = "1122334455",
            lastDonationDate = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000),
            donationHistory = emptyList()
        ),
        Donor("4", "Alice Brown", BloodGroup.B_POSITIVE, "alice@example.com", "password", "Area 4", "District 4", "556677", "5566778899")
    ))

    override fun searchDonors(bloodGroup: BloodGroup, area: String, district: String, pincode: String): Flow<List<Donor>> {
        return _donors.map { list ->
            list.filter { donor ->
                donor.bloodGroup == bloodGroup &&
                        donor.area.replace(" ", "").equals(area.replace(" ", ""), ignoreCase = true) &&
                        donor.district.replace(" ", "").equals(district.replace(" ", ""), ignoreCase = true) &&
                        donor.pincode == pincode &&
                        donor.isEligible
            }
        }
    }

    override fun getEligibleDonors(bloodGroup: BloodGroup): Flow<List<Donor>> {
        return _donors.map { list ->
            list.filter { it.bloodGroup == bloodGroup && it.isEligible }
        }
    }

    override fun getAllDonors(): Flow<List<Donor>> = _donors.asStateFlow()

    override suspend fun updateDonor(donor: Donor) {
        val currentList = _donors.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == donor.id }
        if (index != -1) {
            currentList[index] = donor
        } else {
            val newDonor = donor.copy(id = (currentList.size + 1).toString())
            currentList.add(newDonor)
        }
        _donors.value = currentList
    }

    override suspend fun getDonorProfile(id: String): Donor? {
        return _donors.value.find { it.id == id }
    }

    override suspend fun login(identifier: String, password: String): Donor? {
        return _donors.value.find { (it.email == identifier || it.phoneNumber == identifier) && it.password == password }
    }

    override suspend fun register(donor: Donor): Donor {
        val currentList = _donors.value.toMutableList()
        val newDonor = donor.copy(id = (currentList.size + 1).toString())
        currentList.add(newDonor)
        _donors.value = currentList
        return newDonor
    }
}
