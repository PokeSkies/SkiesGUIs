package com.pokeskies.skiesguis.config.requirements

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import com.mojang.serialization.Codec
import com.pokeskies.skiesguis.config.requirements.types.ItemRequirement
import com.pokeskies.skiesguis.config.requirements.types.PermissionRequirement

data class RequirementType<A : Requirement>(val id: String, val codec: Codec<A>) {
    companion object {
        private val map: BiMap<String, RequirementType<*>> = HashBiMap.create()

        fun <A : Requirement> create(id: String, codec: Codec<A>): RequirementType<A> {
            val type = RequirementType(id, codec)
            map[id] = type
            return type
        }

        val CODEC: Codec<RequirementType<*>> = Codec.STRING.xmap({ map[it] }, { map.inverse()[it] })
        val PERMISSION: RequirementType<PermissionRequirement> = create("PERMISSION", PermissionRequirement.CODEC)
        val ITEM: RequirementType<ItemRequirement> = create("ITEM", ItemRequirement.CODEC)
    }
}