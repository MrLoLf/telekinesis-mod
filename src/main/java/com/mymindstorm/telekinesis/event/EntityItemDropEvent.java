package com.mymindstorm.telekinesis.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.world.World;

public interface EntityItemDropEvent {
    Event<EntityItemDropEvent> EVENT = EventFactory.createArrayBacked(
            EntityItemDropEvent.class,
            (listeners) -> (source, causedByPlayer, lootTable, lootContext, world, dropStack) -> {
                for (EntityItemDropEvent callback : listeners) {
                    boolean result = callback.onEntityItemDrop(source, causedByPlayer, lootTable, lootContext, world, dropStack);

                    if (!result) {
                        return false;
                    }
                }
                return true;
            }
    );

    /**
     * @param lootContext constructed loot table
     * @return false if normal item drop should not happen
     * */
    boolean onEntityItemDrop(DamageSource source, boolean causedByPlayer, LootTable lootTable, LootContext.Builder lootContext, World world, EntityDropStack dropStack);
}
