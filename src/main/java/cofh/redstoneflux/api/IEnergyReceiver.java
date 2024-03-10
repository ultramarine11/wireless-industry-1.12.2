package cofh.redstoneflux.api;

import net.minecraft.util.EnumFacing;

public interface IEnergyReceiver extends IEnergyHandler {
    int receiveEnergy(EnumFacing side, int maxReceive, boolean simulate);
}
