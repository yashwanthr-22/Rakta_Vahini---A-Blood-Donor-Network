package com.example.raktaa_vahini.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.raktaa_vahini.data.DonorRepository

class ViewModelFactory(private val repository: DonorRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DonorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DonorViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
