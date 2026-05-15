package com.example.raktaa_vahini.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.raktaa_vahini.data.BloodGroup
import com.example.raktaa_vahini.data.Donor
import com.example.raktaa_vahini.data.DonorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DonorViewModel(private val repository: DonorRepository) : ViewModel() {

    private val _eligibleDonors = MutableStateFlow<List<Donor>>(emptyList())
    val eligibleDonors: StateFlow<List<Donor>> = _eligibleDonors.asStateFlow()

    private val _currentDonor = MutableStateFlow<Donor?>(null)
    val currentDonor: StateFlow<Donor?> = _currentDonor.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    init {
        checkCurrentUser()
        loadAllEligibleDonors()
    }

    private fun loadAllEligibleDonors() {
        viewModelScope.launch {
            repository.getAllDonors().collectLatest { all ->
                _eligibleDonors.value = all.filter { it.isEligible }
            }
        }
    }

    private fun checkCurrentUser() {
        viewModelScope.launch {
            val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
            if (user != null) {
                _currentDonor.value = repository.getDonorProfile(user.uid)
            }
        }
    }

    fun searchDonors(bloodGroup: BloodGroup, area: String, district: String, pincode: String) {
        viewModelScope.launch {
            repository.searchDonors(bloodGroup, area, district, pincode).collectLatest {
                _eligibleDonors.value = it
            }
        }
    }

    fun searchDonors(bloodGroup: BloodGroup) {
        viewModelScope.launch {
            repository.getEligibleDonors(bloodGroup).collectLatest {
                _eligibleDonors.value = it
            }
        }
    }

    fun login(identifier: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val donor = repository.login(identifier, password)
            if (donor != null) {
                _currentDonor.value = donor
                _loginError.value = null
                onSuccess()
            } else {
                _loginError.value = "Invalid email/phone or password"
            }
        }
    }

    fun register(donor: Donor, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val newDonor = repository.register(donor)
            _currentDonor.value = newDonor
            onSuccess()
        }
    }

    fun logout(onSuccess: () -> Unit) {
        _currentDonor.value = null
        onSuccess()
    }

    fun loadProfile(id: String) {
        viewModelScope.launch {
            _currentDonor.value = repository.getDonorProfile(id)
        }
    }

    fun updateProfile(donor: Donor) {
        viewModelScope.launch {
            repository.updateDonor(donor)
            _currentDonor.value = donor
        }
    }

    fun logDonation(donor: Donor) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val updatedHistory = donor.donationHistory + now
            val updatedDonor = donor.copy(
                lastDonationDate = now,
                donationHistory = updatedHistory
            )
            repository.updateDonor(updatedDonor)
            _currentDonor.value = updatedDonor
        }
    }
}
