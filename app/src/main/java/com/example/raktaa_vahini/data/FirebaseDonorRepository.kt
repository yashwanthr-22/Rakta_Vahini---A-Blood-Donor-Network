package com.example.raktaa_vahini.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseDonorRepository : DonorRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val donorsCollection = firestore.collection("donors")

    override fun getEligibleDonors(bloodGroup: BloodGroup): Flow<List<Donor>> = callbackFlow {
        val subscription = donorsCollection
            .whereEqualTo("bloodGroup", bloodGroup.name)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val donors = snapshot?.documents?.mapNotNull { it.toObject<Donor>() } ?: emptyList()
                trySend(donors.filter { it.isEligible })
            }
        awaitClose { subscription.remove() }
    }

    override fun searchDonors(
        bloodGroup: BloodGroup,
        area: String,
        district: String,
        pincode: String
    ): Flow<List<Donor>> = callbackFlow {
        val subscription = donorsCollection
            .whereEqualTo("bloodGroup", bloodGroup.name)
            .whereEqualTo("pincode", pincode)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val donors = snapshot?.documents?.mapNotNull { it.toObject<Donor>() } ?: emptyList()
                val filteredDonors = donors.filter { donor ->
                    val matchArea = donor.area.replace(" ", "").equals(area.replace(" ", ""), ignoreCase = true)
                    val matchDistrict = donor.district.replace(" ", "").equals(district.replace(" ", ""), ignoreCase = true)
                    matchArea && matchDistrict && donor.isEligible
                }
                trySend(filteredDonors)
            }
        awaitClose { subscription.remove() }
    }

    override fun getAllDonors(): Flow<List<Donor>> = callbackFlow {
        val subscription = donorsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val donors = snapshot?.documents?.mapNotNull { it.toObject<Donor>() } ?: emptyList()
            trySend(donors)
        }
        awaitClose { subscription.remove() }
    }

    override suspend fun updateDonor(donor: Donor) {
        donorsCollection.document(donor.id).set(donor).await()
    }

    override suspend fun getDonorProfile(id: String): Donor? {
        return donorsCollection.document(id).get().await().toObject<Donor>()
    }

    override suspend fun login(identifier: String, password: String): Donor? {
        return try {
            val email = if (identifier.contains("@")) {
                identifier
            } else {
                // If it's a phone number, we need to find the email associated with it first
                // as Firebase Auth email login requires the email.
                val snapshot = donorsCollection.whereEqualTo("phoneNumber", identifier).get().await()
                snapshot.documents.firstOrNull()?.getString("email") ?: return null
            }
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: return null
            getDonorProfile(userId)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun register(donor: Donor): Donor {
        val result = auth.createUserWithEmailAndPassword(donor.email, donor.password).await()
        val userId = result.user?.uid ?: throw Exception("Failed to create user")
        val donorWithId = donor.copy(id = userId)
        updateDonor(donorWithId)
        return donorWithId
    }
}
