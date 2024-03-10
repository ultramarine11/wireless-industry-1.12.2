package ru.wirelesstools.container;

import ic2.core.ContainerFullInv;
import net.minecraft.entity.player.EntityPlayer;
import ru.wirelesstools.tileentities.xp.TileExperienceTransmitter;

import java.util.List;

public class ContainerXPTransmitter extends ContainerFullInv<TileExperienceTransmitter> {
    
    public ContainerXPTransmitter(EntityPlayer player, TileExperienceTransmitter base) {
        super(player, base, 166);
    }
    
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("sendingXPMode");
        ret.add("isOn");
        ret.add("amountXPTransmit");
        ret.add("storedXP");
        ret.add("playerNamesSet");
        ret.add("activeMode");
        return ret;
    }
    
}
