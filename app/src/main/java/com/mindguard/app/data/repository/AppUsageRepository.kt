package com.mindguard.app.data.repository

import com.mindguard.app.data.database.AppUsageDao
import com.mindguard.app.data.model.AppUsage
import kotlinx.coroutines.flow.Flow

class AppUsageRepository(
    private val appUsageDao: AppUsageDao
) {

    fun getAllEnabledApps(): Flow<List<AppUsage>> = appUsageDao.getAllEnabledApps()

    suspend fun getAppByPackage(packageName: String): AppUsage? =
        appUsageDao.getAppByPackage(packageName)

    suspend fun insertApp(appUsage: AppUsage) = appUsageDao.insertApp(appUsage)

    suspend fun updateApp(appUsage: AppUsage) = appUsageDao.updateApp(appUsage)

    suspend fun deleteApp(appUsage: AppUsage) = appUsageDao.deleteApp(appUsage)

    suspend fun updateUsage(packageName: String, usage: Long) =
        appUsageDao.updateUsage(packageName, usage)

    suspend fun resetAllUsage() = appUsageDao.resetAllUsage()

    fun getAppsAtLimit(): Flow<List<AppUsage>> = appUsageDao.getAppsAtLimit()

    suspend fun clearAllApps() = appUsageDao.clearAllApps()

    fun getAllApps(): Flow<List<AppUsage>> = appUsageDao.getAllApps()

    suspend fun clearAppData(packageName: String) = appUsageDao.deleteByPackageName(packageName)

    // Predefined social media apps
    suspend fun initializeDefaultApps() {
        val defaultApps = listOf(
            AppUsage(
                packageName = "com.instagram.android", appName = "Instagram", dailyTimeLimit = 30
            ), AppUsage(
                packageName = "com.zhiliaoapp.musically", appName = "TikTok", dailyTimeLimit = 30
            ), AppUsage(
                packageName = "com.facebook.katana", appName = "Facebook", dailyTimeLimit = 20
            ), AppUsage(
                packageName = "com.twitter.android", appName = "Twitter", dailyTimeLimit = 20
            ), AppUsage(
                packageName = "com.snapchat.android", appName = "Snapchat", dailyTimeLimit = 15
            )
        )

        defaultApps.forEach { appUsageDao.insertApp(it) }
    }
} 