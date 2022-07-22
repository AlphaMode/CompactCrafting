package dev.compactmods.crafting.core;

import dev.compactmods.crafting.CompactCrafting;
import dev.compactmods.crafting.api.field.*;
import dev.compactmods.crafting.api.projector.IProjectorRenderInfo;
import dev.compactmods.crafting.client.render.ClientProjectorRenderInfo;
import dev.compactmods.crafting.field.ActiveWorldFields;
import dev.compactmods.crafting.field.events.LevelFieldsProvider;
import dev.compactmods.crafting.projector.FieldProjectorEntity;
import dev.compactmods.crafting.proxies.data.BaseFieldProxyEntity;
import dev.compactmods.crafting.proxies.data.MatchFieldProxyEntity;
import dev.onyxstudios.cca.api.v3.block.BlockComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.block.BlockComponentInitializer;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import net.minecraft.resources.ResourceLocation;

public class CCCapabilities implements WorldComponentInitializer, EntityComponentInitializer, BlockComponentInitializer {

    public static ComponentKey<IProjectorRenderInfo> TEMP_PROJECTOR_RENDERING = ComponentRegistry.getOrCreate(new ResourceLocation(CompactCrafting.MOD_ID, "projector_renderer"), IProjectorRenderInfo.class);

    public static ComponentKey<LevelFieldsProvider> FIELDS = ComponentRegistry.getOrCreate(new ResourceLocation(CompactCrafting.MOD_ID, "fields"), LevelFieldsProvider.class);

    public static ComponentKey<FieldListenerProvider> FIELD_LISTENER = ComponentRegistry.getOrCreate(new ResourceLocation(CompactCrafting.MOD_ID, "field_listener"), FieldListenerProvider.class);

    public static ComponentKey<MiniaturizationProvider> MINIATURIZATION_FIELD = ComponentRegistry.getOrCreate(new ResourceLocation(CompactCrafting.MOD_ID, "miniaturization_field"), MiniaturizationProvider.class);

    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
        registry.register(FIELDS, level -> new LevelFieldsProvider(new ActiveWorldFields(level)));
    }

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(TEMP_PROJECTOR_RENDERING, ClientProjectorRenderInfo::new);
    }

    @Override
    public void registerBlockComponentFactories(BlockComponentFactoryRegistry registry) {
        registry.registerFor(FieldProjectorEntity.class, FIELDS, LevelFieldsProvider::new);
        registry.registerFor(FieldProjectorEntity.class, MINIATURIZATION_FIELD, MiniaturizationProvider::new);
        registry.registerFor(BaseFieldProxyEntity.class, MINIATURIZATION_FIELD, MiniaturizationProvider::new);
        registry.registerFor(MatchFieldProxyEntity.class, FIELD_LISTENER, FieldListenerProvider::new);
    }
}
