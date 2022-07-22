package dev.compactmods.crafting.api.field;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MiniaturizationProvider implements Component {

    private IMiniaturizationField field;

    public MiniaturizationProvider(BlockEntity be) {}

    public IMiniaturizationField getField() {
        return field;
    }

    public void setMiniaturizationField(IMiniaturizationField field) {
        this.field = field;
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        field.deserializeNBT(tag.get("data"));
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.put("data", field.serializeNBT());
    }
}
