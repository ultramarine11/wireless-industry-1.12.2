package ru.wirelesstools.tileentities.othertes;

import cofh.redstoneflux.api.IEnergyReceiver;
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
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import ru.wirelesstools.config.ConfigWI;
import ru.wirelesstools.container.ContainerWChPlayer;
import ru.wirelesstools.gui.GuiWChPlayer;
import ru.wirelesstools.items.tools.ItemPlayerModule;
import ru.wirelesstools.slot.InvSlotPlayerModule;
import ru.wirelesstools.utils.Utilities;

import java.util.List;

public class TileWirelessChargerPlayer extends TileEntityInventory
        implements IHasGui, INetworkClientTileEntityEventListener, IEnergyReceiver {
    
    protected final Energy energyEU;
    protected int radius = 10;
    private AxisAlignedBB boundingbox;
    protected GameProfile owner = null;
    private boolean isSetPrivate = false;
    private int energyRF;
    protected int maxEnergyRF;
    private int autoConversionValueEuToRf;
    private int autoConversionValueRfToEu;
    protected short conversionMode;
    protected boolean workingModeGP = false;
    public final InvSlotPlayerModule modulePlayerSlot = new InvSlotPlayerModule(this, 3);
    
    public TileWirelessChargerPlayer() {
        this.energyEU = this.addComponent(Energy.asBasicSink(this, ConfigWI.maxStorageEUChargerPlayer, ConfigWI.tierCharger));
        this.energyRF = 0;
        this.maxEnergyRF = ConfigWI.maxStorageRFChargerPlayer;
        this.autoConversionValueEuToRf = 512;
        this.autoConversionValueRfToEu = 2048;
        this.conversionMode = 0;
    }
    
    protected void updateEntityServer() {
        super.updateEntityServer();
        if(this.conversionMode != 0)
            this.autoConvert();
        if(this.workingModeGP)
            this.chargeInventoryPlayerGP();
        else {
            if(this.boundingbox != null)
                this.chargePlayers();
        }
    }
    
    private void chargeInventoryPlayerGP() {
        for(int i = 0; i < this.modulePlayerSlot.size(); i++) {
            ItemStack modulestack = this.modulePlayerSlot.get(i);
            if(modulestack != null && modulestack.getItem() instanceof ItemPlayerModule) {
                NBTTagCompound nbt = StackUtil.getOrCreateNbtData(modulestack);
                GameProfile playerGP = NBTUtil.readGameProfileFromNBT(nbt.getCompoundTag("playerModulegameprofile"));
                if(playerGP != null) {
                    if(this.getWorld().getMinecraftServer() != null) {
                        EntityPlayerMP plMP = this.getWorld().getMinecraftServer().getPlayerList().getPlayerByUUID(playerGP.getId());
                        Utilities.chargeInventory(plMP, this);
                    }
                }
            }
        }
    }
    
    private void chargePlayers() {
        for(EntityPlayer player : this.getWorld().getEntitiesWithinAABB(EntityPlayer.class, this.boundingbox)) {
            if(player != null) {
                if(this.isSetPrivate) {
                    if(!player.getGameProfile().equals(this.getOwner()))
                        continue;
                }
                Utilities.chargeInventory(player, this);
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, List<String> info, ITooltipFlag flag) {
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            info.add(Localization.translate("info.WChP.only.owner.change"));
            info.add(Localization.translate("ic2.item.tooltip.Store") + " "
                    + (long)StackUtil.getOrCreateNbtData(stack).getDouble("energy") + " "
                    + Localization.translate("ic2.generic.text.EU"));
        }
        else
            info.add(TextFormatting.ITALIC + Localization.translate("info.wi.press.lshift"));
        super.addInformation(stack, info, flag);
    }
    
    public void useEnergyAmount(double amount) {
        this.energyEU.useEnergy(amount);
    }
    
    public double getStorage() {
        return this.energyEU.getEnergy();
    }
    
    public int getRadius() {
        return this.radius;
    }
    
    public boolean getIsChargerPrivate() {
        return this.isSetPrivate;
    }
    
    public boolean getIsModeGP() {
        return this.workingModeGP;
    }
    
    protected void onLoaded() {
        super.onLoaded();
        if(!this.world.isRemote)
            this.boundingbox = this.reSetAABB();
    }
    
    protected void onUnloaded() {
        super.onUnloaded();
        if(!this.world.isRemote)
            this.boundingbox = null;
    }
    
    protected AxisAlignedBB reSetAABB() {
        return new AxisAlignedBB(this.getPos().getX() - this.radius,
                this.getPos().getY() - this.radius, this.getPos().getZ() - this.radius,
                this.getPos().getX() + this.radius + 1, this.getPos().getY() + this.radius + 1,
                this.getPos().getZ() + this.radius + 1);
    }
    
    protected void changeRadius(int value) {
        this.radius = value < 0 ? Math.max(this.radius + value, 1) : Math.min(this.radius + value, 20);
        this.boundingbox = this.reSetAABB();
    }
    
    protected void autoConvert() {
        switch(this.conversionMode) {
            case 1: // EU to RF
                double sentAmount1 = Math.min(this.energyEU.getEnergy(), this.autoConversionValueEuToRf);
                this.energyEU.useEnergy(sentAmount1);
                this.energyRF += (int)sentAmount1 * 4;
                break;
            case 2: // RF to EU
                int sentAmount2 = Math.min(this.energyRF, this.autoConversionValueRfToEu);
                this.useEnergyRF(sentAmount2);
                this.energyEU.addEnergy((double)sentAmount2 / 4);
                break;
        }
    }
    
    protected void changeAutoConversionEuToRf(boolean increase) {
        if(increase) {
            this.autoConversionValueEuToRf = Math.min(this.autoConversionValueEuToRf * 2, 262144);
        }
        else {
            this.autoConversionValueEuToRf = Math.max(this.autoConversionValueEuToRf / 2, 512);
        }
    }
    
    protected void changeAutoConversionRfToEu(boolean increase) {
        if(increase) {
            this.autoConversionValueRfToEu = Math.min(this.autoConversionValueRfToEu * 2, 1048576);
        }
        else {
            this.autoConversionValueRfToEu = Math.max(this.autoConversionValueRfToEu / 2, 2048);
        }
    }
    
    public boolean permitsAccess(GameProfile profile) {
        if(profile == null)
            return this.getOwner() == null;
        GameProfile teOwner = this.getOwner();
        if(!this.world.isRemote) {
            if(teOwner == null) {
                this.setOwner(profile);
                return true;
            }
        }
        return !this.isSetPrivate || teOwner.equals(profile);
    }
    
    protected boolean onActivated(EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY,
                                  float hitZ) {
        if(!this.world.isRemote && !this.permitsAccess(player.getGameProfile()) && !player.capabilities.isCreativeMode) {
            player.sendMessage(new TextComponentTranslation("access.charger.not.allowed"));
            return true;
        }
        return super.onActivated(player, hand, side, hitX, hitY, hitZ);
    }
    
    protected ItemStack adjustDrop(ItemStack drop, boolean wrench) {
        drop = super.adjustDrop(drop, wrench);
        if(wrench || this.teBlock.getDefaultDrop() == TeBlock.DefaultDrop.Self) {
            NBTTagCompound nbt = StackUtil.getOrCreateNbtData(drop);
            nbt.setDouble("energy", this.energyEU.getEnergy());
            nbt.setInteger("rf_energy", this.energyRF);
        }
        return drop;
    }
    
    public void onPlaced(ItemStack stack, EntityLivingBase placer, EnumFacing facing) {
        super.onPlaced(stack, placer, facing);
        if(!this.world.isRemote) {
            if(placer instanceof EntityPlayer) {
                NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
                this.energyEU.addEnergy(nbt.getDouble("energy"));
                this.energyRF = nbt.getInteger("rf_energy");
                this.setOwner(((EntityPlayer)placer).getGameProfile());
            }
        }
    }
    
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if(nbt.hasKey("ownerGameProfile")) {
            this.owner = NBTUtil.readGameProfileFromNBT(nbt.getCompoundTag("ownerGameProfile"));
        }
        this.radius = nbt.getInteger("radius");
        this.isSetPrivate = nbt.getBoolean("isprivate");
        this.energyRF = nbt.getInteger("rf_energy");
        this.autoConversionValueEuToRf = nbt.getInteger("autoConversionValueEuToRf");
        this.autoConversionValueRfToEu = nbt.getInteger("autoConversionValueRfToEu");
        this.conversionMode = nbt.getShort("conversionMode");
    }
    
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        if(this.owner != null) {
            NBTTagCompound ownerNbt = new NBTTagCompound();
            NBTUtil.writeGameProfile(ownerNbt, this.owner);
            nbt.setTag("ownerGameProfile", ownerNbt);
        }
        nbt.setInteger("radius", this.radius);
        nbt.setBoolean("isprivate", this.isSetPrivate);
        nbt.setInteger("rf_energy", this.energyRF);
        nbt.setInteger("autoConversionValueEuToRf", this.autoConversionValueEuToRf);
        nbt.setInteger("autoConversionValueRfToEu", this.autoConversionValueRfToEu);
        nbt.setShort("conversionMode", this.conversionMode);
        return nbt;
    }
    
    public GameProfile getOwner() {
        return this.owner;
    }
    
    public void setOwner(GameProfile owner) {
        this.owner = owner;
        IC2.network.get(true).updateTileEntityField(this, "owner");
    }
    
    @Override
    public void onNetworkEvent(EntityPlayer player, int eventID) {
        if(player.getGameProfile().equals(this.getOwner())) {
            switch(eventID) {
                case 0:
                    this.changeRadius(1);
                    break;
                case 1:
                    this.changeRadius(-1);
                    break;
                case 2:
                    this.isSetPrivate = !this.isSetPrivate;
                    break;
                case 3:
                    if(++this.conversionMode > 2)
                        this.conversionMode = 0;
                    break;
                case 4:
                    this.changeAutoConversionEuToRf(true);
                    break;
                case 5:
                    this.changeAutoConversionEuToRf(false);
                    break;
                case 6:
                    this.changeAutoConversionRfToEu(true);
                    break;
                case 7:
                    this.changeAutoConversionRfToEu(false);
                    break;
                case 8:
                    this.workingModeGP = !this.workingModeGP;
                    break;
            }
        }
    }
    
    @SideOnly(value = Side.CLIENT)
    public GuiScreen getGui(EntityPlayer player, boolean arg1) {
        return new GuiWChPlayer(new ContainerWChPlayer(player, this));
    }
    
    @Override
    public ContainerBase<TileWirelessChargerPlayer> getGuiContainer(EntityPlayer player) {
        return new ContainerWChPlayer(player, this);
    }
    
    @Override
    public void onGuiClosed(EntityPlayer player) {
    }
    
    public void useEnergyRF(int amount) {
        this.energyRF -= Math.min(this.energyRF, amount);
    }
    
    public int getAutoConversionValueEuToRf() {
        return this.autoConversionValueEuToRf;
    }
    
    public int getAutoConversionValueRfToEu() {
        return this.autoConversionValueRfToEu;
    }
    
    public short getConversionMode() {
        return this.conversionMode;
    }
    
    public int getEnergyRF() {
        return this.energyRF;
    }
    
    public int getMaxEnergyRF() {
        return this.maxEnergyRF;
    }
    
    @Override
    public boolean canConnectEnergy(EnumFacing side) {
        return true;
    }
    
    @Override
    public int getEnergyStored(EnumFacing side) {
        return this.energyRF;
    }
    
    @Override
    public int getMaxEnergyStored(EnumFacing side) {
        return this.maxEnergyRF;
    }
    
    @Override
    public int receiveEnergy(EnumFacing side, int receive, boolean simulate) {
        int energyReceived = Math.min(this.maxEnergyRF - this.energyRF, receive);
        if(!simulate)
            this.energyRF += energyReceived;
        return energyReceived;
    }
}
