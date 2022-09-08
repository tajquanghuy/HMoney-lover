package com.pigllet.hmoneylover.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pigllet.hmoneylover.model.Transaction

@Database(
    entities = [Transaction::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getTransactionDao(): TransactionDao
}