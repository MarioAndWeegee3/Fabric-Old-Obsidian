package marioandweegee3.oldobsidian.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.fluid.BaseFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

@Mixin(FluidBlock.class)
public abstract class FluidBlockMixin extends Block implements FluidDrainable {
    @Shadow
    private BaseFluid fluid;

    @Shadow
    protected abstract void playExtinguishSound(IWorld world, BlockPos pos);

    public FluidBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "receiveNeighborFluids", at = @At("HEAD"), cancellable = true)
    public void onUpdate(World world, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> ci){
        if(this.fluid.matches(FluidTags.LAVA)){
            Direction[] horizontal = new Direction[]{
                Direction.NORTH,
                Direction.EAST,
                Direction.SOUTH,
                Direction.WEST
            };

            for(Direction dir : horizontal){
                BlockPos offset = pos.offset(Direction.DOWN);
                BlockState redState = world.getBlockState(offset);
                BlockPos waterPos = offset.offset(dir);
                FluidState fluidState2 = world.getFluidState(waterPos);
                if (fluidState2.matches(FluidTags.WATER) && redState.getBlock() == Blocks.REDSTONE_WIRE && redState.get(RedstoneWireBlock.POWER) == 0) {
                    world.setBlockState(offset, Blocks.OBSIDIAN.getDefaultState(), 2);
                    this.playExtinguishSound(world, offset);
                    ci.setReturnValue(false);
                    break;
                }
            }
        }
    }

}