package ru.wirelesstools.container;

import ic2.core.ContainerFullInv;
import net.minecraft.entity.player.EntityPlayer;
import ru.wirelesstools.tileentities.othertes.TileEntityMachinesChargerBase;

import java.util.List;

public class ContainerWirelessMachinesCharger extends ContainerFullInv<TileEntityMachinesChargerBase> {
    
    public ContainerWirelessMachinesCharger(EntityPlayer player, TileEntityMachinesChargerBase base) {
        super(player, base, 196);
    }
    
    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
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
