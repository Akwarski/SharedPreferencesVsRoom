package com.example.speedtest.data.repository

import com.example.speedtest.data.model.SaveFieldData
import com.example.speedtest.db.SaveFieldDao

class Repository(private val saveFieldDao: SaveFieldDao) {
    fun insert(saveField: SaveFieldData) {
        saveFieldDao.insert(saveField)
    }

    fun delete(){
        saveFieldDao.deleteAll()
    }

    fun getAll() : List<SaveFieldData>{
        return saveFieldDao.getAll()
    }
}