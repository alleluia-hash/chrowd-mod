# Chrowd (Fabric 1.20.1)

Needs Fabric Loader 0.15+ and **Fabric API 0.92.2+1.20.1**.

## Content

- **Shard Ore**: generates underground in the overworld (Y -48 to 48, 6 veins per chunk). Needs an iron pickaxe or better. Always drops Raw Shards: 4 with a 1/3 chance, 3 with a 2/3 chance (a weighted loot pool, weights 1 and 2). No Fortune or Silk Touch effect, so the split stays exact.
- **Raw Shard**: crafting material.
- **Chrowd** (1 ender pearl surrounded by 4 Raw Shards, in a plus shape): works in the main hand or the off-hand.
  - **Double jump**: press jump again while airborne. Boosts you up and forward, spawns portal and end rod particles, and cancels fall damage. One air jump per landing.
  - **Anti-gravity slap**: attacking a living mob or player gives them Levitation II for 15 seconds (300 ticks).
  - **Cooldown**: after a slap the Chrowd is on a 1 minute (1200 tick) cooldown with the vanilla hotbar overlay.

## Tuning

- `ChrowdItem.java`: `LEVITATION_TICKS`, `LEVITATION_AMPLIFIER`, `COOLDOWN_TICKS`.
- `ChrowdClient.java`: `UP_VELOCITY`, `FORWARD_BOOST`.
- `data/chrowd/worldgen/placed_feature/shard_ore.json`: ore rarity and height.

## Build

Java 17 and Gradle 8.5, then `gradle build`. The jar is `build/libs/chrowd-mod-1.0.0.jar`.
