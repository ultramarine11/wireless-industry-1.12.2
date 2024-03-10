package cofh.redstoneflux.api;

import net.minecraft.util.EnumFacing;

public interface IEnergyProvider extends IEnergyHandler {
    int extractEnergy(EnumFacing side, int maxExtract, boolean simulate);
}
