package com.example.speedtest.data.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.speedtest.data.model.ChartData
import com.example.speedtest.data.repository.RepositoryChart
import com.example.speedtest.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChartViewModel(application: Application): AndroidViewModel(application) {
    private val repositoryChart: RepositoryChart
    var listObservable: LiveData<List<ChartData>>

    init {
        val addTreatmentDao = AppDatabase.getDatabase(application).chartDao()
        repositoryChart = RepositoryChart(addTreatmentDao)
        listObservable = repositoryChart.getChartData()
    }

    fun addChartData(
        chartData: ChartData
    ) = viewModelScope.launch(Dispatchers.IO) {
        repositoryChart.insert(chartData)
    }
}