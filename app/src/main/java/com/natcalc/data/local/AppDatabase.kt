package com.natcalc.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.natcalc.data.local.dao.MessageDao
import com.natcalc.data.local.entity.MessageEntity

@Database(entities = [MessageEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
}
