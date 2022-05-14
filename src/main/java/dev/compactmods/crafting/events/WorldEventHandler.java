package dev.compactmods.crafting.events;

import dev.compactmods.crafting.CompactCrafting;
import dev.compactmods.crafting.api.field.IActiveWorldFields;
import dev.compactmods.crafting.core.CCCapabilities;
import dev.compactmods.crafting.network.ClientFieldUnwatchPacket;
import dev.compactmods.crafting.network.ClientFieldWatchPacket;
import dev.compactmods.crafting.network.NetworkHandler;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

@SuppressWarnings("unused")
public class WorldEventHandler {

    public static void init() {
        ServerLifecycleEvents.SERVER_STARTED.register(WorldEventHandler::onServerStarted);
        ServerTickEvents.START_WORLD_TICK.register(WorldEventHandler::onWorldTick);
        ClientTickEvents.START_WORLD_TICK.register(WorldEventHandler::onWorldTick);
    }

    public static final Subject<ChunkEvent> CHUNK_CHANGES;

    static {
        CHUNK_CHANGES = PublishSubject.create();
    }

    public static void onServerStarted(MinecraftServer server) {
        CompactCrafting.LOGGER.trace("Server started; calling previously active fields to validate themselves.");
        for (ServerLevel level : server.getAllLevels()) {
            CCCapabilities.FIELDS.maybeGet(level)
                    .ifPresent(fields -> {
                        fields.setLevel(level);
                        fields.getFields().forEach(f -> {
                            f.setLevel(level);
                            f.checkLoaded();
                        });
                    });
        }
    }

    public static void onWorldTick(final Level world) {
        CCCapabilities.FIELDS.maybeGet(world)
                .ifPresent(IActiveWorldFields::tickFields);
    }

    @SubscribeEvent
    public static void onStartChunkTracking(final ChunkWatchEvent.Watch event) {
        final ServerPlayer player = event.getPlayer();
        final ChunkPos pos = event.getPos();
        final ServerLevel level = event.getWorld();

        CCCapabilities.FIELDS.maybeGet(level)
                .map(f -> f.getFields(pos))
                .ifPresent(activeFields -> {
                    activeFields.forEach(field -> {
                        ClientFieldWatchPacket pkt = new ClientFieldWatchPacket(field);

                        NetworkHandler.MAIN_CHANNEL.send(
                                PacketDistributor.PLAYER.with(() -> player),
                                pkt
                        );
                    });
                });
    }

    @SubscribeEvent
    public static void onStopChunkTracking(final ChunkWatchEvent.UnWatch event) {
        final ServerPlayer player = event.getPlayer();
        final ChunkPos pos = event.getPos();
        final ServerLevel level = event.getWorld();

        CCCapabilities.FIELDS.maybeGet(level)
                .map(f -> f.getFields(pos))
                .ifPresent(activeFields -> {
                    activeFields.forEach(field -> {
                        ClientFieldUnwatchPacket pkt = new ClientFieldUnwatchPacket(field.getCenter());

                        NetworkHandler.MAIN_CHANNEL.send(
                                PacketDistributor.PLAYER.with(() -> player),
                                pkt
                        );
                    });
                });
    }

    @SubscribeEvent
    public static void onChunkLoadStatusChanged(final ChunkEvent cEvent) {
        if (cEvent instanceof ChunkEvent.Load || cEvent instanceof ChunkEvent.Unload) {
            // CompactCrafting.LOGGER.debug("Chunk load status changed: {}", cEvent.getChunk().getPos());
            CHUNK_CHANGES.onNext(cEvent);
        }
    }
}
