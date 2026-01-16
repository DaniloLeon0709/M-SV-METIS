package com.vitaalert.mobile.dev.di

import android.content.Context
import androidx.room.Room
import com.vitaalert.data.local.VitaAlertDatabase
import com.vitaalert.data.repository.VitalRepositoryImpl
import com.vitaalert.domain.repository.VitalRepository
import com.vitaalert.domain.service.VitalThresholds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Provides database and repository dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppDataModule {
    /**
     * Provides the Room database instance.
     */
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): VitaAlertDatabase {
        return Room.databaseBuilder(context, VitaAlertDatabase::class.java, "vitaalert.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    /**
     * Provides the vital repository.
     */
    @Provides
    @Singleton
    fun provideVitalRepository(database: VitaAlertDatabase): VitalRepository {
        return VitalRepositoryImpl(database.vitalReadingDao())
    }

    /**
     * Provides vital thresholds service.
     */
    @Provides
    fun provideVitalThresholds(): VitalThresholds = VitalThresholds()
}
