package dev.compactmods.crafting.events;

import dev.compactmods.crafting.CompactCrafting;
import dev.compactmods.crafting.field.FieldHelper;
import dev.compactmods.crafting.field.MissingFieldsException;
import io.github.fabricators_of_create.porting_lib.event.common.BlockPlaceCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

@SuppressWarnings("unused")
public class BlockEventHandler {

    public static void init() {
        UseBlockCallback.EVENT.register(BlockEventHandler::onRightClickBlock);
        PlayerBlockBreakEvents.BEFORE.register(BlockEventHandler::onBlockDestroyed);
        BlockPlaceCallback.EVENT.register(BlockEventHandler::onBlockPlaced);

    }

    static InteractionResult onRightClickBlock(Player player, Level w, InteractionHand hand, BlockHitResult hitVec) {
        if(w.isClientSide) {
            final BlockPos placedAt = hitVec.getBlockPos().relative(hitVec.getDirection());
            try {
                final boolean allowPlace = FieldHelper.checkBlockPlacement(w, placedAt);
                if (!allowPlace) {
                    return InteractionResult.SUCCESS;
                }

            } catch (MissingFieldsException e) {
                CompactCrafting.LOGGER.error("Missing the active miniaturization fields capability in the level. Report this!");
            }
        }
        return InteractionResult.PASS;
    }

    static InteractionResult onBlockPlaced(BlockPlaceContext context) {
        return !blockHandler(context.getLevel(), context.getClickedPos()) ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    static boolean onBlockDestroyed(Level world, Player player, BlockPos pos, BlockState state, /* Nullable */ BlockEntity blockEntity) {
        return blockHandler(world, pos);
    }

    private static boolean blockHandler(LevelAccessor world, BlockPos pos) {
        // Check if block is in or around a projector field

        // Send the event position over to the field helper, so any nearby projectors can be notified
        if (world instanceof Level) {
            try {
                boolean allowPlace = FieldHelper.checkBlockPlacement((Level) world, pos);
                if (!allowPlace) {
                   return false;
                }
            } catch (MissingFieldsException e) {
                CompactCrafting.LOGGER.error("Missing the active miniaturization fields capability in the level. Report this!");
            }
        }
        return true;
    }
}
