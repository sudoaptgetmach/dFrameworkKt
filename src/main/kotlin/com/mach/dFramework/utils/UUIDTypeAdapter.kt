package com.mach.dFramework.utils

import java.nio.ByteBuffer
import java.util.*

class UUIDTypeAdapter {
    fun toBytes(uuid: UUID): ByteArray {
        val buffer: ByteBuffer = ByteBuffer.allocate(16)
        buffer.putLong(uuid.mostSignificantBits)
        buffer.putLong(uuid.leastSignificantBits)
        return buffer.array()
    }

    fun fromBytes(bytes: ByteArray?): UUID {
        val buffer: ByteBuffer = ByteBuffer.wrap(bytes)
        val high: Long = buffer.getLong()
        val low: Long = buffer.getLong()
        return UUID(high, low)
    }
}