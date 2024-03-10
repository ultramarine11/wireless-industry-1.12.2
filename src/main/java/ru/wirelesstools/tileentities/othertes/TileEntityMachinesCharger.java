package ru.wirelesstools.tileentities.othertes;

import ic2.core.ContainerBase;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.wirelesstools.config.ConfigWI;
import ru.wirelesstools.container.ContainerWirelessMachinesCharger;
import ru.wirelesstools.gui.GUIWirelessMachinesCharger;

public class TileEntityMachinesCharger extends TileEntityMachinesChargerBase {
    
    public TileEntityMachinesCharger() {
        super(ConfigWI.maxStorageMachinesCharger, ConfigWI.machinesChargerTier);
    }
    
    @SideOnly(Side.CLIENT)
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GUIWirelessMachinesCharger(new ContainerWirelessMachinesCharger(player, this));
    }
    
    @Override
    public ContainerBase<TileEntityMachinesChargerBase> getGuiContainer(EntityPlayer player) {
        return new ContainerWirelessMachinesCharger(player, this);
    }
    
}
