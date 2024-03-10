package ru.wirelesstools.tileentities.othertes.transferhandler;

import com.denfop.componets.AdvEnergy;
import com.denfop.tiles.base.TileEntityInventory;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.comp.Energy;
import net.minecraft.tileentity.TileEntity;
import ru.wirelesstools.api.IEnergyTransfer;
import ru.wirelesstools.tileentities.othertes.TileEntityMachinesChargerBase;
import ru.wirelesstools.tileentities.wireless.IChargerDispatcherTile;

public class EnergyTransferIU implements IEnergyTransfer {
    
    @Override
    public void transferEnergyTEMC(TileEntity teTarget, TileEntityBlock teSource) {
        if(teSource instanceof TileEntityMachinesChargerBase) {
            TileEntityMachinesChargerBase teMChIU = (TileEntityMachinesChargerBase)teSource;
            if(teTarget instanceof TileEntityBlock && !(teTarget instanceof IChargerDispatcherTile)) {
                TileEntityBlock teBlock = (TileEntityBlock)teTarget;
                if(teBlock instanceof TileEntityInventory) {
                    TileEntityInventory teInv = (TileEntityInventory)teBlock;
                    if(teInv.hasComp(AdvEnergy.class)) {
                        AdvEnergy advEnergy = teInv.getComp(AdvEnergy.class);
                        if(!advEnergy.getSinkDirs().isEmpty() && advEnergy.getSourceDirs().isEmpty()) {
                            teMChIU.incrementTEQuantity();
                            double freeEnergy = advEnergy.getFreeEnergy();
                            if(freeEnergy > 0.0) {
                                teMChIU.getEnergyReference().useEnergy(advEnergy.addEnergy(Math.min(teMChIU.getEnergyReference().getEnergy(),
                                        teMChIU.getMode() == 8 ? freeEnergy : teMChIU.getChargeRate())));
                            }
                        }
                    }
                }
                else if(teBlock.hasComponent(Energy.class)) {
                    Energy energy = teBlock.getComponent(Energy.class);
                    if(!energy.getSinkDirs().isEmpty() && energy.getSourceDirs().isEmpty() && !energy.isMultiSource()) {
                        teMChIU.incrementTEQuantity();
                        double freeEnergy = energy.getFreeEnergy();
                        if(freeEnergy > 0.0) {
                            teMChIU.getEnergyReference().useEnergy(energy.addEnergy(Math.min(teMChIU.getEnergyReference().getEnergy(),
                                    teMChIU.getMode() == 8 ? freeEnergy : teMChIU.getChargeRate())));
                        }
                    }
                }
            }
        }
    }
}
