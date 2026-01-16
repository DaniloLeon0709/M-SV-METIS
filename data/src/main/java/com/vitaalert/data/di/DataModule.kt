package com.vitaalert.data.di

import android.content.Context
import androidx.room.Room
import com.vitaalert.data.local.SensorDao
import com.vitaalert.data.local.VitaAlertDatabase
import com.vitaalert.data.repository.RoomSensorRepository
import com.vitaalert.domain.repository.SensorRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for local data sources.
 */
@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    /**
     * Provides the Room database.
     */
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): VitaAlertDatabase = Room.databaseBuilder(
        context,
        VitaAlertDatabase::class.java,
        "vitaalert.db"
    ).build()

    /**
     * Provides the sensor DAO.
     */
    @Provides
    fun provideSensorDao(database: VitaAlertDatabase): SensorDao = database.sensorDao()

    /**
     * Provides the sensor repository backed by Room.
     */
    @Provides
    @Singleton
    fun provideSensorRepository(sensorDao: SensorDao): SensorRepository =
        RoomSensorRepository(sensorDao)
}
