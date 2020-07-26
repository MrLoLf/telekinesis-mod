package com.mymindstorm.telekinesis.mixin;

import com.mymindstorm.telekinesis.BlockItemDropEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.List;

import static net.minecraft.block.Block.dropStack;
import static net.minecraft.block.Block.getDroppedStacks;

@Mixin(Block.class)
public class BlockItemDropEventMixin {
    /**
     * I can't inject code inside of a lambda for some reason, so I just rewrote it here instead.
     * @author mymindstorm
     */
    @Overwrite
    public static void dropStacks(BlockState state, World world, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack stack) {
        if (world instanceof ServerWorld) {
            List<ItemStack> itemStackList = getDroppedStacks(state, (ServerWorld)world, pos, blockEntity, entity, stack);
            boolean shouldDrop = BlockItemDropEvent.EVENT.invoker().onBlockItemDrop(state, world, pos, blockEntity, entity, stack, itemStackList);
            if (shouldDrop) {
                itemStackList.forEach(itemStack -> dropStack(world, pos, itemStack));
            }
        }

        state.onStacksDropped(world, pos, stack);
    }

}
