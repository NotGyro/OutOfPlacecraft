package ink.echol.outofplacecraft.blocks;

import ink.echol.outofplacecraft.capabilities.SpeciesCapability;
import ink.echol.outofplacecraft.capabilities.SpeciesHelper;
import ink.echol.outofplacecraft.items.ItemRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class ClamSandBlock extends FallingBlock {
    protected static final int SPREAD_RADIUS = 8;
    protected static final int TOO_CLOSE = 4;
    protected static final int TOO_CLOSE_INNER = 3;
    protected static final int WATER_SEARCH_DIST = 3;
    public ClamSandBlock(Properties p_i48401_1_) {
        super(p_i48401_1_);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.randomTick(state, world, pos, random);
        int x = 0;
        int z = 0;
        while( (x < TOO_CLOSE) && (x > -TOO_CLOSE) ) {
            x = random.nextInt(SPREAD_RADIUS*2) -SPREAD_RADIUS;
        }
        while( (z < TOO_CLOSE) && (z > -TOO_CLOSE) ) {
            z = random.nextInt(SPREAD_RADIUS*2) -SPREAD_RADIUS;
        }
        BlockPos target_pos = pos.offset(x, 0, z);
        BlockState bstate = world.getBlockState(target_pos);
        if( ( bstate.isAir() ) || (bstate.getBlock() == Blocks.WATER) ) {
            // If this is air, is the block below this position sand? If so, we can use that one.
            BlockPos temp_pos = pos.below();
            BlockState tempbstate = world.getBlockState(temp_pos);
            if(tempbstate.getBlock() == Blocks.SAND) {
                target_pos = temp_pos;
                bstate = tempbstate;
            }
            else {
                //Not a valid position. Fail early.
                return;
            }
        }
        else {
            BlockPos above = target_pos.above();
            BlockState bstate_above = world.getBlockState(above);
            // If this doesn't have air above it, it's not workable. BUT, if it has SAND above it,
            // and there's air above THAT, we can work with that.
            if(bstate_above.getBlock() == Blocks.SAND) {
                BlockState aboveabove = world.getBlockState(above.above());
                if((aboveabove.isAir() ) || (aboveabove.getBlock() == Blocks.WATER)) {
                    target_pos = above;
                    bstate = bstate_above;
                }
                else {
                    //Not a valid position. Fail early.
                    return;
                }
            }
        }
        //After adjusting our Y-level a bit, are we looking at sand?
        if(bstate.getBlock() == Blocks.SAND) {
            //if so, continue.
            //Check to see if we're way too close to another clamsand.
            boolean water = false;

            for(int ix = -WATER_SEARCH_DIST; ix <= WATER_SEARCH_DIST; ++ix ) {
                for(int iy = -1; iy <= 1; ++iy ) {
                    for(int iz = -WATER_SEARCH_DIST; iz <= WATER_SEARCH_DIST; ++iz ) {
                        BlockState surround = world.getBlockState(target_pos.offset(ix,iy,iz));
                        if(surround.getBlock() == Blocks.WATER) {
                            water = true;
                        }
                        else if(surround.getBlock() == BlockRegistry.CLAM_SAND_BLOCK.get()) {
                            if((ix < TOO_CLOSE_INNER) && (ix > -TOO_CLOSE_INNER)) {
                                if((iz < TOO_CLOSE_INNER) && (iz > -TOO_CLOSE_INNER)) {
                                    // Competition! No thanks.
                                    return;
                                }
                            }
                        }
                    }
                }
            }

            //If our conditions succeed
            if( water ) {
                //Congrats! Clam reproduction.
                world.setBlock(target_pos, BlockRegistry.CLAM_SAND_BLOCK.get().defaultBlockState(), 3);
            }
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState p_149653_1_) {
        return true;
    }

    @Override
    public ActionResultType use(BlockState p_225533_1_, World world, BlockPos pos, PlayerEntity player, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
        if(SpeciesHelper.getPlayerSpecies(player) == SpeciesCapability.YINGLET_ID) {
            ItemEntity ety = new ItemEntity(world, ((double) pos.getX()) + 0.5d, ((double) pos.getY()) +1.0d, ((double) pos.getZ()) + 0.5d, new ItemStack(ItemRegistry.CLAM.get()));
            ety.setDefaultPickUpDelay();
            world.addFreshEntity(ety);
            world.setBlock(pos, Blocks.SAND.defaultBlockState(), 3);
            return ActionResultType.SUCCESS;
        }
        return super.use(p_225533_1_, world, pos, player, p_225533_5_, p_225533_6_);
    }
}
