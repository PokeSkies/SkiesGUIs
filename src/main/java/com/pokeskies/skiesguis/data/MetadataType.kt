package com.pokeskies.skiesguis.data

import com.cobblemon.mod.common.util.isDouble
import com.cobblemon.mod.common.util.isInt


enum class MetadataType(
    val clazz: Class<*>,
    val isValid: (String) -> Boolean = { true },
) {
    STRING(String::class.java),
    INTEGER(Int::class.java, { v -> v.isInt() }),
    DOUBLE(Double::class.java, { v -> v.isDouble() }),
    LONG(Long::class.java, { v -> v.trim().toLongOrNull() != null }),
    BOOLEAN(Boolean::class.java, { v ->
        when (v.trim().lowercase()) {
            "true", "t", "yes", "y", "1" -> true
            "false", "f", "no", "n", "0" -> false
            else -> false
        }
    });

    fun parseString(value: String): Pair<Any?, String> {
        return try {
            when (this) {
                STRING -> value
                INTEGER -> value.toInt()
                DOUBLE -> value.toDouble()
                LONG -> value.toLong()
                BOOLEAN -> value.toBoolean()
            } to ""
        } catch (e: Exception) {
            null to (e.message ?: "Unknown error!")
        }
    }

    companion object {
        fun getFromClass(clazz: Class<*>): MetadataType? =
            entries.firstOrNull { it.clazz == clazz }

        fun getFromName(type: String): MetadataType? =
            entries.firstOrNull { it.name == type }
    }
}