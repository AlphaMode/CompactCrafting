package dev.compactmods.crafting.api.field;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;

public class FieldListenerProvider implements Component {

    private IFieldListener fieldListener;

    public FieldListenerProvider(BlockEntity blockEntity) {

    }

    public IFieldListener getFieldListener() {
        return fieldListener;
    }

    public void setFieldListener(IFieldListener fieldListener) {
        this.fieldListener = fieldListener;
    }

    @Override
    public void readFromNbt(CompoundTag tag) {}

    @Override
    public void writeToNbt(CompoundTag tag) {}
}
