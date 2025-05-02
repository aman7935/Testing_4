package com.example.testing4.factory

import androidx.lifecycle.ViewModelProvider
import com.example.testing4.repo.Repo
import com.example.testing4.viewmodels.ViewModel



class Factory(private val repo: Repo): ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewModel::class.java)){
            return ViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
