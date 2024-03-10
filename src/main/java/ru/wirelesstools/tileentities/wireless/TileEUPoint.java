package ru.wirelesstools.tileentities.wireless;

import com.mojang.authlib.GameProfile;
import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.comp.Energy;
import ic2.core.init.Localization;
import ic2.core.ref.TeBlock;
import ic2.core.util.StackUtil;
import ic2.core.util.Util;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.wirelesstools.container.ContainerEuPoint;
import ru.wirelesstools.gui.GuiEuPoint;
import ru.wirelesstools.utils.Utilities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class TileEUPoint extends TileEntityInventory implements IHasGui, INetworkClientTileEntityEventListener {
    protected boolean sendMode;
    protected int channel;
    protected GameProfile owner = null;
    protected final Set<GameProfile> owners = new HashSet<>();
    protected final Energy energy;
    protected static final List<TileEUPoint> tilesEuPointsList = new ArrayList<>();
    protected boolean modePublic;
    protected double wirelessTransferLimit;
    protected short guiType;
    
    public TileEUPoint(double limit, int tier, double capacity, short guiType) {
        this.sendMode = true;
        this.energy = this.addComponent(new Energy(this, capacity, Util.allFacings, Util.allFacings, tier));
        this.energy.setSendingEnabled(false);
        this.energy.setReceivingEnabled(true);
        this.channel = 1;
        this.modePublic = false;
        this.wirelessTransferLimit = limit;
        this.guiType = guiType;
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, List<String> info, ITooltipFlag flag) {
        super.addInformation(stack, info, flag);
        info.add(Localization.translate("ic2.item.tooltip.Store") + " "
                + (long)StackUtil.getOrCreateNbtData(stack).getDouble("energy") + " "
                + Localization.translate("ic2.generic.text.EU"));
    }
    
    protected void onLoaded() {
        super.onLoaded();
        if(!this.world.isRemote)
            tilesEuPointsList.add(this);
    }
    
    protected void onUnloaded() {
        if(!this.world.isRemote)
            tilesEuPointsList.remove(this);
        super.onUnloaded();
    }
    
    protected void updateEntityServer() {
        super.updateEntityServer();
        if(this.sendMode)
            this.searchForEuPoints();
    }
    
    protected void searchForEuPoints() {
        for(TileEUPoint point : TileEUPoint.tilesEuPointsList) {
            if(!point.sendMode) {
                if(this.modePublic) {
                    if((this.channel == point.channel) && point.modePublic) {
                        this.energy.useEnergy(point.energy.addEnergy(Math.min(this.energy.getEnergy(), this.wirelessTransferLimit)));
                    }
                }
                else {
                    if(Utilities.canSendEnergy(this.channel, point.channel, this.owner, point.owner)) {
                        this.energy.useEnergy(point.energy.addEnergy(Math.min(this.energy.getEnergy(), this.wirelessTransferLimit)));
                    }
                }
            }
        }
    }
    
    protected boolean canEntityDestroy(Entity entity) {
        if(entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            return player.capabilities.isCreativeMode || player.getGameProfile().equals(this.owner);
        }
        return false;
    }
    
    public short getGuiType() {
        return this.guiType;
    }
    
    public Energy getEnergyRef() {
        return this.energy;
    }
    
    public int gaugeEnergy(int pixels) {
        return (int)(pixels * this.energy.getEnergy() / this.energy.getCapacity());
    }
    
    public int getChannel() {
        return this.channel;
    }
    
    public void setChannel(int ch) {
        this.channel = Math.max(ch, 1);
    }
    
    public GameProfile getOwner() {
        return this.owner;
    }
    
    public double getWirelessTransferLimit() {
        return this.wirelessTransferLimit;
    }
    
    public boolean isSending() {
        return this.sendMode;
    }
    
    public boolean getIsPublic() {
        return this.modePublic;
    }
    
    protected void invertMode() {
        this.sendMode = !this.sendMode;
        if(this.sendMode) { // if sends energy wirelessly, eu point is a wire sink
            this.energy.setSendingEnabled(false);
            this.energy.setReceivingEnabled(true);
            this.setActive(false);
        }
        else { // if receives energy wirelessly, eu point is a wire source
            this.energy.setReceivingEnabled(false);
            this.energy.setSendingEnabled(true);
            this.setActive(true);
        }
    }
    
    @Override
    public void onNetworkEvent(EntityPlayer player, int id) {
        switch(id) {
            case 0:
                this.invertMode();
                break;
            case 1:
                this.channel++;
                break;
            case 2:
                this.channel = Math.max(this.channel - 1, 1);
                break;
            case 3:
                this.modePublic = !this.modePublic;
                break;
            case 4:
                this.channel += 10;
                break;
            case 5:
                this.channel = Math.max(this.channel - 10, 1);
        }
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
            if(placer instanceof EntityPlayer) {
                this.setOwner(((EntityPlayer)placer).getGameProfile());
            }
        }
    }
    
    protected boolean onActivated(EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY,
                                  float hitZ) {
        if(!this.world.isRemote && !this.permitsAccess(player.getGameProfile())
                && !player.capabilities.isCreativeMode) {
            player.sendMessage(new TextComponentTranslation("access.eupoint.not.allowed")
                    .setStyle(new Style().setColor(TextFormatting.RED)));
            return true;
        }
        return super.onActivated(player, hand, side, hitX, hitY, hitZ);
    }
    
    protected boolean permitsAccess(GameProfile profile) {
        if(profile == null)
            return this.owner == null;
        if(!this.world.isRemote) {
            if(this.owner == null) {
                this.setOwner(profile);
                return true;
            }
        }
        return this.owner.equals(profile);
    }
    
    public void setOwner(GameProfile owner) {
        this.owner = owner;
        IC2.network.get(true).updateTileEntityField(this, "owner");
    }
    
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if(nbt.hasKey("ownerGameProfile"))
            this.owner = NBTUtil.readGameProfileFromNBT(nbt.getCompoundTag("ownerGameProfile"));
        
        this.channel = nbt.getInteger("channel");
        this.sendMode = nbt.getBoolean("receivemode");
        if(this.sendMode) { // if sends energy wirelessly, eu point is a wire sink
            this.energy.setSendingEnabled(false);
            this.energy.setReceivingEnabled(true);
        }
        else { // if receives energy wirelessly, eu point is a wire source
            this.energy.setReceivingEnabled(false);
            this.energy.setSendingEnabled(true);
        }
        this.modePublic = nbt.getBoolean("modepublic");
    }
    
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        if(this.owner != null)
            nbt.setTag("ownerGameProfile", NBTUtil.writeGameProfile(new NBTTagCompound(), this.owner));
        nbt.setInteger("channel", this.channel);
        nbt.setBoolean("receivemode", this.sendMode);
        nbt.setBoolean("modepublic", this.modePublic);
        return nbt;
    }
    
    @Override
    public ContainerBase<TileEUPoint> getGuiContainer(EntityPlayer entityPlayer) {
        return new ContainerEuPoint(entityPlayer, this);
    }
    
    @SideOnly(value = Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean b) {
        return new GuiEuPoint(new ContainerEuPoint(entityPlayer, this));
    }
    
    @Override
    public void onGuiClosed(EntityPlayer entityPlayer) {
    }
}
