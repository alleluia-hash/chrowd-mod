package com.example.chrowd;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public final class ModItems {
    private ModItems() {}

    public static final Item RAW_SHARD = register("raw_shard", new Item(new Item.Settings()));

    public static final Item CHROWD = register("chrowd",
        new ChrowdItem(new Item.Settings().maxCount(1).rarity(Rarity.RARE)));

    private static Item register(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(ChrowdMod.MOD_ID, name), item);
    }

    public static void init() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> entries.add(RAW_SHARD));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> entries.add(CHROWD));
    }
}
