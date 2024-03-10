package ru.wirelesstools.container;

import ic2.core.ContainerFullInv;
import ic2.core.slot.SlotInvSlot;
import net.minecraft.entity.player.EntityPlayer;
import ru.wirelesstools.tileentities.othertes.TileWirelessChargerPlayer;

import java.util.List;

public class ContainerWChPlayer extends ContainerFullInv<TileWirelessChargerPlayer> {
    
    public ContainerWChPlayer(EntityPlayer player, TileWirelessChargerPlayer base) {
        super(player, base, 196);
    
        for(int i = 0; i < base.modulePlayerSlot.size(); i++)
            this.addSlotToContainer(new SlotInvSlot(base.modulePlayerSlot, i, 152, 56 + i * 18));
    }
    
    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("radius");
        ret.add("isSetPrivate");
        ret.add("owner");
        ret.add("energyRF");
        ret.add("autoConversionValueEuToRf");
        ret.add("autoConversionValueRfToEu");
        ret.add("conversionMode");
        ret.add("workingModeGP");
        return ret;
    }
    
}
