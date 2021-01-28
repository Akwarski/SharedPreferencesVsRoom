package com.example.speedtest.data.repository

import androidx.lifecycle.LiveData
import com.example.speedtest.data.model.ChartData
import com.example.speedtest.db.ChartDao

class RepositoryChart(private val chartDao: ChartDao) {
    fun insert(chartData: ChartData) {
        chartDao.insert(chartData)
    }

    fun getChartData(): LiveData<List<ChartData>> {
        return chartDao.getAll()
    }
}