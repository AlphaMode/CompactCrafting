package dev.compactmods.crafting.proxies.block;

import javax.annotation.Nullable;
import dev.compactmods.crafting.api.EnumCraftingState;
import dev.compactmods.crafting.core.CCCapabilities;
import dev.compactmods.crafting.proxies.data.BaseFieldProxyEntity;
import dev.compactmods.crafting.proxies.data.RescanFieldProxyEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class RescanFieldProxyBlock extends FieldProxyBlock implements EntityBlock {

    public RescanFieldProxyBlock(Properties props) {
        super(props);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RescanFieldProxyEntity(pos, state);
    }

    @Override
    public void neighborChanged(BlockState thisState, Level level, BlockPos thisPos, Block changedBlock, BlockPos changedPos, boolean _b) {
        if (level.isClientSide) {
            return;
        }
        BaseFieldProxyEntity tile = (BaseFieldProxyEntity) level.getBlockEntity(thisPos);

        if (level.hasNeighborSignal(thisPos)) {
            // call recipe scan
            if (tile != null) {
                CCCapabilities.MINIATURIZATION_FIELD.maybeGet(tile)
                        .ifPresent(field -> {
                            if(field.getField().getCraftingState() != EnumCraftingState.CRAFTING)
                                field.getField().fieldContentsChanged();
                        });
            }
        }
    }
}
