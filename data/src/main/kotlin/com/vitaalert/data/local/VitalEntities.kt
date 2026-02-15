package com.vitaalert.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vitaalert.domain.model.VitalQuality
import com.vitaalert.domain.model.VitalType
import java.time.Instant

/**
 * Room entity for storing vital readings.
 */
@Entity(tableName = "vital_readings")
data class VitalReadingEntity(
    @PrimaryKey val id: String,
    val type: VitalType,
    val value: Double,
    val unit: String,
    val timestamp: Instant,
    val quality: VitalQuality,
    val sourceDeviceId: String,
    val sessionId: String?
)

/**
 * Room entity representing a known device.
 */
@Entity(tableName = "devices")
data class DeviceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val model: String,
    val lastSeen: Instant
)

/**
 * Stub entity for pending uploads.
 */
@Entity(tableName = "pending_uploads")
data class PendingUploadEntity(
    @PrimaryKey val id: String,
    val createdAt: Instant,
    val sent: Boolean = false
)

/**
 * Stub entity for device profiles.
 */
@Entity(tableName = "device_profiles")
data class DeviceProfileEntity(
    @PrimaryKey val id: String,
    val deviceId: String,
    val createdAt: Instant
)

/**
 * Entity representing the user profile.
 */
@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: String,
    val email: String,
    val displayName: String,
    val photoUri: String?,
    val phoneNumber: String?,
    val createdAt: Long,
    val updatedAt: Long
)

