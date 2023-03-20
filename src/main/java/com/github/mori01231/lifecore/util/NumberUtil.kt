package com.github.mori01231.lifecore.util

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.pow

object NumberUtil {
    @JvmStatic
    fun toFriendlyString(number: Long): String {
        val suffixes = arrayOf("", "", "万", "億", "兆", "京")
        val suffixNum = ceil(("" + number).length / 4.0)
        val shortValue = floor(number / 10000.0.pow(suffixNum - 1) * 100) / 100
        val suffix = suffixes[suffixNum.toInt()]
        return "$shortValue$suffix"
    }
}
