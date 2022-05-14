package dev.compactmods.crafting.projector.render;

import dev.compactmods.crafting.core.CCBlocks;
import dev.compactmods.crafting.core.CCItems;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.renderer.RenderType;

public class FieldProjectorRenderSetup {

    public static void regRenderer() {
        BlockEntityRendererRegistry.register(CCBlocks.FIELD_PROJECTOR_TILE.get(), FieldProjectorRenderer::new);
    }

    public static void init() {
        BlockRenderLayerMap.INSTANCE.putBlock(CCBlocks.FIELD_PROJECTOR_BLOCK.get(), RenderType.cutoutMipped());
        regRenderer();
        onBlockColors();
        onItemColors();
        registerSpecialModels();
    }

    public static void onBlockColors() {
        ColorProviderRegistry.BLOCK.register(new FieldProjectorColors.Block(), CCBlocks.FIELD_PROJECTOR_BLOCK.get());
    }

    public static void onItemColors() {
        ColorProviderRegistry.ITEM.register(new FieldProjectorColors.Item(), CCItems.FIELD_PROJECTOR_ITEM.get());
    }

    public static void registerSpecialModels() {
        ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> out.accept(FieldProjectorRenderer.FIELD_DISH_RL));
    }
}
