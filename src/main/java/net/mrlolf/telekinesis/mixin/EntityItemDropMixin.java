package net.mrlolf.telekinesis.mixin;

import net.mrlolf.telekinesis.event.EntityItemDropEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LivingEntity.class)
public abstract class EntityItemDropMixin extends Entity {
    @Inject(method = "dropLoot", at = @At(value = "INVOKE", target = "Lnet/minecraft/loot/LootTable;generateLoot(Lnet/minecraft/loot/context/LootContext;Ljava/util/function/Consumer;)V"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void generateEntityItemDropEvent(DamageSource source, boolean causedByPlayer, CallbackInfo ci, Identifier identifier, LootTable lootTable, LootContext.Builder lootContext) {
        boolean shouldDrop = EntityItemDropEvent.EVENT.invoker().onEntityItemDrop(source, causedByPlayer, lootTable, lootContext, super.world, super::dropStack);
        if (!shouldDrop) {
            ci.cancel();
        }
    }

    public EntityItemDropMixin(EntityType<?> type, World world) {
        super(type, world);
    }
}
