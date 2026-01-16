package com.vitaalert.data.local

import androidx.room.TypeConverter
import java.time.Instant

/**
 * Type converters for Instant.
 */
class InstantConverters {
    @TypeConverter
    fun fromInstant(value: Instant?): String? = value?.toString()

    @TypeConverter
    fun toInstant(value: String?): Instant? = value?.let { Instant.parse(it) }
}
