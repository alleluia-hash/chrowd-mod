package com.example.chrowd;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GenerationStep;

/** Common (server + client) entrypoint. */
public class ChrowdMod implements ModInitializer {
    public static final String MOD_ID = "chrowd";

    @Override
    public void onInitialize() {
        ModBlocks.init();
        ModItems.init();

        // Feature B + C: levitation slap and cooldown.
        ChrowdItem.registerAttackHandler();

        // Feature A (server half): receives the client's double jump packet.
        DoubleJumpNetworking.registerServer();

        // Spawn Shard Ore in every overworld biome. The ore itself is defined in
        // data/chrowd/worldgen/configured_feature and placed_feature.
        BiomeModifications.addFeature(
            BiomeSelectors.foundInOverworld(),
            GenerationStep.Feature.UNDERGROUND_ORES,
            RegistryKey.of(RegistryKeys.PLACED_FEATURE, new Identifier(MOD_ID, "shard_ore"))
        );
    }
}
