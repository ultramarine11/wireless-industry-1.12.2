package ru.wirelesstools.container;

import ic2.core.ContainerFullInv;
import net.minecraft.entity.player.EntityPlayer;
import ru.wirelesstools.tileentities.othertes.TileEntityMachinesChargerBase;
import ru.wirelesstools.tileentities.othertes.TileEntitySolarMachinesCharger;

import java.util.List;

public class ContainerSolarWirelessMachinesCharger extends ContainerFullInv<TileEntityMachinesChargerBase> {
    
    public ContainerSolarWirelessMachinesCharger(EntityPlayer player, TileEntitySolarMachinesCharger base) {
        super(player, base, 196);
    }
    
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("activeState");
        ret.add("radius");
        ret.add("chargeRate");
        ret.add("mode");
        ret.add("energyTilesQuantity");
        ret.add("isOn");
        ret.add("isAsymmetricCharging");
        ret.add("offsetXZ");
        ret.add("offsetY");
        return ret;
    }
}
