package com.example.speedtest.data.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.speedtest.data.model.SaveFieldData
import com.example.speedtest.data.repository.Repository
import com.example.speedtest.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewModel(application: Application): AndroidViewModel(application)  {
    private val repository: Repository

    init {
        val saveFieldDao = AppDatabase.getDatabase(application).saveFieldDao()
        repository = Repository(saveFieldDao)
    }

    fun insert(saveField: SaveFieldData) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(saveField)
    }

    fun delete() = viewModelScope.launch(Dispatchers.IO){
        repository.delete()
    }

    fun getAll() = viewModelScope.launch(Dispatchers.IO){
        repository.getAll()
    }
}