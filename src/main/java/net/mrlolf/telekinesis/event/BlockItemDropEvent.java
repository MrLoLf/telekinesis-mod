package net.mrlolf.telekinesis.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

@FunctionalInterface
public interface BlockItemDropEvent {
    Event<BlockItemDropEvent> EVENT = EventFactory.createArrayBacked(
            BlockItemDropEvent.class,
            (listeners) -> (state, world, pos, blockEntity, entity, stack, drops) -> {
                for (BlockItemDropEvent callback : listeners) {
                    boolean result = callback.onBlockItemDrop(state, world, pos, blockEntity, entity, stack, drops);

                    if (!result) {
                        return false;
                    }
                }
                return true;
            }
    );

    /**
     * @param state state of broken block
     * @param pos position of broken block
     * @param entity entity that broke block
     * @param stack item used by entity that broke block
     * @param drops calculated drops for broken block
     * @return false if normal item drop should not happen
     * */
    boolean onBlockItemDrop(BlockState state, World world, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack stack, List<ItemStack> drops);
}