package com.vitaalert.data.mapper

import com.vitaalert.data.local.VitalReadingEntity
import com.vitaalert.domain.model.VitalReading

/**
 * Maps a [VitalReadingEntity] to a domain [VitalReading].
 */
fun VitalReadingEntity.toDomain(): VitalReading = VitalReading(
    id = id,
    type = type,
    value = value,
    unit = unit,
    timestamp = timestamp,
    quality = quality,
    sourceDeviceId = sourceDeviceId,
    sessionId = sessionId
)

/**
 * Maps a domain [VitalReading] to a [VitalReadingEntity].
 */
fun VitalReading.toEntity(): VitalReadingEntity = VitalReadingEntity(
    id = id,
    type = type,
    value = value,
    unit = unit,
    timestamp = timestamp,
    quality = quality,
    sourceDeviceId = sourceDeviceId,
    sessionId = sessionId
)
