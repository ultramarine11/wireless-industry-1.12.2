package ru.wirelesstools.container;

import ic2.core.ContainerFullInv;
import net.minecraft.entity.player.EntityPlayer;
import ru.wirelesstools.tileentities.wireless.TileEUPoint;

import java.util.List;

public class ContainerEuPoint extends ContainerFullInv<TileEUPoint> {
    public ContainerEuPoint(EntityPlayer player, TileEUPoint base) {
        super(player, base, 166);
    }
    
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("owner");
        ret.add("channel");
        ret.add("sendMode");
        ret.add("modePublic");
        ret.add("wirelessTransferLimit");
        return ret;
    }
}
