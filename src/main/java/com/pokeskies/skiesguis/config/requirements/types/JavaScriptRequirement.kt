package com.pokeskies.skiesguis.config.requirements.types

import com.pokeskies.skiesguis.SkiesGUIs
import com.pokeskies.skiesguis.config.requirements.ComparisonType
import com.pokeskies.skiesguis.config.requirements.Requirement
import com.pokeskies.skiesguis.config.requirements.RequirementType
import com.pokeskies.skiesguis.utils.Utils
import net.minecraft.server.network.ServerPlayerEntity
import org.graalvm.polyglot.Context
import org.graalvm.polyglot.Value
import java.lang.Exception

class JavaScriptRequirement(
    type: RequirementType = RequirementType.PERMISSION,
    comparison: ComparisonType = ComparisonType.EQUALS,
    private val expression: String = ""
) : Requirement(type, comparison) {
    override fun checkRequirements(player: ServerPlayerEntity): Boolean {
        if (!checkComparison())
            return false

        val context = Context.newBuilder()
            .engine(SkiesGUIs.INSTANCE.graalEngine)
            .build()

        try {
            val result: Value = context.eval("js", Utils.parsePlaceholders(player, expression))

            if (!result.isBoolean) {
                Utils.printError("A Javascript Requirement expression '$expression' did not return a boolean!")
                return false
            }

            val bool = result.asBoolean()

            return if (comparison == ComparisonType.EQUALS) bool else !bool
        } catch (e: Exception) {
            Utils.printError("An error occurred while parsing the Javascript Requirement expression '$expression': ${e.printStackTrace()}")
        }

        return false
    }

    override fun allowedComparisons(): List<ComparisonType> {
        return listOf(ComparisonType.EQUALS, ComparisonType.NOT_EQUALS)
    }

    override fun toString(): String {
        return "JavascriptRequirement(expression='$expression')"
    }

}