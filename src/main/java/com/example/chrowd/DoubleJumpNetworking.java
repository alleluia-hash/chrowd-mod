package com.example.chrowd;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

/**
 * Server side of the double jump. The client applies the boost immediately (so it
 * feels instant) and sends the resulting velocity here. The server checks that the
 * request is legitimate, applies the same velocity, cancels fall damage, and spawns
 * the particles.
 */
public final class DoubleJumpNetworking {
    private DoubleJumpNetworking() {}

    public static final Identifier DOUBLE_JUMP_ID = new Identifier(ChrowdMod.MOD_ID, "double_jump");

    /** Largest velocity vector the server will accept (a normal boost is about 0.9). */
    private static final double MAX_SPEED = 2.0;

    /** Players who already used their air jump since last touching the ground. */
    private static final Set<UUID> USED_IN_AIR = new HashSet<>();

    public static void registerServer() {
        ServerPlayNetworking.registerGlobalReceiver(DOUBLE_JUMP_ID, (server, player, handler, buf, responseSender) -> {
            // Read on the network thread, act on the server thread.
            double vx = buf.readDouble();
            double vy = buf.readDouble();
            double vz = buf.readDouble();
            server.execute(() -> handleDoubleJump(player, new Vec3d(vx, vy, vz)));
        });

        // Landing (or swimming, climbing) gives the air jump back.
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (player.isOnGround() || player.isTouchingWater() || player.isClimbing()) {
                    USED_IN_AIR.remove(player.getUuid());
                }
            }
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
            USED_IN_AIR.remove(handler.player.getUuid()));
    }

    private static void handleDoubleJump(ServerPlayerEntity player, Vec3d velocity) {
        if (!Double.isFinite(velocity.x) || !Double.isFinite(velocity.y) || !Double.isFinite(velocity.z)) {
            return;
        }
        if (velocity.length() > MAX_SPEED) {
            return;
        }
        if (!ChrowdItem.isHolding(player) || player.isSpectator()) {
            return;
        }
        if (player.isOnGround() || player.isTouchingWater() || player.isFallFlying()
            || player.getAbilities().flying || player.hasVehicle()) {
            return;
        }
        if (!USED_IN_AIR.add(player.getUuid())) {
            return;
        }

        player.setVelocity(velocity);
        player.fallDistance = 0.0F;

        ServerWorld world = player.getServerWorld();
        world.spawnParticles(ParticleTypes.PORTAL,
            player.getX(), player.getY(), player.getZ(), 30, 0.3, 0.1, 0.3, 0.2);
        world.spawnParticles(ParticleTypes.END_ROD,
            player.getX(), player.getY(), player.getZ(), 15, 0.25, 0.05, 0.25, 0.05);
    }
}
