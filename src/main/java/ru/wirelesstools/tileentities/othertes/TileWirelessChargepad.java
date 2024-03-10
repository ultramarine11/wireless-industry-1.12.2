package ru.wirelesstools.tileentities.othertes;

import cofh.redstoneflux.api.IEnergyContainerItem;
import cofh.redstoneflux.api.IEnergyProvider;
import cofh.redstoneflux.api.IEnergyReceiver;
import ic2.api.item.IElectricItem;
import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.core.ContainerBase;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.comp.Energy;
import ic2.core.block.wiring.TileEntityElectricBlock;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.wirelesstools.container.ContainerWirelessChargepad;
import ru.wirelesstools.gui.GuiWirelessChargepad;
import ru.wirelesstools.tileentities.wireless.IChargerDispatcherTile;
import ru.wirelesstools.utils.Utilities;

import java.util.Collections;
import java.util.List;

public class TileWirelessChargepad extends TileEntityInventory implements IHasGui, INetworkClientTileEntityEventListener, IEnergyReceiver, IChargerDispatcherTile {
    protected final Energy energyEU;
    private EntityPlayer playerStandingOnTop;
    protected int radius;
    private Iterable<BlockPos.MutableBlockPos> poses;
    protected short mode;
    protected boolean isOn;
    
    public TileWirelessChargepad() {
        this.energyEU = this.addComponent(Energy.asBasicSink(this, 200000000.0, 10));
        this.playerStandingOnTop = null;
        this.radius = 5;
        this.isOn = true;
        this.mode = 0;
    }
    
    protected List<AxisAlignedBB> getAabbs(boolean forCollision) {
        return Collections.singletonList(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.25, 1.0));
    }
    
    protected void updateEntityServer() {
        super.updateEntityServer();
        if(this.isOn) {
            if(this.poses != null)
                this.consumeEnergyFromElectricBlocks();
        }
        if(this.playerStandingOnTop != null && this.energyEU.getEnergy() >= 1.0) {
            if(!this.getActive())
                this.setActive(true);
            
            this.chargeInventory(this.playerStandingOnTop);
            this.playerStandingOnTop = null;
        }
        else if(this.getActive()) {
            this.setActive(false);
        }
        
    }
    
    protected void onLoaded() {
        super.onLoaded();
        if(!this.world.isRemote) {
            this.poses = BlockPos.getAllInBoxMutable(
                    this.getPos().getX() - this.radius, this.getPos().getY() - this.radius, this.getPos().getZ() - this.radius,
                    this.getPos().getX() + this.radius, this.getPos().getY() + this.radius, this.getPos().getZ() + this.radius);
        }
    }
    
    protected void onUnloaded() {
        super.onUnloaded();
        if(!this.world.isRemote) {
            this.poses = null;
        }
    }
    
    private void consumeEnergyFromElectricBlocks() {
        for(BlockPos.MutableBlockPos pos : this.poses) {
            TileEntity te = this.world.getTileEntity(pos);
            switch(this.mode) {
                case 0:
                    this.consumeBothEnergies(te);
                    break;
                case 1:
                    this.consumeEUFromTile(te);
                    break;
                case 2:
                    this.consumeRFFromTile(te);
                    break;
            }
        }
    }
    
    private void consumeBothEnergies(TileEntity te) {
        this.consumeEUFromTile(te);
        this.consumeRFFromTile(te);
    }
    
    private void consumeEUFromTile(TileEntity te) {
        if(te instanceof TileEntityElectricBlock && !(te instanceof IChargerDispatcherTile)) {
            TileEntityElectricBlock teElectricBlock = (TileEntityElectricBlock)te;
            Energy energyComp = teElectricBlock.getComponent(Energy.class);
            double tileEnergy = energyComp.getEnergy();
            double freeEnergyChp = this.energyEU.getFreeEnergy();
            if(tileEnergy > 0.0 && freeEnergyChp > 0.0) {
                double useAmountEU = Math.min(freeEnergyChp, Math.min(tileEnergy, 16384.0));
                energyComp.useEnergy(useAmountEU);
                this.energyEU.addEnergy(useAmountEU);
            }
        }
    }
    
    private void consumeRFFromTile(TileEntity te) {
        if(te instanceof IEnergyProvider && !(te instanceof IChargerDispatcherTile)) {
            IEnergyProvider teRF = (IEnergyProvider)te;
            int energyRF = teRF.getEnergyStored(EnumFacing.UP);
            double freeEnergyEU = this.energyEU.getFreeEnergy();
            if(energyRF > 0 && freeEnergyEU > 0.0) {
                int useAmountRF = (int)Math.min(freeEnergyEU * 4, Math.min(energyRF, 65536));
                teRF.extractEnergy(EnumFacing.UP, useAmountRF, false);
                this.energyEU.addEnergy(useAmountRF / 4.0);
            }
        }
    }
    
    public int getRadius() {
        return this.radius;
    }
    
    protected void chargeInventory(EntityPlayer player) {
        for(ItemStack stack : Utilities.getAllPlayerInventory(player)) {
            Item item = stack.getItem();
            if(item instanceof IElectricItem) {
                double tileEnergyEU = this.energyEU.getEnergy();
                if(tileEnergyEU > 0.0) {
                    if(Utilities.isNotFullyCharged(stack))
                        this.energyEU.useEnergy(Utilities.simpleChargeElectricItem(stack, tileEnergyEU));
                }
            }
            else if(item instanceof IEnergyContainerItem) {
                int tileEnergyRF = (int)(this.energyEU.getEnergy() * 4);
                if(tileEnergyRF > 0) {
                    IEnergyContainerItem rfItem = (IEnergyContainerItem)item;
                    int requiredEnergy = rfItem.receiveEnergy(stack, Integer.MAX_VALUE, true);
                    if(requiredEnergy > 0)
                        this.energyEU.useEnergy(rfItem.receiveEnergy(stack, Math.min(tileEnergyRF, requiredEnergy), false) / 4.0);
                }
            }
        }
    }
    
    private void changeMode() {
        short tmp = this.mode;
        if(++tmp > 2) // 0 = EU and RF, 1 = EU only, 2 = RF only
            tmp = 0;
        this.mode = tmp;
    }
    
    protected void onEntityCollision(Entity entity) {
        super.onEntityCollision(entity);
        if(!this.getWorld().isRemote && entity instanceof EntityPlayer) {
            this.playerStandingOnTop = (EntityPlayer)entity;
        }
    }
    
    protected void getPlayerStandingOn() {
        List<EntityPlayer> playersList = this.world.getEntitiesWithinAABB(EntityPlayer.class,
                new AxisAlignedBB(this.pos.up()).expand(0, 1, 0));
        if(!playersList.isEmpty()) {
            Collections.shuffle(playersList);
            this.playerStandingOnTop = playersList.get(0);
        }
    }
    
    protected void changeRadius(int value) {
        this.radius = value < 0 ? Math.max(this.radius + value, 1) : Math.min(this.radius + value, 8);
    }
    
    public short getMode() {
        return this.mode;
    }
    
    public boolean getIsOn() {
        return this.isOn;
    }
    
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.radius = nbt.getInteger("radius");
        this.mode = nbt.getShort("workMode");
        this.isOn = nbt.getBoolean("isOn");
    }
    
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("radius", this.radius);
        nbt.setShort("workMode", this.mode);
        nbt.setBoolean("isOn", this.isOn);
        
        return nbt;
    }
    
    @Override
    public void onNetworkEvent(EntityPlayer player, int event) {
        switch(event) {
            case 0:
                this.changeRadius(1);
                break;
            case 1:
                this.changeRadius(-1);
                break;
            case 2:
                this.changeMode();
                break;
            case 3:
                this.isOn = !this.isOn;
                break;
        }
    }
    
    @Override
    public ContainerBase<TileWirelessChargepad> getGuiContainer(EntityPlayer player) {
        return new ContainerWirelessChargepad(player, this);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean b) {
        return new GuiWirelessChargepad(new ContainerWirelessChargepad(player, this));
    }
    
    @Override
    public void onGuiClosed(EntityPlayer player) {
    }
    
    @Override
    public boolean canConnectEnergy(EnumFacing side) {
        return true;
    }
    
    @Override
    public int getEnergyStored(EnumFacing side) {
        return (int)this.energyEU.getEnergy() * 4;
    }
    
    @Override
    public int getMaxEnergyStored(EnumFacing side) {
        return (int)this.energyEU.getCapacity() * 4;
    }
    
    @Override
    public int receiveEnergy(EnumFacing side, int receive, boolean simulate) {
        int energyReceivedRF = (int)Math.min((this.energyEU.getCapacity() - this.energyEU.getEnergy()) * 4, receive);
        if(!simulate)
            this.energyEU.addEnergy(energyReceivedRF / 4.0);
        return energyReceivedRF;
    }
}
