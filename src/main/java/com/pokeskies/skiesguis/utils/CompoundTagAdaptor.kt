package com.pokeskies.skiesguis.utils

import com.google.gson.*
import net.minecraft.nbt.*
import java.lang.reflect.Type

class CompoundTagAdaptor : JsonSerializer<CompoundTag>, JsonDeserializer<CompoundTag> {
    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): CompoundTag {
        val tag = CompoundTag()
        if (!json.isJsonObject) return tag
        val obj = json.asJsonObject
        for ((key, value) in obj.entrySet()) { // Iterate over every key in the JSON Object
            if (value.isJsonObject) { // If the value is a JSON Object: { }
                val valueObj = value.asJsonObject
                val typeField = valueObj.get("type")
                if (typeField == null) { // Check for a "type" field, if it's not present, we assume it's a nested CompoundTag
                    tag.put(key, deserialize(valueObj, typeOfT, context))
                } else { // If it is present, deserialize the "value" field based on the type specified
                    val valueField = valueObj.get("value")
                    if (valueField != null) {
                        when (typeField.asString) {
                            "byte" -> tag.putByte(key, valueField.asByte)
                            "short" -> tag.putShort(key, valueField.asShort)
                            "int" -> tag.putInt(key, valueField.asInt)
                            "long" -> tag.putLong(key, valueField.asLong)
                            "string" -> tag.putString(key, valueField.asString)
                            else -> tag.putString(key, valueField.toString())
                        }
                    } else {
                        tag.put(key, deserialize(value, typeOfT, context))
                    }
                }
            } else if (value.isJsonPrimitive) { // Handle primitive values like normal JSON does
                val primitive = value.asJsonPrimitive
                when { // Valid default types are either Double, Int, Boolean, or String
                    primitive.isNumber -> {
                        if (primitive.asString.contains('.')) {
                            tag.putDouble(key, primitive.asDouble)
                        } else {
                            tag.putInt(key, primitive.asInt)
                        }
                    }
                    primitive.isBoolean -> tag.putBoolean(key, primitive.asBoolean)
                    primitive.isString -> tag.putString(key, primitive.asString)
                    else -> tag.putString(key, primitive.toString())
                }
            } else if (value.isJsonArray) { // If its an array, we need to determine the type of the list
                val arr = value.asJsonArray
                if (arr.size() == 0) {
                    tag.put(key, ListTag())
                } else {
                    when (determineListType(arr)) {
                        InternalListType.NUMBER -> {
                            val intList = IntArray(arr.size()) { arr[it].asInt }
                            tag.put(key, IntArrayTag(intList))
                        }
                        InternalListType.STRING -> {
                            val strList = arr.map { it.asString }
                            val tagList = ListTag()
                            strList.forEach { tagList.add(StringTag.valueOf(it)) }
                            tag.put(key, tagList)
                        }
                        InternalListType.BOOLEAN -> {
                            val boolList = BooleanArray(arr.size()) { arr[it].asBoolean }
                            val tagList = ListTag()
                            boolList.forEach { tagList.add(ByteTag.valueOf(it)) } // I hope this is right
                            tag.put(key, tagList)
                        }
                        InternalListType.OBJECT -> {
                            val tagList = ListTag()
                            arr.forEach { tagList.add(deserialize(it, typeOfT, context)) }
                            tag.put(key, tagList)
                        }
                        InternalListType.ARRAY -> {
                            val tagList = ListTag()
                            arr.forEach { tagList.add(deserialize(it, typeOfT, context)) }
                            tag.put(key, tagList)
                        }
                        else -> {
                            // Default to an empty ListTag if the type is unknown
                            tag.put(key, ListTag())
                        }
                    }
                }
            }
        }
        return tag
    }

    override fun serialize(src: CompoundTag, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val obj = JsonObject()
        for (key in src.allKeys) {
            val element = src[key] ?: continue
            when (element) {
                is ByteTag -> obj.add(key, JsonObject().apply {
                    addProperty("type", "byte")
                    addProperty("value", src.getByte(key))
                })
                is ShortTag -> obj.add(key, JsonObject().apply {
                    addProperty("type", "short")
                    addProperty("value", src.getShort(key))
                })
                is IntTag -> {
                    obj.addProperty(key, src.getInt(key))
                }
                is LongTag -> obj.add(key, JsonObject().apply {
                    addProperty("type", "long")
                    addProperty("value", src.getLong(key))
                })
                is StringTag -> {
                    obj.addProperty(key, src.getString(key))
                }
                is CompoundTag -> {
                    obj.add(key, serialize(src.getCompound(key), typeOfSrc, context))
                }
                is IntArrayTag -> {
                    val arr = JsonArray()
                    element.asIntArray.forEach { arr.add(it) }
                    obj.add(key, arr)
                }
                is ListTag -> {
                    val arr = JsonArray()
                    for (i in 0 until element.size) {
                        when (val item = element[i]) {
                            is StringTag -> arr.add(item.asString)
                            is IntTag -> arr.add(item.asInt)
                            is FloatTag -> arr.add(item.asFloat)
                            is DoubleTag -> arr.add(item.asDouble)
                            is ShortTag -> arr.add(item.asShort)
                            is ByteTag -> arr.add(item.asByte)
                            is LongTag -> arr.add(item.asLong)
                            is CompoundTag -> arr.add(serialize(item, typeOfSrc, context))
                            else -> arr.add(item.toString())
                        }
                    }
                    obj.add(key, arr)
                }
            }
        }
        return obj
    }

    // Attempts to determine the type of a list based on its contents. If all elements are of the same type, it returns that type.
    // If any element is of a different type, it defaults to all being treated as a list of strings.
    private fun determineListType(arr: JsonArray): InternalListType {
        val types = arr.map {
            when {
                it.isJsonPrimitive && it.asJsonPrimitive.isNumber -> InternalListType.NUMBER
                it.isJsonPrimitive && it.asJsonPrimitive.isString -> InternalListType.STRING
                it.isJsonPrimitive && it.asJsonPrimitive.isBoolean -> InternalListType.BOOLEAN
                it.isJsonObject -> InternalListType.OBJECT
                it.isJsonArray -> InternalListType.ARRAY
                else -> InternalListType.UNKNOWN
            }
        }.toSet()
        return if (types.size == 1) types.first() else InternalListType.STRING
    }

    enum class InternalListType {
        NUMBER,
        STRING,
        BOOLEAN,
        OBJECT,
        ARRAY,
        UNKNOWN;
    }
}
