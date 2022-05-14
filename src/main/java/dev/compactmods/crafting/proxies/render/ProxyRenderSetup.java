package dev.compactmods.crafting.proxies.render;

import dev.compactmods.crafting.core.CCBlocks;
import dev.compactmods.crafting.core.CCItems;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.renderer.RenderType;

@SuppressWarnings("unused")
public class ProxyRenderSetup {

    public static void init() {
        BlockRenderLayerMap.INSTANCE.putBlock(CCBlocks.MATCH_FIELD_PROXY_BLOCK.get(), RenderType.cutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(CCBlocks.RESCAN_FIELD_PROXY_BLOCK.get(), RenderType.cutoutMipped());
        onBlockColors();
        onItemColors();
    }

    public static void onBlockColors() {
        // color the ring at the base of the proxy poles
       ColorProviderRegistry.BLOCK.register(new FieldProxyColors.MatchBlock(), CCBlocks.MATCH_FIELD_PROXY_BLOCK.get());
       ColorProviderRegistry.BLOCK.register(new FieldProxyColors.RescanBlock(), CCBlocks.RESCAN_FIELD_PROXY_BLOCK.get());
    }

    public static void onItemColors() {
        ColorProviderRegistry.ITEM.register(new FieldProxyColors.MatchItem(), CCItems.MATCH_PROXY_ITEM.get());
        ColorProviderRegistry.ITEM.register(new FieldProxyColors.RescanItem(), CCItems.RESCAN_PROXY_ITEM.get());
    }
}
