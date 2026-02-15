package com.mach.dFramework.utils

import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import java.util.*


class PlaceholderUtils {
    companion object {
        fun applyPlaceholder(message: String?, placeholder: MutableMap<String?, String?>): String? {
            var message = message ?: return null

            for (entry in placeholder.entries) {
                message = message.replace("%" + entry.key + "%", entry.value!!)
            }
            message = message.replace("\\n", "\n")

            if (message.endsWith("\n")) {
                message += " "
            }

            return message
        }

        fun format(message: String): TextComponent {
            return LegacyComponentSerializer.legacyAmpersand().deserialize(message.replace("\\n", "\n"))
        }

        fun format(message: String, placeholders: MutableMap<String?, String?>): TextComponent {
            return LegacyComponentSerializer.legacyAmpersand().deserialize(message.replace("\\n", "\n"))
        }

        fun format(message: String?, placeholders: MutableMap<String?, String?>): TextComponent? {
            return applyPlaceholder(message, placeholders)?.let {
                LegacyComponentSerializer.legacyAmpersand().deserialize(it)
            }
        }

        fun getPlayerNameFromUUID(uuid: UUID): String? {
            val player = Bukkit.getOfflinePlayer(uuid)
            return if (player.hasPlayedBefore()) {
                player.name
            } else {
                "Invalid player with UUID $uuid"
            }
        }
    }
}