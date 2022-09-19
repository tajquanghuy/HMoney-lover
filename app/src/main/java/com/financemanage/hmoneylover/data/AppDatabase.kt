package com.financemanage.hmoneylover.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.financemanage.hmoneylover.model.Transaction

@Database(
    entities = [Transaction::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getTransactionDao(): TransactionDao
}