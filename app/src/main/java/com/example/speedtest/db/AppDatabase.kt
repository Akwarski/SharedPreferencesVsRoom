package com.example.speedtest.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.speedtest.data.model.SaveFieldData

@Database(entities = [SaveFieldData::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun saveFieldDao(): SaveFieldDao

    companion object {
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "speedtest.app"
                ).addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        Log.w(
                            "AppDatabase",
                            "onCreate() ${db.isDatabaseIntegrityOk} ${db.version} ${db.path}"
                        )
                        super.onCreate(db)
                    }

                    override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                        Log.w(
                            "AppDatabase",
                            "onDestructiveMigration() ${db.isDatabaseIntegrityOk} ${db.version} ${db.path}"
                        )
                        super.onDestructiveMigration(db)
                    }

                    override fun onOpen(db: SupportSQLiteDatabase) {
                        Log.w(
                            "AppDatabase",
                            "onOpen() ${db.isDatabaseIntegrityOk} ${db.version} ${db.path}"
                        )
                        super.onOpen(db)
                    }
                })
                    .fallbackToDestructiveMigration()
                    .setJournalMode(JournalMode.TRUNCATE)
                    .build()
                INSTANCE = instance
                return instance
            }

        }
    }
}