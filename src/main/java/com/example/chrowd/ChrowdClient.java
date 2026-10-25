package com.example.chrowd;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;

/** Client-only entrypoint: detects the mid-air jump key press. */
public class ChrowdClient implements ClientModInitializer {
    /** Upward velocity of the boost (a normal jump is 0.42). */
    private static final double UP_VELOCITY = 0.75;
    /** Extra horizontal speed added in the direction you are looking. */
    private static final double FORWARD_BOOST = 0.4;
    /** Ticks you must be airborne before the second press counts, so the first jump never triggers it. */
    private static final int MIN_AIR_TICKS = 2;

    private boolean wasJumpPressed = false;
    private boolean usedAirJump = false;
    private int airTicks = 0;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
    }

    private void onTick(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null || client.world == null) {
            wasJumpPressed = false;
            usedAirJump = false;
            airTicks = 0;
            return;
        }

        boolean jumpPressed = client.options.jumpKey.isPressed();

        boolean grounded = player.isOnGround() || player.isTouchingWater() || player.isInLava()
            || player.isClimbing() || player.hasVehicle() || player.isFallFlying()
            || player.getAbilities().flying;

        if (grounded) {
            usedAirJump = false;
            airTicks = 0;
        } else {
            airTicks++;
        }

        // A fresh press of the jump key while airborne = the "double tap".
        if (!grounded && jumpPressed && !wasJumpPressed && !usedAirJump
            && airTicks >= MIN_AIR_TICKS && client.currentScreen == null
            && ChrowdItem.isHolding(player)) {
            usedAirJump = true;
            doubleJump(player);
        }

        wasJumpPressed = jumpPressed;
    }

    private void doubleJump(ClientPlayerEntity player) {
        Vec3d look = player.getRotationVector();
        double hx = look.x;
        double hz = look.z;
        double length = Math.sqrt(hx * hx + hz * hz);
        if (length > 1.0E-4) {
            hx /= length;
            hz /= length;
        } else {
            hx = 0.0;
            hz = 0.0;
        }

        Vec3d current = player.getVelocity();
        Vec3d boosted = new Vec3d(
            current.x + hx * FORWARD_BOOST,
            UP_VELOCITY,
            current.z + hz * FORWARD_BOOST);

        // Apply locally right away...
        player.setVelocity(boosted);
        player.fallDistance = 0.0F;

        // ...and tell the server so it matches, cancels fall damage and spawns particles.
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeDouble(boosted.x);
        buf.writeDouble(boosted.y);
        buf.writeDouble(boosted.z);
        ClientPlayNetworking.send(DoubleJumpNetworking.DOUBLE_JUMP_ID, buf);
    }
}
