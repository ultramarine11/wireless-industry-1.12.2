package ru.wirelesstools.tileentities.wireless.receivers;

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
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
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
import ru.wirelesstools.api.IWirelessStorage;
import ru.wirelesstools.container.ContainerWSPersonal;
import ru.wirelesstools.gui.GuiWSPersonal;
import ru.wirelesstools.tileentities.WSBConfig;

import java.util.ArrayList;
import java.util.List;

public abstract class TileEntityWSBPersonal extends TileEntityInventory implements IHasGui, INetworkClientTileEntityEventListener, IWirelessStorage {
    
    public final Energy energy;
    protected static ArrayList<IWirelessStorage> instances_WSB = new ArrayList<>();
    protected int channel;
    protected GameProfile owner = null;
    
    public TileEntityWSBPersonal(WSBConfig config) {
        this(config.maxstorage, config.tier);
    }
    
    public TileEntityWSBPersonal(double maxstorage, int tier) {
        this.energy = this.addComponent(Energy.asBasicSource(this, maxstorage, tier));
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
    
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if(nbt.hasKey("ownerGameProfile")) {
            this.owner = NBTUtil.readGameProfileFromNBT(nbt.getCompoundTag("ownerGameProfile"));
        }
        this.channel = nbt.getInteger("channel");
    }
    
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("channel", this.channel);
        if(this.owner != null) {
            NBTTagCompound ownerNbt = new NBTTagCompound();
            NBTUtil.writeGameProfile(ownerNbt, this.owner);
            nbt.setTag("ownerGameProfile", ownerNbt);
        }
        return nbt;
    }
    
    public static List<IWirelessStorage> getInstancesWSB() {
        return TileEntityWSBPersonal.instances_WSB;
    }
    
    protected void onLoaded() {
        super.onLoaded();
        if(!this.world.isRemote)
            TileEntityWSBPersonal.instances_WSB.add(this);
    }
    
    protected void onUnloaded() {
        if(!this.world.isRemote)
            TileEntityWSBPersonal.instances_WSB.remove(this);
        super.onUnloaded();
    }
    
    @Override
    public int getChannel() {
        return this.channel;
    }
    
    public void setChannel(int ch) {
        this.channel = ch;
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
    
    @Override
    public ContainerBase<TileEntityWSBPersonal> getGuiContainer(EntityPlayer player) {
        return new ContainerWSPersonal(player, this);
    }
    
    @SideOnly(Side.CLIENT)
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GuiWSPersonal(new ContainerWSPersonal(player, this));
    }
    
    @Override
    public void onGuiClosed(EntityPlayer nameEntityPlayer) {
    }
    
    protected boolean onActivated(EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY,
                                  float hitZ) {
        if(!this.world.isRemote && !this.permitsAccess(player.getGameProfile())
                && !player.capabilities.isCreativeMode) {
            player.sendMessage(new TextComponentTranslation("access.wsbp.not.allowed")
                    .setStyle(new Style().setColor(TextFormatting.RED)));
            return true;
        }
        return super.onActivated(player, hand, side, hitX, hitY, hitZ);
    }
    
    public boolean permitsAccess(GameProfile profile) {
        if(profile == null)
            return (this.getOwner() == null);
        GameProfile teOwner = this.getOwner();
        if(!this.world.isRemote) {
            if(teOwner == null) {
                this.setOwner(profile);
                return true;
            }
        }
        return teOwner.equals(profile);
    }
    
    @Override
    public GameProfile getOwner() {
        return this.owner;
    }
    
    public void setOwner(GameProfile owner) {
        this.owner = owner;
        IC2.network.get(true).updateTileEntityField(this, "owner");
    }
    
    public void changeChannel(int value) {
        this.channel = Math.max(this.channel + value, 0);
    }
    
    public void onNetworkEvent(EntityPlayer nameEntityPlayer, int event) {
        switch(event) {
            case 1:
                this.changeChannel(1);
                break;
            case 2:
                this.changeChannel(-1);
                break;
        }
    }
    
    @Override
    public double getMaxCapacityOfStorage() {
        return this.energy.getCapacity();
    }
    
    @Override
    public double getCurrentEnergyInStorage() {
        return this.energy.getEnergy();
    }
    
    @Override
    public double addEnergy(double amount) {
        return this.energy.addEnergy(amount);
    }
    
}
