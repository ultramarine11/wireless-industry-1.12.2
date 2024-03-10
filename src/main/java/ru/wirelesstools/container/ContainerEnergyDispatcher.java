package ru.wirelesstools.container;

import ic2.core.ContainerFullInv;
import net.minecraft.entity.player.EntityPlayer;
import ru.wirelesstools.tileentities.wireless.TileEnergyAutoDispatcher;

import java.util.List;

public class ContainerEnergyDispatcher extends ContainerFullInv<TileEnergyAutoDispatcher> {
    
    public ContainerEnergyDispatcher(EntityPlayer player, TileEnergyAutoDispatcher base) {
        super(player, base, 196);
    }
    
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("transmitValue");
        ret.add("evenlyDistribution");
        ret.add("sentSingleEnergyPacket");
        ret.add("isOn");
        ret.add("energyTilesQuantity");
        return ret;
    }
}
