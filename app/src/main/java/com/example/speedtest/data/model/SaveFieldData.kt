package com.example.speedtest.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SaveFieldData(
    @PrimaryKey val id: Int,
    val number: Int
)