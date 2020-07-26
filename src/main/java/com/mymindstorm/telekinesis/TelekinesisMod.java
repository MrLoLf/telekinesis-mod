package com.mymindstorm.telekinesis;

import com.mymindstorm.telekinesis.event.BlockItemDropEvent;
import com.mymindstorm.telekinesis.event.EntityItemDropEvent;
import net.fabricmc.api.ModInitializer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static net.minecraft.block.Block.dropStack;

public class TelekinesisMod implements ModInitializer {
	private static Enchantment ENCHANTMENT_TELEKINESIS = Registry.register(
			Registry.ENCHANTMENT,
			new Identifier("telekinesis", "telekinesis"),
			new TelekinesisEnchantment()
	);

	@Override
	public void onInitialize() {
		BlockItemDropEvent.EVENT.register((state, world, pos, blockEntity, entity, stack, drops) -> {
			if (stack.hasEnchantments() && EnchantmentHelper.getLevel(ENCHANTMENT_TELEKINESIS, stack) > 0) {
				// get player object
				PlayerEntity player = world.getPlayerByUuid(entity.getUuid());
				if (player == null) {
					return true;
				}

				// insert drops into inventory
				drops.forEach(itemStack -> {
					if (!player.inventory.insertStack(itemStack)) {
						dropStack(world, pos, itemStack);
					}
				});

				return false;
			}
			return true;
		});

		EntityItemDropEvent.EVENT.register((source, causedByPlayer, lootTable, lootContext, world, entityDropStack) -> {
			if (causedByPlayer) {
				Entity attacker = source.getAttacker();
				if (attacker == null) {
					return true;
				}

				PlayerEntity player = world.getPlayerByUuid(attacker.getUuid());
				if (player == null) {
					return true;
				}

				// TODO: what if the weapon is in off hand?
				ItemStack playerWeapon = player.getMainHandStack();
				if (playerWeapon.hasEnchantments() && EnchantmentHelper.getLevel(ENCHANTMENT_TELEKINESIS, playerWeapon) > 0) {
					lootTable.generateLoot(lootContext.build(LootContextTypes.ENTITY)).forEach(itemStack -> {
						if (!player.inventory.insertStack(itemStack)) {
							entityDropStack.dropStack(itemStack);
						}
					});
					return false;
				}
			}
			return true;
		});
	}
}
