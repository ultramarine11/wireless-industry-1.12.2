package cofh.redstoneflux.api;

import net.minecraft.util.EnumFacing;

public interface IEnergyHandler extends IEnergyConnection {
    int getEnergyStored(EnumFacing side);
    
    int getMaxEnergyStored(EnumFacing side);
}
