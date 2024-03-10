package ru.wirelesstools.slot;

import ic2.core.block.IInventorySlotHolder;
import ic2.core.block.invslot.InvSlot;
import net.minecraft.item.ItemStack;
import ru.wirelesstools.items.tools.ItemPlayerModule;

public class InvSlotPlayerModule extends InvSlot {
    
    public InvSlotPlayerModule(IInventorySlotHolder<?> base, int count) {
        super(base, "moduleslot", InvSlot.Access.NONE, count, InvSide.ANY);
        this.setStackSizeLimit(1);
    }
    
    @Override
    public boolean accepts(ItemStack stack) {
        return stack.getItem() instanceof ItemPlayerModule;
    }
    
}
