package com.mach.dFramework

import com.mach.dFramework.interfaces.IConfigurableEnum
import com.mach.dFramework.manager.ConfigManager
import com.mach.dFramework.manager.DatabaseManager
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin
import kotlin.reflect.KClass

class DFramework : JavaPlugin() {
    override fun onEnable() {

    }

    override fun onDisable() {
        databaseManager?.shutdown()
    }

    companion object {
        private var databaseManager: DatabaseManager? = null

        fun initConfigs(plugin: JavaPlugin, map: MutableMap<ConfigManager?, KClass<out IConfigurableEnum>?>) {
            for (entry in map.entries) {
                val cm: ConfigManager = entry.key!!
                cm.setup()

                val config = cm.getConfig()

                val enumClass: Class<out IConfigurableEnum>? = entry.value?.java

                try {
                    val enumConstants: Array<out IConfigurableEnum>? = enumClass?.enumConstants
                    if (enumConstants.isNullOrEmpty()) {
                        plugin.logger.warning("No enum constants found in " + enumClass?.getName())
                        continue
                    }

                    for (enumConstant in enumConstants) {
                        enumClass.getMethod("init", FileConfiguration::class.java)
                            .invoke(enumConstant, config)
                    }
                } catch (e: Exception) {
                    plugin.logger.severe("Failed to initialize enum " + enumClass?.getName() + ": " + e)
                    e.printStackTrace()
                }
            }

            val configManager = map.keys.stream()
                .filter { cm: ConfigManager? -> "config.yml" == cm!!.getFileName() }
                .findFirst()
                .orElse(null)

            if (configManager != null) {
                databaseManager = DatabaseManager(configManager, plugin)
                databaseManager!!.setup()
            }
        }

        fun getDatabaseManager(): DatabaseManager? {
            return databaseManager
        }
    }
}