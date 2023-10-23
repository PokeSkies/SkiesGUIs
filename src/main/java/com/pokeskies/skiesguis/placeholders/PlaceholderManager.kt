package com.pokeskies.skiesguis.placeholders

import com.pokeskies.skiesguis.placeholders.services.DefaultPlaceholderService
import com.pokeskies.skiesguis.placeholders.services.ImpactorPlaceholderService
import com.pokeskies.skiesguis.placeholders.services.PlaceholderAPIService
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

class PlaceholderManager {
    private val services: MutableList<IPlaceholderService> = mutableListOf()

    init {
        services.add(DefaultPlaceholderService())
        for (service in PlaceholderMod.values()) {
            if (service.isPresent()) {
                services.add(getServiceForType(service))
            }
        }
    }

    fun parse(player: ServerPlayerEntity, text: String): String {
        var returnValue = text
        for (service in services) {
            returnValue = service.parsePlaceholders(player, returnValue)
        }
        return returnValue
    }

    private fun getServiceForType(placeholderMod: PlaceholderMod): IPlaceholderService {
        return when (placeholderMod) {
            PlaceholderMod.IMPACTOR -> ImpactorPlaceholderService()
            PlaceholderMod.PLACEHOLDERAPI -> PlaceholderAPIService()
        }
    }
}