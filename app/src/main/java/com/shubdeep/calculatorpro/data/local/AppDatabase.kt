package com.shubdeep.calculatorpro.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

class Converters {
    @TypeConverter
    fun fromCalculationType(type: CalculationType): String = type.name

    @TypeConverter
    fun toCalculationType(name: String): CalculationType = try {
        CalculationType.valueOf(name)
    } catch (e: Exception) {
        CalculationType.STANDARD
    }
}

@Database(
    entities = [CalculationEntity::class, CurrencyRateEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun calculationHistoryDao(): CalculationHistoryDao
    abstract fun currencyCacheDao(): CurrencyCacheDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "shubdeep_calculator_pro.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
