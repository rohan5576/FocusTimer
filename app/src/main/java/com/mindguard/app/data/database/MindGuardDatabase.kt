package com.mindguard.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mindguard.app.data.model.AppSessionLog
import com.mindguard.app.data.model.AppUsage
import com.mindguard.app.data.model.BlockingEvent
import com.mindguard.app.data.model.DailyStatistics
import com.mindguard.app.data.model.MotivationalQuote
import com.mindguard.app.data.model.QuoteCategory

@Database(
    entities = [AppUsage::class, MotivationalQuote::class, DailyStatistics::class, AppSessionLog::class, BlockingEvent::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MindGuardDatabase : RoomDatabase() {

    abstract fun appUsageDao(): AppUsageDao
    abstract fun motivationalQuoteDao(): MotivationalQuoteDao
    abstract fun dailyStatisticsDao(): DailyStatisticsDao

    companion object {
        @Volatile
        private var INSTANCE: MindGuardDatabase? = null

        fun getDatabase(context: Context): MindGuardDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, MindGuardDatabase::class.java, "mindguard_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class Converters {
    @androidx.room.TypeConverter
    fun fromQuoteCategory(category: QuoteCategory): String {
        return category.name
    }

    @androidx.room.TypeConverter
    fun toQuoteCategory(category: String): QuoteCategory {
        return QuoteCategory.valueOf(category)
    }
} 