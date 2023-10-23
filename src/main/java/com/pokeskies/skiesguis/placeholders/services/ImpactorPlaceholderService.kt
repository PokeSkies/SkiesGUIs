package com.pokeskies.skiesguis.placeholders.services

import com.pokeskies.skiesguis.SkiesGUIs
import com.pokeskies.skiesguis.placeholders.IPlaceholderService
import com.pokeskies.skiesguis.utils.Utils
import net.impactdev.impactor.api.platform.sources.PlatformSource
import net.impactdev.impactor.api.utility.Context
import net.impactdev.impactor.core.text.processors.MiniMessageProcessor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.minecraft.server.network.ServerPlayerEntity

/*
    This class will parse Impactor based placeholders if the mod is present. Out of the box, Impactor will
    process color codes too, which breaks the compatibility with also parsing placeholders with other mods.
    A custom MiniMessageProcessor is created with an empty TagResolver list, that way it will not process
    any color codes and will just pass a string back with only Impactor placeholders processed.
 */
class ImpactorPlaceholderService : IPlaceholderService {
    private val processor = MiniMessageProcessor(
        MiniMessage.builder()
            .tags(TagResolver.builder().build())
            .build()
    )

    init {
        Utils.info("Impactor mod found! Enabling placeholder integration...")
    }

    override fun parsePlaceholders(player: ServerPlayerEntity, text: String): String {
        return SkiesGUIs.INSTANCE.adventure!!.toNative(
            processor.parse(PlatformSource.server(), text, Context().append(ServerPlayerEntity::class.java, player))
        ).string
    }
}