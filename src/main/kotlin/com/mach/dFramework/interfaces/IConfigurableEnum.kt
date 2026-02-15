package com.mach.dFramework.interfaces

import com.mach.dFramework.utils.PlaceholderUtils
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.configuration.file.FileConfiguration

interface IConfigurableEnum {
    fun init(config: FileConfiguration)

    fun getPath(): String;

    fun getConfig(): FileConfiguration

    fun get(): String {
        val cfg: FileConfiguration = getConfig()
        if (cfg.contains(getPath())) {
            return cfg.getString(getPath()) ?: ""
        }
        return "Config not found: ${getPath()}"
    }

    fun get(placeholders: MutableMap<String?, String?>): TextComponent? {
        val cfg: FileConfiguration = getConfig()
        if (cfg.contains(getPath())) {
            val msg = cfg.getString(getPath())
            if (msg != null) {
                return PlaceholderUtils.format(msg, placeholders)
            }
        }
        return Component.text("Config not found: ${getPath()}").color(NamedTextColor.RED)
    }
}
