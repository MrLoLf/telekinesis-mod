package net.mrlolf.telekinesis;

import net.mrlolf.telekinesis.event.BlockItemDropEvent;
import net.mrlolf.telekinesis.event.EntityItemDropEvent;
import net.mrlolf.telekinesis.interfaces.PlayerTelekinesisTracker;
import net.fabricmc.api.ModInitializer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.Registry;

import java.util.List;

import static net.minecraft.block.Block.dropStack;

public class TelekinesisMod implements ModInitializer {
	private static final Enchantment ENCHANTMENT_TELEKINESIS = Registry.register(
			Registry.ENCHANTMENT,
			new Identifier("telekinesis", "telekinesis"),
			new TelekinesisEnchantment()
	);

	@Override
	public void onInitialize() {
		BlockItemDropEvent.EVENT.register((state, world, pos, blockEntity, entity, stack, drops) -> {
			// Tall block logic
			if (entity == null) {
				// Get all players that have enchantment in 5 block radius
				List<PlayerEntity> players = world.getEntitiesByClass(PlayerEntity.class, new Box(pos.add(-5, -32, -5), pos.add(5, 32, 5)),
						playerEntity -> EnchantmentHelper.getLevel(ENCHANTMENT_TELEKINESIS, playerEntity.getMainHandStack()) > 0);
				if (players.isEmpty()) {
					return true;
				} else {
					// Find player that broke the block
					PlayerEntity player = null;
					for (PlayerEntity playerEntity : players) {
						// Is the block break within one tick and one block of this player's last telekinesis block break?
						if (((PlayerTelekinesisTracker)playerEntity).checkTelekinesisTick(world.getTime())
							&& pos.isWithinDistance(((PlayerTelekinesisTracker)playerEntity).getLastTelekinesisBlockPos(), 2)) {
							player = playerEntity;
							break;
						}
					}
					if (player == null) {
						return true;
					}
					// Insert drops into inventory
					PlayerEntity finalPlayer = player;
					drops.forEach(itemStack -> {
						if (!finalPlayer.getInventory().insertStack(itemStack)) {
							dropStack(world, pos, itemStack);
						}
					});
					((PlayerTelekinesisTracker) player).setLastTelekinesisBreak(pos, world.getTime());
					return false;
				}
			}

			// Normal player block break
			if (EnchantmentHelper.getLevel(ENCHANTMENT_TELEKINESIS, stack) > 0) {
				// get player object
				PlayerEntity player = world.getPlayerByUuid(entity.getUuid());
				if (player == null) {
					return true;
				}

				// insert drops into inventory
				drops.forEach(itemStack -> {
					if (!player.getInventory().insertStack(itemStack)) {
						dropStack(world, pos, itemStack);
					}
				});

				((PlayerTelekinesisTracker) player).setLastTelekinesisBreak(pos, world.getTime());
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
						if (!player.getInventory().insertStack(itemStack)) {
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
