package com.vitaalert.data.local

import androidx.room.TypeConverter
import com.vitaalert.domain.model.VitalQuality
import com.vitaalert.domain.model.VitalType
import java.time.Instant

/**
 * Type converters for Room persistence.
 */
class RoomConverters {
    @TypeConverter
    fun fromInstant(value: Instant?): String? = value?.toString()

    @TypeConverter
    fun toInstant(value: String?): Instant? = value?.let(Instant::parse)

    @TypeConverter
    fun fromVitalType(value: VitalType?): String? = value?.name

    @TypeConverter
    fun toVitalType(value: String?): VitalType? = value?.let(VitalType::valueOf)

    @TypeConverter
    fun fromVitalQuality(value: VitalQuality?): String? = value?.name

    @TypeConverter
    fun toVitalQuality(value: String?): VitalQuality? = value?.let(VitalQuality::valueOf)
}
