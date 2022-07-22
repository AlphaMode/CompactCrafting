package dev.compactmods.crafting.projector;

import dev.compactmods.crafting.api.field.IActiveWorldFields;
import dev.compactmods.crafting.api.field.IMiniaturizationField;
import dev.compactmods.crafting.core.CCBlocks;
import dev.compactmods.crafting.core.CCCapabilities;
import io.github.fabricators_of_create.porting_lib.block.CustomRenderBoundingBoxBlockEntity;
import io.github.fabricators_of_create.porting_lib.util.LazyOptional;
import io.github.fabricators_of_create.porting_lib.util.OnLoadBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class FieldProjectorEntity extends BlockEntity implements OnLoadBlockEntity, CustomRenderBoundingBoxBlockEntity {

    protected LazyOptional<IMiniaturizationField> fieldCap = LazyOptional.empty();
    protected LazyOptional<IActiveWorldFields> levelFields = LazyOptional.empty();

    public FieldProjectorEntity(BlockPos pos, BlockState state) {
        super(CCBlocks.FIELD_PROJECTOR_TILE.get(), pos, state);
    }

    public Direction getProjectorSide() {
        return getBlockState().getValue(FieldProjectorBlock.FACING).getOpposite();
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        this.levelFields = LazyOptional.ofObject(CCCapabilities.FIELDS.maybeGet(this.level).get().getInst());
        CCCapabilities.FIELDS.get(this).setInst(this.levelFields);
    }

    @Override
    public void onLoad() {
        if (level != null) {
            if(level.isClientSide) {
                loadFieldFromState();
            } else {
                MinecraftServer server = level.getServer();
                if(server != null)
                    server.tell(new TickTask(0, this::loadFieldFromState));
            }
        }
    }

    private void loadFieldFromState() {
        this.fieldCap = levelFields.lazyMap(fields -> {
            BlockState state = getBlockState();
            return fields.getLazy(FieldProjectorBlock.getFieldCenter(state, worldPosition));
        }).orElse(LazyOptional.empty());
        CCCapabilities.MINIATURIZATION_FIELD.get(this).setMiniaturizationField(fieldCap.getValueUnsafer());
    }

    @Override
    public AABB getRenderBoundingBox() {
        // Check - if we have a valid field use the entire field plus space
        // Otherwise just use the super implementation
        return fieldCap
                .map(f -> f.getBounds().inflate(20))
                .orElse(new AABB(worldPosition).inflate(20));
    }

    public LazyOptional<IMiniaturizationField> getField() {
        return this.fieldCap.cast();
    }

    public void setFieldRef(LazyOptional<IMiniaturizationField> fieldRef) {
        this.fieldCap = fieldRef;
        CCCapabilities.MINIATURIZATION_FIELD.get(this).setMiniaturizationField(fieldCap.getValueUnsafer());
        setChanged();
    }
}
