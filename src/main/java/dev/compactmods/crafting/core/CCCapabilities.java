package dev.compactmods.crafting.core;

import dev.compactmods.crafting.CompactCrafting;
import dev.compactmods.crafting.api.field.IActiveWorldFields;
import dev.compactmods.crafting.api.field.IFieldListener;
import dev.compactmods.crafting.api.field.IMiniaturizationField;
import dev.compactmods.crafting.api.projector.IProjectorRenderInfo;
import dev.compactmods.crafting.client.render.ClientProjectorRenderInfo;
import dev.compactmods.crafting.field.ActiveWorldFields;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import io.github.fabricators_of_create.porting_lib.util.LazyOptional;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CCCapabilities implements WorldComponentInitializer, EntityComponentInitializer {

    public static ComponentKey<IProjectorRenderInfo> TEMP_PROJECTOR_RENDERING = ComponentRegistry.getOrCreate(new ResourceLocation(CompactCrafting.MOD_ID, "projector_renderer"), IProjectorRenderInfo.class);

    public static ComponentKey<IActiveWorldFields> FIELDS = ComponentRegistry.getOrCreate(new ResourceLocation(CompactCrafting.MOD_ID, "fields"), IActiveWorldFields.class);

    public static ComponentKey<IFieldListener> FIELD_LISTENER = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static ComponentKey<IMiniaturizationField> MINIATURIZATION_FIELD = CapabilityManager.get(new CapabilityToken<>() {
    });

    @SubscribeEvent
    public void registerCapabilities(RegisterCapabilitiesEvent evt) {
        evt.register(IProjectorRenderInfo.class);
        evt.register(IMiniaturizationField.class);
        evt.register(IActiveWorldFields.class);
        evt.register(IFieldListener.class);
    }

    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
        registry.register(FIELDS, ActiveWorldFields::new);
    }

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(TEMP_PROJECTOR_RENDERING, ClientProjectorRenderInfo::new);
    }
}
