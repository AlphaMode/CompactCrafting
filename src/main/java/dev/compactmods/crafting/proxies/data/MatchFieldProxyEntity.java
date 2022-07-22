package dev.compactmods.crafting.proxies.data;

import dev.compactmods.crafting.api.field.IFieldListener;
import dev.compactmods.crafting.api.field.IMiniaturizationField;
import dev.compactmods.crafting.core.CCBlocks;
import dev.compactmods.crafting.core.CCCapabilities;
import dev.compactmods.crafting.proxies.listener.MatchModeProxyFieldListener;
import io.github.fabricators_of_create.porting_lib.util.LazyOptional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class MatchFieldProxyEntity extends BaseFieldProxyEntity {
    protected LazyOptional<IFieldListener> listener = LazyOptional.empty();

    public MatchFieldProxyEntity(BlockPos pos, BlockState state) {
        super(CCBlocks.MATCH_PROXY_ENTITY.get(), pos, state);
    }

    @Override
    protected void fieldChanged(LazyOptional<IMiniaturizationField> f) {
        super.fieldChanged(f);

        MatchModeProxyFieldListener listener = new MatchModeProxyFieldListener(level, worldPosition);

        this.listener = LazyOptional.of(() -> listener);
        CCCapabilities.FIELD_LISTENER.get(this).setFieldListener(listener);

        // if field actually present, register this proxy
        f.ifPresent(f2 -> f2.registerListener(this.listener));
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        listener.invalidate();
    }
}
