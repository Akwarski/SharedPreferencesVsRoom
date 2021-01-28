package com.example.speedtest.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ChartData (
    @PrimaryKey val name: String,
    val timeCheckSP: Float,
    val timeCheckRoom: Float,
    val timeGetSP: Float,
    val timeGetRoom: Float,
    val timeDeleteSP: Float,
    val timeDeleteRoom: Float
)