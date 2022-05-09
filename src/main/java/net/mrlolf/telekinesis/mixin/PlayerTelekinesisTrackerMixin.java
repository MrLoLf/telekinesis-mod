package net.mrlolf.telekinesis.mixin;

import net.mrlolf.telekinesis.interfaces.PlayerTelekinesisTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntity.class)
public abstract class PlayerTelekinesisTrackerMixin implements PlayerTelekinesisTracker {
    @Unique
    private BlockPos lastTelekinesisBreakPos;

    @Unique
    private long lastTelekinesisBreakTime;

    public void setLastTelekinesisBreak(BlockPos pos, long tick) {
        lastTelekinesisBreakPos = pos;
        lastTelekinesisBreakTime = tick;
    }

    public BlockPos getLastTelekinesisBlockPos() {
        return lastTelekinesisBreakPos;
    }

    public boolean checkTelekinesisTick(long tick) {
        return lastTelekinesisBreakTime >= tick - 1 && lastTelekinesisBreakTime <= tick + 1;
    }
}
