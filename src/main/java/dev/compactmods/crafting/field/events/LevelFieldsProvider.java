package dev.compactmods.crafting.field.events;

import dev.compactmods.crafting.api.field.IActiveWorldFields;
import dev.onyxstudios.cca.api.v3.component.Component;
import io.github.fabricators_of_create.porting_lib.util.INBTSerializable;
import io.github.fabricators_of_create.porting_lib.util.LazyOptional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

class LevelFieldsProvider implements Component {

    private final LazyOptional<IActiveWorldFields> inst;

    LevelFieldsProvider(IActiveWorldFields inst) {
        this.inst = LazyOptional.of(() -> inst);
    }

    public LazyOptional<IActiveWorldFields> getInst() {
        return inst;
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
