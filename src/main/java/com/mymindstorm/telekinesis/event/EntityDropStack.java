package com.mymindstorm.telekinesis.event;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;

public interface EntityDropStack {
    ItemEntity dropStack(ItemStack stack);
}
