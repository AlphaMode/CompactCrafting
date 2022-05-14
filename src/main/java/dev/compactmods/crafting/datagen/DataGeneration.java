package dev.compactmods.crafting.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

public class DataGeneration implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator gen) {
//        if (event.includeServer())
            registerServerProviders(gen);

//        if (event.includeClient())
            registerClientProviders(gen, null);
    }

    private static void registerServerProviders(FabricDataGenerator generator) {
        generator.addProvider(new LootTableGenerator(generator));
        generator.addProvider(new RecipeGenerator(generator));
    }

    private static void registerClientProviders(DataGenerator generator, ExistingFileHelper helper) {
        generator.addProvider(new SharedStateGenerator(generator, helper));
        generator.addProvider(new ProjectorStateGenerator(generator, helper));
        generator.addProvider(new ProxyStateGenerator(generator, helper));
    }
}
