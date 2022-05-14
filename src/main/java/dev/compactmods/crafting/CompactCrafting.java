package dev.compactmods.crafting;

import dev.compactmods.crafting.client.ClientConfig;
import dev.compactmods.crafting.client.ui.container.ContainerRegistration;
import dev.compactmods.crafting.command.CraftingCommandRoot;
import dev.compactmods.crafting.core.*;
import dev.compactmods.crafting.events.BlockEventHandler;
import dev.compactmods.crafting.events.WorldEventHandler;
import dev.compactmods.crafting.network.NetworkHandler;
import dev.compactmods.crafting.recipes.components.ComponentRegistration;
import dev.compactmods.crafting.server.ServerConfig;
import io.github.fabricators_of_create.porting_lib.util.LazyItemGroup;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.ModLoadingContext;
import net.minecraftforge.api.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CompactCrafting implements ModInitializer
{
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger(CompactCrafting.MOD_ID);
    public static final Logger RECIPE_LOGGER = LogManager.getLogger("CCRecipeLoader");

    public static final String MOD_ID = "compactcrafting";

    public static final CreativeModeTab ITEM_GROUP = new CCItemGroup();

    @Override
    public void onInitialize() {
        NetworkHandler.initialize();

        ModLoadingContext.registerConfig(MOD_ID, ModConfig.Type.CLIENT, ClientConfig.CONFIG);
        ModLoadingContext.registerConfig(MOD_ID, ModConfig.Type.SERVER, ServerConfig.CONFIG);
        ModConfigEvent.LOADING.register(ClientConfig::onLoad);
        ModConfigEvent.LOADING.register(ServerConfig::onConfigEvent);
        ModConfigEvent.RELOADING.register(ClientConfig::onLoad);
        ModConfigEvent.RELOADING.register(ServerConfig::onConfigEvent);

        CCBlocks.init();
        CCCatalystTypes.init();
        CCItems.init();
        CCLayerTypes.init();
        CCMiniaturizationRecipes.init();

        ComponentRegistration.init();
        ContainerRegistration.init();

        CommandRegistrationCallback.EVENT.register(CraftingCommandRoot::onCommandsRegister);
        BlockEventHandler.init();
        WorldEventHandler.init();
    }

    public static class CCItemGroup extends LazyItemGroup {
        public CCItemGroup() {
            super(CompactCrafting.MOD_ID);
        }

        @Override
        public ItemStack makeIcon() {
            return new ItemStack(CCItems.FIELD_PROJECTOR_ITEM.get());
        }
    }
}
