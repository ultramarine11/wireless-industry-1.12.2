package ru.wirelesstools.container;

import ic2.core.ContainerFullInv;
import net.minecraft.entity.player.EntityPlayer;
import ru.wirelesstools.tileentities.wireless.panels.TileEntityWPBase;

import java.util.List;

public class ContainerWPP extends ContainerFullInv<TileEntityWPBase> {
    
    public ContainerWPP(EntityPlayer player, TileEntityWPBase base) {
        super(player, base, 168);
    }
    
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("owner");
        ret.add("statePower");
        ret.add("channel");
        ret.add("active");
        return ret;
    }
}
