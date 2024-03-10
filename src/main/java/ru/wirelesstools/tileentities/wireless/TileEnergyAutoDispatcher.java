package ru.wirelesstools.tileentities.wireless;

import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.core.ContainerBase;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.comp.Energy;
import ic2.core.init.Localization;
import ic2.core.ref.TeBlock;
import ic2.core.util.StackUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.wirelesstools.config.ConfigWI;
import ru.wirelesstools.container.ContainerEnergyDispatcher;
import ru.wirelesstools.gui.GuiEnergyDispatcher;

import java.util.List;
import java.util.stream.Collectors;

public class TileEnergyAutoDispatcher extends TileEntityInventory implements IHasGui,
        INetworkClientTileEntityEventListener, IChargerDispatcherTile {
    
    protected final Energy energy;
    protected double transmitValue;
    protected boolean evenlyDistribution;
    protected double sentSingleEnergyPacket;
    protected boolean isOn;
    protected int energyTilesQuantity;
    
    public TileEnergyAutoDispatcher() {
        this.energy = this.addComponent(Energy.asBasicSink(this, ConfigWI.maxStorageEnergyDispatcher, ConfigWI.energyDispatcherTier));
        this.transmitValue = 1024.0;
        this.evenlyDistribution = false;
        this.sentSingleEnergyPacket = 0;
        this.isOn = true;
        this.energyTilesQuantity = 0;
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, List<String> info, ITooltipFlag flag) {
        super.addInformation(stack, info, flag);
        info.add(Localization.translate("ic2.item.tooltip.Store") + " "
                + (long)StackUtil.getOrCreateNbtData(stack).getDouble("energy") + " "
                + Localization.translate("ic2.generic.text.EU"));
    }
    
    protected ItemStack adjustDrop(ItemStack drop, boolean wrench) {
        drop = super.adjustDrop(drop, wrench);
        if(wrench || this.teBlock.getDefaultDrop() == TeBlock.DefaultDrop.Self) {
            StackUtil.getOrCreateNbtData(drop).setDouble("energy", this.energy.getEnergy());
        }
        return drop;
    }
    
    public void onPlaced(ItemStack stack, EntityLivingBase placer, EnumFacing facing) {
        super.onPlaced(stack, placer, facing);
        if(!this.world.isRemote) {
            this.energy.addEnergy(StackUtil.getOrCreateNbtData(stack).getDouble("energy"));
        }
    }
    
    private void changeTransmitValue(boolean increment) {
        double newVal;
        if(increment) {
            newVal = this.transmitValue * 2;
            if(newVal > 524288.0)
                newVal = 1024.0;
        }
        else {
            newVal = this.transmitValue / 2;
            if(newVal < 1024.0)
                newVal = 524288.0;
        }
        this.transmitValue = newVal;
    }
    
    protected void toggleWork() {
        this.isOn = !this.isOn;
    }
    
    protected void toggleDistributionMode() {
        this.evenlyDistribution = !this.evenlyDistribution;
    }
    
    protected void updateEntityServer() {
        super.updateEntityServer();
        if(this.isOn)
            this.searchTilesInChunk();
    }
    
    protected void searchTilesInChunk() {
        List<Energy> energyList = this.world.getChunkFromBlockCoords(this.pos).tileEntities.values()
                .stream()
                .filter(tile -> tile instanceof TileEntityBlock && !(tile instanceof IChargerDispatcherTile))
                .map(tile -> (TileEntityBlock)tile)
                .filter(tile -> tile.hasComponent(Energy.class))
                .map(tile -> tile.getComponent(Energy.class))
                .filter(energy1 -> !energy1.getSinkDirs().isEmpty() && energy1.getSourceDirs().isEmpty()
                        && !energy1.isMultiSource() && energy1.getFreeEnergy() > 0.0)
                .collect(Collectors.toList());
        this.energyTilesQuantity = 0;
        if(!energyList.isEmpty()) {
            this.sentSingleEnergyPacket = this.transmitValue / energyList.size();
            this.energyTilesQuantity = energyList.size();
            energyList.forEach(energy1 ->
                    this.energy.useEnergy(energy1.addEnergy(Math.min(this.evenlyDistribution ? this.sentSingleEnergyPacket : this.transmitValue,
                            this.energy.getEnergy()))));
        }
        else
            this.sentSingleEnergyPacket = 0;
    }
    
    public boolean getIsOn() {
        return this.isOn;
    }
    
    public boolean getIsEvenlyDistribution() {
        return this.evenlyDistribution;
    }
    
    public int getEnergyTilesQuantity() {
        return this.energyTilesQuantity;
    }
    
    public double getTransmitValue() {
        return this.transmitValue;
    }
    
    public double getSinglePacket() {
        return this.sentSingleEnergyPacket;
    }
    
    @Override
    public void onNetworkEvent(EntityPlayer entityPlayer, int id) {
        switch(id) {
            case 1:
                this.changeTransmitValue(true);
                break;
            case 2:
                this.changeTransmitValue(false);
                break;
            case 3:
                this.toggleWork();
                break;
            case 4:
                this.toggleDistributionMode();
                break;
        }
    }
    
    @Override
    public ContainerBase<TileEnergyAutoDispatcher> getGuiContainer(EntityPlayer player) {
        return new ContainerEnergyDispatcher(player, this);
    }
    
    @SideOnly(Side.CLIENT)
    public GuiScreen getGui(EntityPlayer player, boolean b) {
        return new GuiEnergyDispatcher(new ContainerEnergyDispatcher(player, this));
    }
    
    @Override
    public void onGuiClosed(EntityPlayer player) {
    }
    
}
