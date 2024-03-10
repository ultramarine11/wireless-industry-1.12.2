package ru.wirelesstools.tileentities.othertes;

import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.block.invslot.InvSlotProcessableSmelting;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.wirelesstools.config.ConfigWI;
import ru.wirelesstools.container.ContainerSolarFurnace;
import ru.wirelesstools.gui.GuiSolarFurnace;

public class TileSolarFurnace extends TileStandartSolarMachine<ItemStack, ItemStack, ItemStack> {
    
    public TileSolarFurnace() {
        super(2, 90, 1, ConfigWI.dayPowerSolarFurnace, ConfigWI.nightPowerSolarFurnace);
        this.inputSlot = new InvSlotProcessableSmelting(this, "input", 1);
    }
    
    @SideOnly(Side.CLIENT)
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GuiSolarFurnace(new ContainerSolarFurnace(player, this));
    }
    
    @Override
    public ContainerBase<TileSolarFurnace> getGuiContainer(EntityPlayer player) {
        return new ContainerSolarFurnace(player, this);
    }
    
    protected void onUnloaded() {
        super.onUnloaded();
        if(IC2.platform.isRendering()) {
            if(this.startingSound != null) {
                if(!this.startingSound.isComplete()) {
                    this.startingSound.cancel();
                }
                this.startingSound = null;
            }
            
            if(this.finishingSound != null) {
                IC2.audioManager.removeSource(this.finishingSound);
                this.finishingSound = null;
            }
        }
    }
    
    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
    }
    
    public String getStartSoundFile() {
        return "Machines/Electro Furnace/ElectroFurnaceStart.ogg";
    }
    
    public String getLoopSoundFile() {
        return "Machines/Electro Furnace/ElectroFurnaceLoop.ogg";
    }
    
    public String getInterruptSoundFile() {
        return "Machines/Electro Furnace/ElectroFurnaceStop.ogg";
    }
    
}
