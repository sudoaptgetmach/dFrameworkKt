package com.mach.dFramework.manager

import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException
import java.io.InputStreamReader

class ConfigManager(private val plugin: JavaPlugin, private val fileName: String) {

    private var file: File = File(plugin.dataFolder, fileName)
    private var config: FileConfiguration? = null

    fun setup() {
        file = File(plugin.dataFolder, fileName)
        file?.exists()?.let {
            if (!it) {
                plugin.saveResource(fileName, false)
            }
        }
        config = YamlConfiguration.loadConfiguration(file)

        val defaultStream = plugin.getResource(fileName)
        if (defaultStream != null) {
            val defaultConfig: YamlConfiguration = YamlConfiguration.loadConfiguration(InputStreamReader(defaultStream))
            config!!.setDefaults(defaultConfig)
        }
    }

    fun getConfig(): FileConfiguration {
        return config!!
    }

    fun getFileName(): String {
        return fileName
    }

    fun saveConfig() {
        try {
            config?.save(file)
        } catch (e: IOException) {
            Bukkit.getConsoleSender().sendMessage("§cError when trying to save " + fileName + ": " + e.message)
        }
    }

    fun reloadConfig() {
        config = YamlConfiguration.loadConfiguration(file)
    }
}