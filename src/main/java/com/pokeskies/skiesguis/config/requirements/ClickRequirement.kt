package com.pokeskies.skiesguis.config.requirements

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.utils.optionalRecordCodec

class ClickRequirement(
    val requirements: Map<String, Requirement>,
    val denyActions: Map<String, Action>
) {
    companion object {
        val CODEC: Codec<ClickRequirement> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.unboundedMap(Codec.STRING, Requirement.CODEC).optionalRecordCodec("requirements", ClickRequirement::requirements, emptyMap()),
                Codec.unboundedMap(Codec.STRING, Action.CODEC).optionalRecordCodec("deny_actions", ClickRequirement::denyActions, emptyMap()),
            ).apply(instance) { requirements, denyActions ->
                ClickRequirement(requirements, denyActions)
            }
        }
    }
}