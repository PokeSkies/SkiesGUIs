package com.pokeskies.skiesguis.config.requirements

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.pokeskies.skiesguis.config.actions.Action
import com.pokeskies.skiesguis.config.actions.SubAction
import com.pokeskies.skiesguis.utils.optionalRecordCodec

class ViewRequirement(
    val requirements: Map<String, Requirement>,
//    val denyActions: Map<String, SubAction>,
//    val successActions: Map<String, SubAction>
) {
    companion object {
        val CODEC: Codec<ViewRequirement> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.unboundedMap(Codec.STRING, Requirement.CODEC).optionalRecordCodec("requirements", ViewRequirement::requirements, emptyMap()),
//                Codec.unboundedMap(Codec.STRING, SubAction.CODEC).optionalRecordCodec("deny_actions", ViewRequirement::denyActions, emptyMap()),
//                Codec.unboundedMap(Codec.STRING, SubAction.CODEC).optionalRecordCodec("success_actions", ViewRequirement::successActions, emptyMap()),
            ).apply(instance) { requirements ->
                ViewRequirement(requirements)
            }
        }
    }

    override fun toString(): String {
        return "ClickRequirement(requirements=$requirements)"
    }
}