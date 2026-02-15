package com.mach.dFramework.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.configuration.file.FileConfiguration

class ConfigUtils {
    companion object {
        fun getString(config: FileConfiguration?, path: String): TextComponent {
            if (config != null && config.contains(path)) {
                val message = config.getString(path)
                if (message != null) {
                    return PlaceholderUtils.format(message)
                }
            }
            return Component.text("Config not found: $path").color(NamedTextColor.RED)
        }

        fun getString(
            config: FileConfiguration?,
            path: String,
            placeholders: MutableMap<String?, String?>?
        ): TextComponent? {
            if (config != null && config.contains(path)) {
                val message = config.getString(path)
                if (message != null) {
                    return PlaceholderUtils.format(message, placeholders!!)
                }
            }
            return Component.text("Config not found: $path").color(NamedTextColor.RED)
        }
    }
}