package ru.wirelesstools.container;

import ic2.core.ContainerFullInv;
import net.minecraft.entity.player.EntityPlayer;
import ru.wirelesstools.tileentities.othertes.TileWirelessChargepad;

import java.util.List;

public class ContainerWirelessChargepad extends ContainerFullInv<TileWirelessChargepad>  {
    public ContainerWirelessChargepad(EntityPlayer player, TileWirelessChargepad base) {
        super(player, base, 196);
    }
    
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("radius");
        ret.add("mode");
        ret.add("isOn");
        return ret;
    }
}
