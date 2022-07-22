package dev.compactmods.crafting.events;

import dev.compactmods.crafting.CompactCrafting;
import dev.compactmods.crafting.api.field.IActiveWorldFields;
import dev.compactmods.crafting.core.CCCapabilities;
import dev.compactmods.crafting.network.ClientFieldUnwatchPacket;
import dev.compactmods.crafting.network.ClientFieldWatchPacket;
import dev.compactmods.crafting.network.NetworkHandler;
import io.github.fabricators_of_create.porting_lib.event.common.ChunkTrackingCallback;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;

@SuppressWarnings("unused")
public class WorldEventHandler {

    public static void init() {
        ServerLifecycleEvents.SERVER_STARTED.register(WorldEventHandler::onServerStarted);
        ServerTickEvents.START_WORLD_TICK.register(WorldEventHandler::onWorldTick);
        ClientTickEvents.START_WORLD_TICK.register(WorldEventHandler::onWorldTick);
        ServerChunkEvents.CHUNK_LOAD.register(WorldEventHandler::onChunkLoadStatusChanged);
        ServerChunkEvents.CHUNK_UNLOAD.register(WorldEventHandler::onChunkLoadStatusChanged);
        ClientChunkEvents.CHUNK_LOAD.register(WorldEventHandler::onChunkLoadStatusChanged);
        ClientChunkEvents.CHUNK_UNLOAD.register(WorldEventHandler::onChunkLoadStatusChanged);
        ChunkTrackingCallback.WATCH.register(WorldEventHandler::onStartChunkTracking);
        ChunkTrackingCallback.UNWATCH.register(WorldEventHandler::onStopChunkTracking);
    }

    public static final Subject<ChunkAccess> CHUNK_CHANGES;

    static {
        CHUNK_CHANGES = PublishSubject.create();
    }

    public static void onServerStarted(MinecraftServer server) {
        CompactCrafting.LOGGER.trace("Server started; calling previously active fields to validate themselves.");
        for (ServerLevel level : server.getAllLevels()) {
            CCCapabilities.FIELDS.maybeGet(level)
                    .ifPresent(fields -> {
                        fields.getInst().setLevel(level);
                        fields.getInst().getFields().forEach(f -> {
                            f.setLevel(level);
                            f.checkLoaded();
                        });
                    });
        }
    }

    public static void onWorldTick(final Level world) {
        CCCapabilities.FIELDS.maybeGet(world)
                .ifPresent(levelFieldsProvider -> levelFieldsProvider.getInst().tickFields());
    }

    public static void onStartChunkTracking(final ServerPlayer player, final ChunkPos pos, final ServerLevel level) {
        CCCapabilities.FIELDS.maybeGet(level)
                .map(f -> f.getInst().getFields(pos))
                .ifPresent(activeFields -> {
                    activeFields.forEach(field -> {
                        ClientFieldWatchPacket pkt = new ClientFieldWatchPacket(field);

                        NetworkHandler.MAIN_CHANNEL.sendToClient(pkt, player);
                    });
                });
    }

    public static void onStopChunkTracking(final ServerPlayer player, final ChunkPos pos, final ServerLevel level) {
        CCCapabilities.FIELDS.maybeGet(level)
                .map(f -> f.getInst().getFields(pos))
                .ifPresent(activeFields -> {
                    activeFields.forEach(field -> {
                        ClientFieldUnwatchPacket pkt = new ClientFieldUnwatchPacket(field.getCenter());

                        NetworkHandler.MAIN_CHANNEL.sendToClient(pkt, player);
                    });
                });
    }

    public static void onChunkLoadStatusChanged(Level world, ChunkAccess chunk) {
        // CompactCrafting.LOGGER.debug("Chunk load status changed: {}", cEvent.getChunk().getPos());
        CHUNK_CHANGES.onNext(chunk);
    }
}
