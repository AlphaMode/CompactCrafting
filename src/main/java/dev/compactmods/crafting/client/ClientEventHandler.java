package dev.compactmods.crafting.client;

import javax.annotation.Nonnull;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.compactmods.crafting.CompactCrafting;
import dev.compactmods.crafting.api.EnumCraftingState;
import dev.compactmods.crafting.api.field.IActiveWorldFields;
import dev.compactmods.crafting.api.field.IMiniaturizationField;
import dev.compactmods.crafting.api.projector.IProjectorRenderInfo;
import dev.compactmods.crafting.api.recipe.IMiniaturizationRecipe;
import dev.compactmods.crafting.core.CCCapabilities;
import dev.compactmods.crafting.field.render.CraftingPreviewRenderer;
import dev.compactmods.crafting.projector.render.FieldProjectorRenderSetup;
import dev.compactmods.crafting.proxies.render.ProxyRenderSetup;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ClientEventHandler implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ProxyRenderSetup.init();
        ClientTickEvents.START_CLIENT_TICK.register(ClientEventHandler::onTick);
        WorldRenderEvents.END.register(ClientEventHandler::onWorldRender);
        FieldProjectorRenderSetup.init();
    }

    public static void onTick(Minecraft client) {
        final LocalPlayer player = Minecraft.getInstance().player;
        if(player != null) {
            CCCapabilities.TEMP_PROJECTOR_RENDERING.maybeGet(player)
                    .ifPresent(IProjectorRenderInfo::tick);
        }

        ClientLevel level = Minecraft.getInstance().level;
        if (level != null && !Minecraft.getInstance().isPaused()) {
            CCCapabilities.FIELDS.maybeGet(level)
                    .ifPresent(levelFieldsProvider -> levelFieldsProvider.getInst().tickFields());
        }
    }

    public static void onWorldRender(WorldRenderContext context) {
        final Minecraft mc = Minecraft.getInstance();

        if (mc.level == null)
            return;

        doProjectorRender(context, mc);
        doFieldPreviewRender(context, mc);
    }

    @Nonnull
    private static void doFieldPreviewRender(WorldRenderContext context, Minecraft mc) {
        final Camera mainCamera = mc.gameRenderer.getMainCamera();
        final HitResult hitResult = mc.hitResult;

        double viewDistance = 64;
        final MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
        CCCapabilities.FIELDS.maybeGet(mc.level)
                .ifPresent(fields -> {
                    fields.getInst().getFields()
                            .filter(field -> Vec3.atCenterOf(field.getCenter()).closerThan(mainCamera.getPosition(), viewDistance))
                            .filter(field -> field.getCraftingState() == EnumCraftingState.CRAFTING)
                            .filter(field -> field.getCurrentRecipe().isPresent())
                            .filter(IMiniaturizationField::enabled)
                            .forEach(field -> {
                                BlockPos center = field.getCenter();

                                PoseStack stack = context.matrixStack();
                                stack.pushPose();
                                Vec3 projectedView = mainCamera.getPosition();
                                stack.translate(-projectedView.x, -projectedView.y, -projectedView.z);

                                stack.translate(
                                        (double) center.getX(),
                                        (double) center.getY(),
                                        (double) center.getZ()
                                );

                                final IMiniaturizationRecipe rec = field.getCurrentRecipe().get();
                                final int prog = field.getProgress();

                                CraftingPreviewRenderer.render(
                                        rec, prog, stack,
                                        buffers, LightTexture.FULL_SKY, OverlayTexture.NO_OVERLAY
                                );

                                stack.popPose();
                            });
                });
        buffers.endBatch();
    }

    private static void doProjectorRender(WorldRenderContext context, Minecraft mc) {
        CCCapabilities.TEMP_PROJECTOR_RENDERING.maybeGet(mc.player)
                .ifPresent(render -> render.render(context.matrixStack()));
    }
}
