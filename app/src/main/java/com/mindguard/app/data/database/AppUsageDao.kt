package com.mindguard.app.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mindguard.app.data.model.AppUsage
import kotlinx.coroutines.flow.Flow

@Dao
interface AppUsageDao {

    @Query("SELECT * FROM app_usage WHERE isEnabled = 1")
    fun getAllEnabledApps(): Flow<List<AppUsage>>

    @Query("SELECT * FROM app_usage WHERE packageName = :packageName")
    suspend fun getAppByPackage(packageName: String): AppUsage?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApp(appUsage: AppUsage)

    @Update
    suspend fun updateApp(appUsage: AppUsage)

    @Delete
    suspend fun deleteApp(appUsage: AppUsage)

    @Query("UPDATE app_usage SET currentUsage = :usage WHERE packageName = :packageName")
    suspend fun updateUsage(packageName: String, usage: Long)

    @Query("UPDATE app_usage SET currentUsage = 0, lastResetDate = :resetDate")
    suspend fun resetAllUsage(resetDate: Long = System.currentTimeMillis())

    @Query("SELECT * FROM app_usage WHERE currentUsage >= dailyTimeLimit")
    fun getAppsAtLimit(): Flow<List<AppUsage>>

    @Query("DELETE FROM app_usage")
    suspend fun clearAllApps()

    @Query("DELETE FROM app_usage WHERE packageName = :packageName")
    suspend fun deleteByPackageName(packageName: String)

    @Query("SELECT * FROM app_usage")
    fun getAllApps(): Flow<List<AppUsage>>
} 