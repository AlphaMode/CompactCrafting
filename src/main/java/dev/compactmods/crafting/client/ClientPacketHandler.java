package dev.compactmods.crafting.client;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.stream.Stream;
import dev.compactmods.crafting.CompactCrafting;
import dev.compactmods.crafting.api.field.IMiniaturizationField;
import dev.compactmods.crafting.core.CCCapabilities;
import dev.compactmods.crafting.field.MiniaturizationField;
import dev.compactmods.crafting.projector.FieldProjectorBlock;
import dev.compactmods.crafting.projector.FieldProjectorEntity;
import io.github.fabricators_of_create.porting_lib.util.LazyOptional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public abstract class ClientPacketHandler {

    public static void handleFieldActivation(IMiniaturizationField field, CompoundTag fieldClientData) {
        Minecraft mc = Minecraft.getInstance();
        mc.submitAsync(() -> {
            ClientLevel cw = mc.level;
            if (cw == null)
                return;

            field.setLevel(cw);
            field.loadClientData(fieldClientData);

            CCCapabilities.FIELDS.maybeGet(mc.level)
                    .ifPresent(fields -> fields.getInst().registerField(field));
        });
    }

    public static void handleFieldDeactivation(BlockPos center) {
        Minecraft mc = Minecraft.getInstance();
        mc.submitAsync(() -> {
            ClientLevel cw = mc.level;
            CCCapabilities.FIELDS.maybeGet(cw).ifPresent(fields -> {
                fields.getInst().get(center).map(IMiniaturizationField::getProjectorPositions)
                        .orElse(Stream.empty())
                        .forEach(proj -> FieldProjectorBlock.deactivateProjector(cw, proj));

                fields.getInst().unregisterField(center);
            });
        });
    }

    public static void handleFieldData(CompoundTag fieldData) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null)
            return;

        MiniaturizationField field = new MiniaturizationField();
        field.setLevel(mc.level);
        field.loadClientData(fieldData);

        CCCapabilities.FIELDS.maybeGet(mc.level)
                .ifPresent(fields -> {
                    fields.getInst().setLevel(mc.level);
                    CompactCrafting.LOGGER.debug("Registering field on client");
                    final IMiniaturizationField fieldRegistered = fields.getInst().registerField(field);

                    CompactCrafting.LOGGER.debug("Setting field references");

                    field.getProjectorPositions()
                            .map(mc.level::getBlockEntity)
                            .map(tile -> (FieldProjectorEntity) tile)
                            .filter(Objects::nonNull)
                            .forEach(tile -> {
                                final BlockState state = tile.getBlockState();
                                tile.setFieldRef(fieldRegistered.getRef());
                            });
                });
    }

    public static void removeField(BlockPos fieldCenter) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null)
            return;

        CCCapabilities.FIELDS.maybeGet(mc.level)
                .ifPresent(fields -> fields.getInst().unregisterField(fieldCenter));
    }

    public static void handleRecipeChanged(BlockPos center, @Nullable ResourceLocation recipe) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null)
            return;

        LazyOptional.fromOptional(CCCapabilities.FIELDS.maybeGet(mc.level))
                .lazyMap(af -> af.getInst().get(center))
                .ifPresent(field -> field.ifPresent(f -> f.setRecipe(recipe)));
    }
}
