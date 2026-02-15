package com.mach.dFramework.builder

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

class ItemBuilder(private val item: ItemStack) {
    private val meta = item.itemMeta

    companion object {
        fun of(material: Material, amount: Int = 1): ItemBuilder {
            return ItemBuilder(ItemStack(material, amount))
        }

        fun head(owner: OfflinePlayer): ItemBuilder {
            return of(Material.PLAYER_HEAD).owning(owner)
        }
    }

    fun amount(amount: Int): ItemBuilder {
        item.amount = amount
        return this
    }

    fun name(name: String): ItemBuilder {
        meta?.displayName(Component.text(name))
        return this
    }

    fun lore(lore: List<String>): ItemBuilder {
        meta?.lore(lore.map { Component.text(it) })
        return this
    }

    fun owning(player: OfflinePlayer): ItemBuilder {
        (meta as? SkullMeta)?.owningPlayer = player
        return this
    }

    fun build(): ItemStack {
        item.itemMeta = meta
        return item
    }
}