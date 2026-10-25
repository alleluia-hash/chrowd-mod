package com.example.chrowd;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModBlocks {
    private ModBlocks() {}

    // Same hardness and tool requirement as iron ore. Its drops come from
    // data/chrowd/loot_tables/blocks/shard_ore.json.
    public static final Block SHARD_ORE = register("shard_ore",
        new Block(AbstractBlock.Settings.copy(Blocks.IRON_ORE)));

    private static Block register(String name, Block block) {
        Identifier id = new Identifier(ChrowdMod.MOD_ID, name);
        Registry.register(Registries.BLOCK, id, block);
        Registry.register(Registries.ITEM, id, new BlockItem(block, new Item.Settings()));
        return block;
    }

    public static void init() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(entries -> entries.add(SHARD_ORE));
    }
}
