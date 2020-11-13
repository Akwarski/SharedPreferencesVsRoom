package com.example.speedtest.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.speedtest.data.model.SaveFieldData

@Dao
interface SaveFieldDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(saveFieldData: SaveFieldData)

    @Query("DELETE FROM SaveFieldData")
    fun deleteAll()
}