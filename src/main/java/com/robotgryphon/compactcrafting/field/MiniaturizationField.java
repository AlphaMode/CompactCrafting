package com.robotgryphon.compactcrafting.field;

import com.robotgryphon.compactcrafting.CompactCrafting;
import com.robotgryphon.compactcrafting.Registration;
import com.robotgryphon.compactcrafting.crafting.CraftingHelper;
import com.robotgryphon.compactcrafting.crafting.EnumCraftingState;
import com.robotgryphon.compactcrafting.field.capability.IMiniaturizationField;
import com.robotgryphon.compactcrafting.field.tile.FieldCraftingPreviewTile;
import com.robotgryphon.compactcrafting.projector.ProjectorHelper;
import com.robotgryphon.compactcrafting.projector.block.FieldProjectorBlock;
import com.robotgryphon.compactcrafting.recipes.MiniaturizationRecipe;
import com.robotgryphon.compactcrafting.util.BlockSpaceUtil;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MiniaturizationField implements IMiniaturizationField {

    private FieldProjectionSize size;
    private BlockPos center;

    private MiniaturizationRecipe currentRecipe = null;
    private EnumCraftingState craftingState;

    public MiniaturizationField() {
    }

    private MiniaturizationField(FieldProjectionSize size, BlockPos center) {
        this.center = center;
        this.size = size;
        this.craftingState = EnumCraftingState.NOT_MATCHED;
    }

    public static MiniaturizationField fromSizeAndCenter(FieldProjectionSize fieldSize, BlockPos center) {
        return new MiniaturizationField(fieldSize, center);
    }

    public FieldProjectionSize getFieldSize() {
        return this.size;
    }

    public BlockPos getCenterPosition() {
        return center;
    }

    @Override
    public void setCenter(BlockPos center) {
        this.center = center;
    }

    @Override
    public void setSize(FieldProjectionSize size) {
        this.size = size;
    }

    public AxisAlignedBB getBounds() {
        FieldProjectionSize size = this.size;
        BlockPos center = getCenterPosition();
        AxisAlignedBB bounds = new AxisAlignedBB(center).inflate(size.getSize());

        return bounds;
    }

    /**
     * Fetches a set of potential field projection sizes via an initial projector location. Will offset to a
     * set of center blocks, then validate if there are all four projectors at that size.
     *
     * @param world
     * @param initial
     * @return
     */
    public static Set<FieldProjectionSize> getValidFieldSizesByInitial(IWorldReader world, BlockPos initial) {
        Optional<Direction> facing = FieldProjectorBlock.getDirection(world, initial);

        // Initial wasn't a valid field projector, can't get direction to look in
        if (!facing.isPresent())
            return Collections.emptySet();

        Direction fieldDirection = facing.get();

        return Stream.of(FieldProjectionSize.values())
                .filter(s -> isFieldSizeValid(world, initial, fieldDirection, s))
                .collect(Collectors.toSet());
    }

    private static boolean isFieldSizeValid(IWorldReader world, BlockPos initial, Direction fieldDirection, FieldProjectionSize size) {
        // check block exists in offset position
        Optional<BlockPos> center = ProjectorHelper.getCenterForSize(world, initial, size);

        // No center block or couldn't get projector direction
        if (!center.isPresent())
            return false;

        return ProjectorHelper.checkProjectorsValid(world, center.get(), size);
    }

    public static Optional<MiniaturizationField> tryCreateFromProjector(IWorldReader world, BlockPos position) {
        Optional<Direction> dir = FieldProjectorBlock.getDirection(world, position);

        // No direction found - probably not a field projector at this location
        if (!dir.isPresent())
            return Optional.empty();

        Set<FieldProjectionSize> potentialSizes = getValidFieldSizesByInitial(world, position);

        for (FieldProjectionSize potSize : potentialSizes) {
            Optional<BlockPos> center = ProjectorHelper.getCenterForSize(world, position, potSize);
            if (!center.isPresent())
                continue;

            Direction.Axis mainAxis = dir.get().getAxis();
            Direction.Axis crossAxis;
            switch (mainAxis) {
                case X:
                    crossAxis = Direction.Axis.Z;
                    break;

                case Z:
                    crossAxis = Direction.Axis.X;
                    break;

                default:
                    // Thi shouldn't happen, but better than an error
                    crossAxis = Direction.Axis.Y;
                    break;
            }

            boolean crossAxisValid = ProjectorHelper.checkAxisForValidProjectors(world, center.get(), crossAxis, potSize);

            // Found a valid size?
            if (crossAxisValid)
                return Optional.of(new MiniaturizationField(potSize, center.get()));
        }

        // No cross axis tests were successful
        return Optional.empty();
    }

    public BlockPos getProjectorInDirection(Direction direction) {
        return center.relative(direction, size.getProjectorDistance() + 1);
    }

    public Stream<BlockPos> getFilledBlocks(IWorldReader level) {
        return BlockPos.betweenClosedStream(getBounds().contract(1, 1, 1))
                .filter(p -> !level.isEmptyBlock(p))
                .map(BlockPos::immutable);
    }

    public AxisAlignedBB getFilledBounds(IWorldReader level) {
        BlockPos[] filled = getFilledBlocks(level).toArray(BlockPos[]::new);
        return BlockSpaceUtil.getBoundsForBlocks(filled);
    }

    public void clearBlocks(IWorld world) {
        // Remove blocks from the world
        getFilledBlocks(world)
                .sorted(Comparator.comparingInt(Vector3i::getY).reversed())
                .forEach(blockPos -> {
                    world.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 7);

                    if (world instanceof ServerWorld) {
                        ((ServerWorld) world).sendParticles(ParticleTypes.LARGE_SMOKE,
                                blockPos.getX() + 0.5f, blockPos.getY() + 0.5f, blockPos.getZ() + 0.5f,
                                1, 0d, 0.05D, 0D, 0.25d);
                    }
                });
    }

    public Optional<MiniaturizationRecipe> getCurrentRecipe() {
        return Optional.ofNullable(this.currentRecipe);
    }

    @Override
    public void clearRecipe() {
        this.currentRecipe = null;
    }

    @Override
    public void completeCraft() {
        this.currentRecipe = null;
        this.craftingState = EnumCraftingState.NOT_MATCHED;
    }

    @Override
    public EnumCraftingState getCraftingState() {
        return craftingState;
    }

    @Override
    public void tick(World level) {
        tickCrafting(level);
    }

    public void tickCrafting(World level) {
        AxisAlignedBB fieldBounds = getBounds();

        // Get out, client worlds
        if (level == null || level.isClientSide())
            return;

        if(this.currentRecipe == null)
            return;

        // We grow the bounds check here a little to support patterns that are exactly the size of the field
        List<ItemEntity> catalystEntities = getCatalystsInField(level, fieldBounds.inflate(0.25), currentRecipe.getCatalyst().getItem());
        if (catalystEntities.size() > 0) {
            // We dropped a catalyst item in
            // At this point, we had a valid recipe and a valid catalyst entity
            // Start crafting
            switch (craftingState) {
                case MATCHED:
                    this.craftingState = EnumCraftingState.CRAFTING;

                    // We know the "recipe" in the field is an exact match already, so wipe the field
                    clearBlocks(level);

                    CraftingHelper.consumeCatalystItem(catalystEntities.get(0), 1);

                    BlockPos centerField = getCenterPosition();
                    level.setBlockAndUpdate(centerField, Registration.FIELD_CRAFTING_PREVIEW_BLOCK.get().defaultBlockState());

                    // TODO - Expose this as a LazyOptional somehow
                    FieldCraftingPreviewTile tile = (FieldCraftingPreviewTile) level.getBlockEntity(centerField);
                    if (tile != null)
                        tile.setField(this);

                    break;

                case CRAFTING:
                    break;
            }
        }
    }

    /**
     * Scans the field and attempts to match a recipe that's placed in it.
     */
    public void doRecipeScan(World level) {
        if (level == null)
            return;

        Stream<BlockPos> filledBlocks = getFilledBlocks(level);

        // If no positions filled, exit early
        if (filledBlocks.count() == 0) {
            clearRecipe();
            return;
        }

        // ===========================================================================================================
        //   RECIPE BEGIN
        // ===========================================================================================================

        /*
         * Dry run - we have the data from the field on what's filled and how large
         * the area is. Run through the recipe list and filter based on that, so
         * we remove all the recipes that are definitely larger than the currently
         * filled space.
         */
        Set<MiniaturizationRecipe> recipes = level.getRecipeManager()
                .getAllRecipesFor(Registration.MINIATURIZATION_RECIPE_TYPE)
                .stream().map(r -> (MiniaturizationRecipe) r)
                .filter(recipe -> recipe.fitsInDimensions(getFilledBounds(level)))
                .collect(Collectors.toSet());

        /*
         * All the recipes we have registered won't fit in the filled bounds -
         * blocks were placed in a larger space than the max recipe size
         */
        CompactCrafting.LOGGER.trace("Matched a total of {} possible recipes.", recipes.size());
        if (recipes.isEmpty()) {
            clearRecipe();
            return;
        }

        // Begin recipe dry run - loop, check bottom layer for matches
        MiniaturizationRecipe matchedRecipe = null;
        for (MiniaturizationRecipe recipe : recipes) {
            boolean recipeMatches = recipe.matches(level, this);
            if (!recipeMatches)
                continue;

            matchedRecipe = recipe;
            this.craftingState = EnumCraftingState.MATCHED;
            break;
        }

        this.currentRecipe = matchedRecipe;
    }

    @Override
    public void setCraftingState(EnumCraftingState state) {
        this.craftingState = state;
    }

    private List<ItemEntity> getCatalystsInField(IWorld level, AxisAlignedBB fieldBounds, Item itemFilter) {
        List<ItemEntity> itemsInRange = level.getEntitiesOfClass(ItemEntity.class, fieldBounds);
        return itemsInRange.stream()
                .filter(ise -> ise.getItem().getItem() == itemFilter)
                .collect(Collectors.toList());
    }

    private boolean hasCatalystInBounds(IWorld level, AxisAlignedBB bounds, Item itemFilter) {
        try {
            List<ItemEntity> itemsInRange = getCatalystsInField(level, bounds, itemFilter);
            int matchedCatalysts = collectItems(itemsInRange);

            return matchedCatalysts > 0;
        } catch (NullPointerException npe) {

        }

        return false;
    }

    private int collectItems(List<ItemEntity> itemsInRange) {
        return itemsInRange.stream()
                .map(ItemEntity::getItem)
                .map(ItemStack::getCount)
                .mapToInt(Integer::intValue)
                .sum();
    }
}
