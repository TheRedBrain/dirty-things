package com.github.theredbrain.dirtyThings.mixin;

import com.github.theredbrain.dirtyThings.DirtyThings;
import com.github.theredbrain.dirtyThings.block.CustomSlabBlock;
import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Random;

@Mixin(GrassBlock.class)
public class GrassBlockMixin {

    private static boolean canSurvive(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.up();
        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.isOf(Blocks.SNOW) && (Integer)blockState.get(SnowBlock.LAYERS) == 1) {
            return true;
        } else if (blockState.getFluidState().getLevel() == 8) {
            return false;
        } else if ((state.isOf(DirtyThings.DIRT_SLAB) || state.isOf(DirtyThings.GRASS_SLAB)) && state.get(CustomSlabBlock.getWaterlogged())) {
            return false;
        } else {
            int i = ChunkLightProvider.getRealisticOpacity(world, state, pos, blockState, blockPos, Direction.UP, blockState.getOpacity(world, blockPos));
            return i < world.getMaxLightLevel();
        }
    }private static boolean canSpread(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.up();
        return canSurvive(state, world, pos) && !world.getFluidState(blockPos).isIn(FluidTags.WATER);
    }

    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!canSurvive(state, world, pos)) {
            world.setBlockState(pos, Blocks.DIRT.getDefaultState());
        } else {
            if (world.getLightLevel(pos.up()) >= 9) {
                BlockState blockState = Blocks.GRASS_BLOCK.getDefaultState();
                BlockState slabBlockState = DirtyThings.GRASS_SLAB.getDefaultState();

                for(int i = 0; i < 4; ++i) {
                    BlockPos blockPos = pos.add(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                    if (world.getBlockState(blockPos).isOf(Blocks.DIRT) && canSpread(blockState, world, blockPos)) {
                        world.setBlockState(blockPos, (BlockState)blockState.with(SnowyBlock.SNOWY, world.getBlockState(blockPos.up()).isOf(Blocks.SNOW)));
                    } else if (world.getBlockState(blockPos).isOf(DirtyThings.DIRT_SLAB) && !(world.getBlockState(blockPos).get(CustomSlabBlock.getWaterlogged())) && canSpread(slabBlockState, world, blockPos)) {
                        world.setBlockState(blockPos, (BlockState)slabBlockState.with(CustomSlabBlock.getType(), world.getBlockState(blockPos).get(CustomSlabBlock.getType())));
                    }
                }
            }

        }
    }
}
