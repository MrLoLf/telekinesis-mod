package com.mymindstorm.telekinesis.mixin;

import com.mymindstorm.telekinesis.event.BlockItemDropEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static net.minecraft.block.Block.getDroppedStacks;

@Mixin(Block.class)
public class BlockItemDropEventMixin {
    @Inject(at = @At("HEAD"), cancellable = true, method = "dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)V")
    private static void dropStacks(BlockState state, World world, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack stack, CallbackInfo ci) {
        if (world instanceof ServerWorld) {
            List<ItemStack> itemStackList = getDroppedStacks(state, (ServerWorld)world, pos, blockEntity, entity, stack);
            boolean shouldDrop = BlockItemDropEvent.EVENT.invoker().onBlockItemDrop(state, world, pos, blockEntity, entity, stack, itemStackList);
            if (!shouldDrop) {
                ci.cancel();
            }
        }
    }

}
