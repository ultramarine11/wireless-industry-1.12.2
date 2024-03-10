package ru.wirelesstools.container;

import ic2.core.ContainerFullInv;
import net.minecraft.entity.player.EntityPlayer;
import ru.wirelesstools.tileentities.wireless.receivers.TileEntityWSBPersonal;

import java.util.List;

public class ContainerWSPersonal extends ContainerFullInv<TileEntityWSBPersonal> {
    
    public ContainerWSPersonal(EntityPlayer player, TileEntityWSBPersonal base) {
        super(player, base, 196);
    }
    
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("owner");
        ret.add("channel");
        return ret;
    }
    
}
