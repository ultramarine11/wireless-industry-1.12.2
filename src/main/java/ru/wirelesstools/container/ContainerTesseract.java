package ru.wirelesstools.container;

import ic2.core.ContainerFullInv;
import net.minecraft.entity.player.EntityPlayer;
import ru.wirelesstools.tileentities.wireless.TileTesseract;

import java.util.List;

public class ContainerTesseract extends ContainerFullInv<TileTesseract> {
    
    public ContainerTesseract(EntityPlayer player, TileTesseract base) {
        super(player, base, 207);
    }
    
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("owner");
        ret.add("channelEnergy");
        ret.add("sendEnergy");
        ret.add("wirelessTransferAmount");
        ret.add("channelFluid_1");
        ret.add("channelFluid_2");
        ret.add("channelFluid_3");
        ret.add("sendFluid_1");
        ret.add("sendFluid_2");
        ret.add("sendFluid_3");
        ret.add("fluidTank_1");
        ret.add("fluidTank_2");
        ret.add("fluidTank_3");
        
        return ret;
    }
}
