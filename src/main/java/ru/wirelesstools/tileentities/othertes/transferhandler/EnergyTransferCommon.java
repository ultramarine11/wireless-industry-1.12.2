package ru.wirelesstools.tileentities.othertes.transferhandler;

import ic2.core.block.TileEntityBlock;
import ic2.core.block.comp.Energy;
import net.minecraft.tileentity.TileEntity;
import ru.wirelesstools.api.IEnergyTransfer;
import ru.wirelesstools.tileentities.othertes.TileEntityMachinesChargerBase;
import ru.wirelesstools.tileentities.wireless.IChargerDispatcherTile;

public class EnergyTransferCommon implements IEnergyTransfer {
    
    @Override
    public void transferEnergyTEMC(TileEntity teTarget, TileEntityBlock teSource) {
        if(teSource instanceof TileEntityMachinesChargerBase) {
            TileEntityMachinesChargerBase teMCh = (TileEntityMachinesChargerBase)teSource;
            if(teTarget instanceof TileEntityBlock && !(teTarget instanceof IChargerDispatcherTile)) {
                TileEntityBlock teBlock = (TileEntityBlock)teTarget;
                if(teBlock.hasComponent(Energy.class)) {
                    Energy energy = teBlock.getComponent(Energy.class);
                    if(!energy.getSinkDirs().isEmpty() && energy.getSourceDirs().isEmpty() && !energy.isMultiSource()) {
                        teMCh.incrementTEQuantity();
                        double freeEnergy = energy.getFreeEnergy();
                        if(freeEnergy > 0.0) {
                            teMCh.getEnergyReference().useEnergy(energy.addEnergy(Math.min(teMCh.getEnergyReference().getEnergy(),
                                    teMCh.getMode() == 8 ? freeEnergy : teMCh.getChargeRate())));
                        }
                    }
                }
            }
        }
    }
}
