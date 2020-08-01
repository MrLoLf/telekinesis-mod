package com.mymindstorm.telekinesis;

import com.mymindstorm.telekinesis.event.BlockItemDropEvent;
import com.mymindstorm.telekinesis.event.EntityItemDropEvent;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.Arrays;

import static net.minecraft.block.Block.dropStack;

public class TelekinesisMod implements ModInitializer {
	private static Enchantment ENCHANTMENT_TELEKINESIS = Registry.register(
			Registry.ENCHANTMENT,
			new Identifier("telekinesis", "telekinesis"),
			new TelekinesisEnchantment()
	);

	// iterate and add to inventory until different block
	private void tallBlock(World world, BlockPos origBlock, Direction direction, Block blockid, PlayerEntity player) {
		BlockPos pos = origBlock.offset(direction);
		while (world.getBlockState(pos).getBlock().equals(blockid)) {
			ItemStack drop = new ItemStack(world.getBlockState(pos).getBlock());
			player.inventory.insertStack(drop);
			world.breakBlock(pos, false);
			pos = pos.offset(direction);
		}
	}

	@Override
	public void onInitialize() {
		BlockItemDropEvent.EVENT.register((state, world, pos, blockEntity, entity, stack, drops) -> {
			if (stack.hasEnchantments() && EnchantmentHelper.getLevel(ENCHANTMENT_TELEKINESIS, stack) > 0) {
				// get player object
				PlayerEntity player = world.getPlayerByUuid(entity.getUuid());
				if (player == null) {
					return true;
				}

				// TODO: add config file instead of hard coding
				Block[] BreakUp = {
						Blocks.SUGAR_CANE,
						Blocks.BAMBOO,
						Blocks.CACTUS
				};
				Block[] BreakDown = {
						Blocks.PEONY,
						Blocks.SUNFLOWER,
						Blocks.LILAC,
						Blocks.ROSE_BUSH
				};
				// check for tall blocks
				Block blockid = state.getBlock();
				if (Arrays.asList(BreakUp).contains(blockid)) {
					tallBlock(world, pos, Direction.UP, blockid, player);
				} else if (Arrays.asList(BreakDown).contains(blockid)) {
					tallBlock(world, pos, Direction.DOWN, blockid, player);
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
