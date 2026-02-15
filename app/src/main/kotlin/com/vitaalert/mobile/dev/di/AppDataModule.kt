package com.vitaalert.mobile.dev.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.vitaalert.data.local.UserProfileDao
import com.vitaalert.data.local.VitaAlertDatabase
import com.vitaalert.data.repository.FirebaseProfileRepository
import com.vitaalert.data.repository.FirebaseVitalRepository
import com.vitaalert.data.repository.UserProfileRepositoryImpl
import com.vitaalert.data.repository.VitalRepositoryImpl
import com.vitaalert.domain.repository.UserProfileRepository
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

    /**
     * Provides the user profile DAO.
     */
    @Provides
    fun provideUserProfileDao(database: VitaAlertDatabase): UserProfileDao {
        return database.userProfileDao()
    }

    /**
     * Provides the user profile repository.
     */
    @Provides
    @Singleton
    fun provideUserProfileRepository(userProfileDao: UserProfileDao): UserProfileRepository {
        return UserProfileRepositoryImpl(userProfileDao)
    }

    /**
     * Provides the user profile repository implementation.
     */
    @Provides
    @Singleton
    fun provideUserProfileRepositoryImpl(userProfileDao: UserProfileDao): UserProfileRepositoryImpl {
        return UserProfileRepositoryImpl(userProfileDao)
    }

    /**
     * Provides Firebase profile repository for cloud sync.
     */
    @Provides
    @Singleton
    fun provideFirebaseProfileRepository(
        firestore: FirebaseFirestore,
        storage: FirebaseStorage,
        auth: FirebaseAuth
    ): FirebaseProfileRepository {
        return FirebaseProfileRepository(firestore, storage, auth)
    }

    /**
     * Provides Firebase vital repository for cloud sync.
     */
    @Provides
    @Singleton
    fun provideFirebaseVitalRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
        vitalThresholds: VitalThresholds
    ): FirebaseVitalRepository {
        return FirebaseVitalRepository(firestore, auth, vitalThresholds)
    }
}
