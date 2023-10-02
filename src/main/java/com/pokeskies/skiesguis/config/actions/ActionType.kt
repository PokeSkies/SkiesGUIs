package com.pokeskies.skiesguis.config.actions

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import com.mojang.serialization.Codec
import com.pokeskies.skiesguis.config.actions.types.*

data class ActionType<A : Action>(val id: String, val codec: Codec<A>) {
    companion object {
        private val map: BiMap<String, ActionType<*>> = HashBiMap.create()

        fun <A : Action> create(id: String, codec: Codec<A>): ActionType<A> {
            val type = ActionType(id, codec)
            map[id] = type
            return type
        }

        val CODEC: Codec<ActionType<*>> = Codec.STRING.xmap({ map[it] }, { map.inverse()[it] })
        val COMMAND_CONSOLE: ActionType<CommandConsole> = create("COMMAND_CONSOLE", CommandConsole.CODEC)
        val COMMAND_PLAYER: ActionType<CommandPlayer> = create("COMMAND_PLAYER", CommandPlayer.CODEC)
        val MESSAGE: ActionType<MessagePlayer> = create("MESSAGE", MessagePlayer.CODEC)
        val BROADCAST: ActionType<MessageBroadcast> = create("BROADCAST", MessageBroadcast.CODEC)
        val PLAYSOUND: ActionType<PlaySound> = create("PLAYSOUND", PlaySound.CODEC)
        val OPEN_GUI: ActionType<OpenGUI> = create("OPEN_GUI", OpenGUI.CODEC)
        val CLOSE_GUI: ActionType<CloseGUI> = create("CLOSE_GUI", CloseGUI.CODEC)
        val GIVE_XP: ActionType<GiveXP> = create("GIVE_XP", GiveXP.CODEC)
    }
}