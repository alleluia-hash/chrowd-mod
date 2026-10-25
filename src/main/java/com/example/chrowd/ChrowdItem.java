package com.example.chrowd;

import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResult;

/**
 * The Chrowd. It does nothing on its own: while it is in either hand, the
 * double jump (see ChrowdClient / DoubleJumpNetworking) and the levitation slap
 * below are active.
 */
public class ChrowdItem extends Item {
    /** 15 seconds. */
    public static final int LEVITATION_TICKS = 300;
    /** 0 = Levitation I, 1 = Levitation II (about twice as fast). Raise it to launch victims harder. */
    public static final int LEVITATION_AMPLIFIER = 1;
    /** 1 minute. */
    public static final int COOLDOWN_TICKS = 1200;

    public ChrowdItem(Settings settings) {
        super(settings);
    }

    public static boolean isHolding(PlayerEntity player) {
        return player.getMainHandStack().isOf(ModItems.CHROWD)
            || player.getOffHandStack().isOf(ModItems.CHROWD);
    }

    /** Feature B (anti-gravity slap) and Feature C (cooldown). Runs on the server only. */
    public static void registerAttackHandler() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClient() || player.isSpectator()) {
                return ActionResult.PASS;
            }
            if (!(entity instanceof LivingEntity target) || !target.isAlive()) {
                return ActionResult.PASS;
            }
            if (!isHolding(player)) {
                return ActionResult.PASS;
            }

            var cooldowns = player.getItemCooldownManager();
            if (cooldowns.isCoolingDown(ModItems.CHROWD)) {
                return ActionResult.PASS;
            }

            target.addStatusEffect(new StatusEffectInstance(
                StatusEffects.LEVITATION, LEVITATION_TICKS, LEVITATION_AMPLIFIER));

            // Vanilla cooldown: the server syncs it to the client, which draws the
            // usual overlay on the hotbar slot.
            cooldowns.set(ModItems.CHROWD, COOLDOWN_TICKS);

            // PASS lets the normal attack go through as well.
            return ActionResult.PASS;
        });
    }
}
