package com.example.speedtest.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.speedtest.data.model.ChartData

@Dao
interface ChartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(chartData: ChartData)

    @Query("DELETE FROM ChartData")
    fun deleteAll()

    @Query("SELECT * FROM ChartData")
    fun getAll() : LiveData<List<ChartData>>
}