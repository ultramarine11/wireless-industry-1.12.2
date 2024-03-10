package ru.wirelesstools.api;

import ic2.core.block.TileEntityBlock;
import net.minecraft.tileentity.TileEntity;

public interface IEnergyTransfer {
    
    void transferEnergyTEMC(TileEntity teTarget, TileEntityBlock teSource);
}
