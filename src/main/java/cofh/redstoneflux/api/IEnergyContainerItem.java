package cofh.redstoneflux.api;

import net.minecraft.item.ItemStack;

public interface IEnergyContainerItem {
    int receiveEnergy(ItemStack stack, int amount, boolean simulate);
    
    int extractEnergy(ItemStack stack, int amount, boolean simulate);
    
    int getEnergyStored(ItemStack stack);
    
    int getMaxEnergyStored(ItemStack stack);
}
