package dev.compactmods.crafting.client.ui.container;

import dev.compactmods.crafting.CompactCrafting;
import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.fabric.impl.screenhandler.ExtendedScreenHandlerType;
import net.minecraft.core.Registry;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;

public class ContainerRegistration {

    public static final LazyRegistrar<MenuType<?>> CONTAINERS =
            LazyRegistrar.create(Registry.MENU, CompactCrafting.MOD_ID);

    public static final RegistryObject<MenuType<TestContainer>> TEST_CONTAINER = CONTAINERS.register("test",
            () -> new ExtendedScreenHandlerType<>((windowId, inv, buf) -> {
                Level world = inv.player.level;
                return new TestContainer(windowId, world, inv, inv.player);
            }));

    public static void init() {
        CONTAINERS.register();
    }
}
