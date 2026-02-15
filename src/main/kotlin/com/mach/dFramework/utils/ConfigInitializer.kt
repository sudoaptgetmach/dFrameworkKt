package com.mach.dFramework.utils

import com.mach.dFramework.interfaces.IConfigurableEnum
import com.mach.dFramework.manager.ConfigManager

class ConfigInitializer {
    fun init(manager: ConfigManager, configurable: IConfigurableEnum) {
        manager.setup()
        configurable.init(manager.getConfig())
    }
}