package dev.compactmods.crafting.proxies.data;

import dev.compactmods.crafting.api.field.IMiniaturizationField;
import dev.compactmods.crafting.core.CCCapabilities;
import io.github.fabricators_of_create.porting_lib.extensions.BlockEntityExtensions;
import io.github.fabricators_of_create.porting_lib.util.LazyOptional;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public abstract class BaseFieldProxyEntity extends BlockEntity implements BlockEntityExtensions {

    @Nullable
    protected BlockPos fieldCenter;

    protected LazyOptional<IMiniaturizationField> field = LazyOptional.empty();

    public BaseFieldProxyEntity(BlockEntityType<? extends BaseFieldProxyEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void onLoad() {
        if(fieldCenter != null && level != null) {
            CCCapabilities.FIELDS.maybeGet(level)
                    .ifPresent(fields -> fieldChanged(fields.getInst().getLazy(fieldCenter)));
        }
    }

    public void updateField(BlockPos fieldCenter) {
        if(level == null)
            return;

        if(fieldCenter == null) {
            this.field = LazyOptional.empty();
            this.fieldCenter = null;
            return;
        }

        CCCapabilities.FIELDS.maybeGet(level)
                .map(fields -> fields.getInst().getLazy(fieldCenter))
                .ifPresent(f -> {
                    this.fieldCenter = fieldCenter;

                    fieldChanged(f);
                });
    }

    protected void fieldChanged(LazyOptional<IMiniaturizationField> f) {
        this.field = f;
        CCCapabilities.MINIATURIZATION_FIELD.get(this).setMiniaturizationField(this.field.getValueUnsafer());

        // field invalidated somewhere
        f.addListener(lof -> {
            this.field = LazyOptional.empty();
            CCCapabilities.MINIATURIZATION_FIELD.get(this).setMiniaturizationField(null);
            this.fieldCenter = null;
        });
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        field.ifPresent(field -> {
            tag.put("center", NbtUtils.writeBlockPos(field.getCenter()));
        });
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        if(tag.contains("center")) {
            this.fieldCenter = NbtUtils.readBlockPos(tag.getCompound("center"));
        }
    }
}
