package dev.compactmods.crafting.field.events;

import dev.compactmods.crafting.api.field.IActiveWorldFields;
import dev.compactmods.crafting.field.ActiveWorldFields;
import dev.onyxstudios.cca.api.v3.component.Component;
import io.github.fabricators_of_create.porting_lib.util.INBTSerializable;
import io.github.fabricators_of_create.porting_lib.util.LazyOptional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntity;

public class LevelFieldsProvider implements Component {

    private LazyOptional<IActiveWorldFields> inst;

    public LevelFieldsProvider(BlockEntity blockEntity) {

    }

    public LevelFieldsProvider(ActiveWorldFields activeWorldFields) {
        this.inst = LazyOptional.of(() -> activeWorldFields);
    }

    public IActiveWorldFields getInst() {
        return inst.getValueUnsafer();
    }

    public void setInst(LazyOptional<IActiveWorldFields> inst) {
        this.inst = inst;
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.put("data", inst.map(INBTSerializable::serializeNBT).orElse(new ListTag()));
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        ListTag nbt = tag.getList("data", Tag.TAG_LIST);
        inst.ifPresent(i -> i.deserializeNBT(nbt));
    }

    public void invalidate() {
        inst.invalidate();
    }
}
