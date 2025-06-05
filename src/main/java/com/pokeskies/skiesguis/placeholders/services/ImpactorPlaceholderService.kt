package com.pokeskies.skiesguis.placeholders.services

import com.pokeskies.skiesguis.SkiesGUIs
import com.pokeskies.skiesguis.placeholders.IPlaceholderService
import com.pokeskies.skiesguis.utils.Utils
import net.impactdev.impactor.api.platform.players.PlatformPlayer
import net.impactdev.impactor.api.platform.sources.PlatformSource
import net.impactdev.impactor.api.text.TextProcessor
import net.impactdev.impactor.api.utility.Context
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.minecraft.server.level.ServerPlayer

/*
    This class will parse Impactor based placeholders if the mod is present. Out of the box, Impactor will
    process color codes, which breaks the compatibility with parsing placeholders using other mods.
    A custom MiniMessage is created with an empty TagResolver list, that way it will not process
    any color codes and will just pass a string back with only Impactor placeholders processed.
 */
class ImpactorPlaceholderService : IPlaceholderService {
    private val processor = TextProcessor.mini(
        MiniMessage.builder()
            .tags(TagResolver.builder().build())
            .build()
    )

    init {
        Utils.printInfo("Impactor mod found! Enabling placeholder integration...")
    }

    override fun parsePlaceholders(player: ServerPlayer, text: String): String {
        val platformPlayer = PlatformPlayer.getOrCreate(player.uuid)
        return SkiesGUIs.INSTANCE.adventure!!.toNative(
            processor.parse(platformPlayer, text, Context().append(PlatformSource::class.java, platformPlayer))
        ).string
    }
}
