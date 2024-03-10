package ru.wirelesstools.container;

import ic2.core.ContainerFullInv;
import ic2.core.slot.SlotInvSlot;
import net.minecraft.entity.player.EntityPlayer;
import ru.wirelesstools.tileentities.othertes.TileSolarFurnace;

import java.util.List;

public class ContainerSolarFurnace extends ContainerFullInv<TileSolarFurnace> {
    
    public ContainerSolarFurnace(EntityPlayer player, TileSolarFurnace base) {
        super(player, base, 166);
        this.addSlotToContainer(new SlotInvSlot(base.inputSlot, 0, 56, 17));
        this.addSlotToContainer(new SlotInvSlot(base.outputSlot, 0, 116, 35));
    }
    
    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("guiProgress");
        ret.add("activeMode");
        return ret;
    }
}
