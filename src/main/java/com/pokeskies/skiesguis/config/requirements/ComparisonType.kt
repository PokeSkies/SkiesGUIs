package com.pokeskies.skiesguis.config.requirements

import net.minecraft.util.StringIdentifiable

enum class ComparisonType(val identifier: String) : StringIdentifiable {
    EQUALS("=="),
    NOT_EQUALS("!="),
    GREATER_THAN(">"),
    LESS_THAN("<"),
    GREATER_THAN_OR_EQUALS(">="),
    LESS_THAN_OR_EQUALS("<=");

    override fun asString(): String {
        return this.identifier
    }
}