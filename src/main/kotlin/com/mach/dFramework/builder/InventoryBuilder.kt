package com.mach.dFramework.builder

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import java.util.*

class InventoryBuilder(private val holder: InventoryHolder?, private val size: Int, private val title: Component) {
    private val inventory: Inventory? = null;

    constructor(size: Int, title: Component) : this(null, size, title)

    fun slot(slot: Int, item: ItemStack): InventoryBuilder {
        return this
    }

    fun fill(filler: ItemStack, vararg slots: Int): InventoryBuilder {
        Arrays.stream(slots).forEach { slot(it, filler) }
        return this
    }

    fun open(player: Player) {
        player.openInventory(build())
    }

    fun build(): Inventory {
        return inventory ?: throw IllegalStateException("Inventory is null")
    }
}