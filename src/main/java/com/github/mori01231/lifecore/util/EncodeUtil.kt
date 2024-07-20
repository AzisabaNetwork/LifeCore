package com.github.mori01231.lifecore.util

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.Base64
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

object EncodeUtil {
    fun encodeBase64AndGzip(data: ByteArray): ByteArray = Base64.getEncoder().encode(gzipByteArray(data))

    fun decodeBase64AndGunzip(data: ByteArray): ByteArray = gunzipByteArray(Base64.getDecoder().decode(data))

    fun gzipByteArray(data: ByteArray): ByteArray =
        ByteArrayOutputStream().use {
            it.apply { GZIPOutputStream(this).use { gz -> gz.write(data) } }.toByteArray()
        }

    fun gunzipByteArray(data: ByteArray): ByteArray =
        GZIPInputStream(ByteArrayInputStream(data)).use { it.readBytes() }
}
