package ru.wirelesstools.container;

import ic2.core.ContainerFullInv;
import ic2.core.slot.SlotInvSlot;
import net.minecraft.entity.player.EntityPlayer;
import ru.wirelesstools.tileentities.othertes.TileVacuumPlayerChest;

import java.util.List;

public class ContainerVacuumPlayerChest extends ContainerFullInv<TileVacuumPlayerChest> {
    
    public ContainerVacuumPlayerChest(EntityPlayer player, TileVacuumPlayerChest base) {
        super(player, base, 228);
        for(int l = 0; l < base.contentSlot.size() / 9; l++) {
            for(int c = 0; c < base.contentSlot.size() / 3; c++) {
                this.addSlotToContainer(new SlotInvSlot(base.contentSlot, l * 9 + c, 8 + c * 18, 22 + l * 18));
            }
            this.addSlotToContainer(new SlotInvSlot(base.modulePlayerSlot, 0, 80, 78));
        }
    }
    
    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("isTurnedOn");
        ret.add("collectradius");
        ret.add("owner");
        return ret;
    }
    
}
