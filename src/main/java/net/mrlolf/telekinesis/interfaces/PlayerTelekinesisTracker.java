package net.mrlolf.telekinesis.interfaces;

import net.minecraft.util.math.BlockPos;

public interface PlayerTelekinesisTracker {
    void setLastTelekinesisBreak(BlockPos pos, long tick);
    BlockPos getLastTelekinesisBlockPos();

    /**
     * Check if a telekinesis block break was made within the last tick.
     * @param tick Current tick
     */
    boolean checkTelekinesisTick(long tick);
}
