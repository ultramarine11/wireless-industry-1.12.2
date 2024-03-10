package ru.wirelesstools.tileentities.wireless;

import com.mojang.authlib.GameProfile;
import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.comp.Energy;
import ic2.core.block.comp.Fluids;
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
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.wirelesstools.config.ConfigWI;
import ru.wirelesstools.container.ContainerTesseract;
import ru.wirelesstools.gui.GuiTesseract;
import ru.wirelesstools.wnet.TesseractRegistry;

import java.util.*;

public class TileTesseract extends TileEntityInventory implements IHasGui, INetworkClientTileEntityEventListener {
    
    protected int channelEnergy;
    protected int channelFluid_1, channelFluid_2, channelFluid_3;
    protected GameProfile owner = null; //Utilities.DEFAULT_UNKNOWN_OWNER;
    protected final Energy energy;
    //protected static final Map<GameProfile, Set<TileTesseract>> tesseractTilesMap = new HashMap<>();
    protected boolean sendEnergy;
    protected double wirelessTransferAmount;
    protected final Fluids fluids;
    protected final Fluids.InternalFluidTank fluidTank_1;
    protected final Fluids.InternalFluidTank fluidTank_2;
    protected final Fluids.InternalFluidTank fluidTank_3;
    protected boolean sendFluid_1, sendFluid_2, sendFluid_3;
    private boolean registered = false;
    
    public TileTesseract() {
        this.energy = this.addComponent(new Energy(this, 320000000.0, Util.allFacings, Util.allFacings, 10));
        this.sendEnergy = true;
        this.energy.setSendingEnabled(false);
        this.energy.setReceivingEnabled(true);
        this.wirelessTransferAmount = ConfigWI.wirelessTransferTesseract;
        this.channelEnergy = this.channelFluid_1 = this.channelFluid_2 = this.channelFluid_3 = 1;
        this.fluids = this.addComponent(new Fluids(this));
        this.fluidTank_1 = this.fluids.addTank("tank_1", 20000);
        this.fluidTank_2 = this.fluids.addTank("tank_2", 20000);
        this.fluidTank_3 = this.fluids.addTank("tank_3", 20000);
        this.sendFluid_1 = this.sendFluid_2 = this.sendFluid_3 = true;
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, List<String> info, ITooltipFlag flag) {
        super.addInformation(stack, info, flag);
        NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
        info.add(Localization.translate("ic2.item.tooltip.Store") + " "
                + (long)nbt.getDouble("energy") + " "
                + Localization.translate("ic2.generic.text.EU"));
        if(nbt.hasKey("FluidName1", Constants.NBT.TAG_STRING)) {
            FluidStack fs1 = new FluidStack(FluidRegistry.getFluid(nbt.getString("FluidName1")), nbt.getInteger("Amount1"));
            info.add(fs1.getLocalizedName() + ": " + fs1.amount);
        }
        if(nbt.hasKey("FluidName2", Constants.NBT.TAG_STRING)) {
            FluidStack fs2 = new FluidStack(FluidRegistry.getFluid(nbt.getString("FluidName2")), nbt.getInteger("Amount2"));
            info.add(fs2.getLocalizedName() + ": " + fs2.amount);
        }
        if(nbt.hasKey("FluidName3", Constants.NBT.TAG_STRING)) {
            FluidStack fs3 = new FluidStack(FluidRegistry.getFluid(nbt.getString("FluidName3")), nbt.getInteger("Amount3"));
            info.add(fs3.getLocalizedName() + ": " + fs3.amount);
        }
    }
    
    protected ItemStack adjustDrop(ItemStack drop, boolean wrench) {
        drop = super.adjustDrop(drop, wrench);
        if(wrench || this.teBlock.getDefaultDrop() == TeBlock.DefaultDrop.Self) {
            NBTTagCompound nbt = StackUtil.getOrCreateNbtData(drop);
            nbt.setDouble("energy", this.energy.getEnergy());
            if(this.owner != null /* Utilities.DEFAULT_UNKNOWN_OWNER */)
                nbt.setTag("ownerGameProfile", NBTUtil.writeGameProfile(new NBTTagCompound(), this.owner));
            
            if(this.fluidTank_1.getFluid() != null) {
                nbt.setString("FluidName1", FluidRegistry.getFluidName(this.fluidTank_1.getFluid().getFluid()));
                nbt.setInteger("Amount1", this.fluidTank_1.getFluid().amount);
                if(this.fluidTank_1.getFluid().tag != null)
                    nbt.setTag("Tag1", this.fluidTank_1.getFluid().tag);
            }
            if(this.fluidTank_2.getFluid() != null) {
                nbt.setString("FluidName2", FluidRegistry.getFluidName(this.fluidTank_2.getFluid().getFluid()));
                nbt.setInteger("Amount2", this.fluidTank_2.getFluid().amount);
                if(this.fluidTank_2.getFluid().tag != null)
                    nbt.setTag("Tag2", this.fluidTank_2.getFluid().tag);
            }
            if(this.fluidTank_3.getFluid() != null) {
                nbt.setString("FluidName3", FluidRegistry.getFluidName(this.fluidTank_3.getFluid().getFluid()));
                nbt.setInteger("Amount3", this.fluidTank_3.getFluid().amount);
                if(this.fluidTank_3.getFluid().tag != null)
                    nbt.setTag("Tag3", this.fluidTank_3.getFluid().tag);
            }
        }
        return drop;
    }
    
    protected boolean onActivated(EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY,
                                  float hitZ) {
        if(!this.world.isRemote) {
            if(!this.permitsAccess(player.getGameProfile()) && !player.capabilities.isCreativeMode) {
                player.sendMessage(new TextComponentTranslation("access.tesseract.not.allowed")
                        .setStyle(new Style().setColor(TextFormatting.RED)));
                return true;
            }
        }
        return super.onActivated(player, hand, side, hitX, hitY, hitZ);
    }
    
    protected boolean permitsAccess(GameProfile profile) {
        if(profile == null)
            return this.owner == null;// Utilities.DEFAULT_UNKNOWN_OWNER;
        if(!this.world.isRemote) {
            if(this.owner == null /* Utilities.DEFAULT_UNKNOWN_OWNER */) {
                this.setOwner(profile);
                return true;
            }
        }
        return this.owner.equals(profile);
    }
    
    public void onPlaced(ItemStack stack, EntityLivingBase placer, EnumFacing facing) {
        super.onPlaced(stack, placer, facing);
        if(!this.world.isRemote) {
            NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
            if(nbt.hasKey("energy"))
                this.energy.addEnergy(nbt.getDouble("energy"));
            if(nbt.hasKey("FluidName1", Constants.NBT.TAG_STRING)) {
                this.fluidTank_1.fill(new FluidStack(FluidRegistry.getFluid(nbt.getString("FluidName1")), nbt.getInteger("Amount1")), true);
            }
            if(nbt.hasKey("FluidName2", Constants.NBT.TAG_STRING)) {
                this.fluidTank_2.fill(new FluidStack(FluidRegistry.getFluid(nbt.getString("FluidName2")), nbt.getInteger("Amount2")), true);
            }
            if(nbt.hasKey("FluidName3", Constants.NBT.TAG_STRING)) {
                this.fluidTank_2.fill(new FluidStack(FluidRegistry.getFluid(nbt.getString("FluidName3")), nbt.getInteger("Amount3")), true);
            }
            if(placer instanceof EntityPlayer) {
                this.setOwner(((EntityPlayer)placer).getGameProfile());
            }
        }
    }
    
    protected void updateEntityServer() {
        super.updateEntityServer();
        /*tesseractTilesMap.get(this.owner).forEach(te -> {
            if(te != this) {
                if(this.channelEnergy == te.channelEnergy && this.sendEnergy && !te.sendEnergy) {
                    if(te.energy.getFreeEnergy() > 0.0 && this.energy.getEnergy() > 0.0)
                        this.energy.useEnergy(te.energy.addEnergy(Math.min(this.wirelessTransferAmount,
                                this.energy.getEnergy())));
                }
                if(this.channelFluid_1 == te.channelFluid_1 && this.sendFluid_1 && !te.sendFluid_1) {
                    FluidStack thisFS = this.fluidTank_1.getFluid();
                    if(thisFS != null) {
                        this.fluidTank_1.drain(
                                te.fluidTank_1.fill(
                                        new FluidStack(thisFS, Math.min(thisFS.amount, 1000)), true), true);
                    }
                }
                if(this.channelFluid_2 == te.channelFluid_2 && this.sendFluid_2 && !te.sendFluid_2) {
                    FluidStack thisFS = this.fluidTank_2.getFluid();
                    if(thisFS != null) {
                        this.fluidTank_2.drain(
                                te.fluidTank_2.fill(
                                        new FluidStack(thisFS, Math.min(thisFS.amount, 1000)), true), true);
                    }
                }
                if(this.channelFluid_3 == te.channelFluid_3 && this.sendFluid_3 && !te.sendFluid_3) {
                    FluidStack thisFS = this.fluidTank_3.getFluid();
                    if(thisFS != null) {
                        this.fluidTank_3.drain(
                                te.fluidTank_3.fill(
                                        new FluidStack(thisFS, Math.min(thisFS.amount, 1000)), true), true);
                    }
                }
            }
        });*/
        if(!this.registered)
            this.addToRegistry();
        
        if(!TesseractRegistry.getInstance().getTesseractTilesMap().isEmpty())
            TesseractRegistry.getInstance().getTesseractTilesMap().get(this.owner).forEach(te -> {
                if(te != this) {
                    if(this.channelEnergy == te.channelEnergy && this.sendEnergy && !te.sendEnergy) {
                        if(te.energy.getFreeEnergy() > 0.0 && this.energy.getEnergy() > 0.0)
                            this.energy.useEnergy(te.energy.addEnergy(Math.min(this.wirelessTransferAmount,
                                    this.energy.getEnergy())));
                    }
                    if(this.channelFluid_1 == te.channelFluid_1 && this.sendFluid_1 && !te.sendFluid_1) {
                        FluidStack thisFS = this.fluidTank_1.getFluid();
                        if(thisFS != null) {
                            this.fluidTank_1.drain(
                                    te.fluidTank_1.fill(
                                            new FluidStack(thisFS, Math.min(thisFS.amount, 1000)), true), true);
                        }
                    }
                    if(this.channelFluid_2 == te.channelFluid_2 && this.sendFluid_2 && !te.sendFluid_2) {
                        FluidStack thisFS = this.fluidTank_2.getFluid();
                        if(thisFS != null) {
                            this.fluidTank_2.drain(
                                    te.fluidTank_2.fill(
                                            new FluidStack(thisFS, Math.min(thisFS.amount, 1000)), true), true);
                        }
                    }
                    if(this.channelFluid_3 == te.channelFluid_3 && this.sendFluid_3 && !te.sendFluid_3) {
                        FluidStack thisFS = this.fluidTank_3.getFluid();
                        if(thisFS != null) {
                            this.fluidTank_3.drain(
                                    te.fluidTank_3.fill(
                                            new FluidStack(thisFS, Math.min(thisFS.amount, 1000)), true), true);
                        }
                    }
                }
            });
    }
    
    /*protected void onLoaded() {
        super.onLoaded();
        if(!this.world.isRemote) {
            //TesseractRegistry.getInstance().getTesseractTilesMap().computeIfAbsent(this.owner, key -> new HashSet<>()).add(this);
            //System.out.println("onLoaded");
            //tesseractTilesMap.computeIfAbsent(this.owner, key -> new HashSet<>()).add(this);
        }
    }*/
    
    protected void onUnloaded() {
        if(!this.world.isRemote) {
            if(this.registered)
                this.removeFromRegistry();
            //TesseractRegistry.getInstance().getTesseractTilesMap().get(this.owner).remove(this); // todo !!!
            //System.out.println("onUnloaded");
            //tesseractTilesMap.get(this.owner).remove(this);
        }
        super.onUnloaded();
    }
    
    private void addToRegistry() {
        TesseractRegistry.getInstance().getTesseractTilesMap().computeIfAbsent(this.owner, key -> new HashSet<>()).add(this);
        this.registered = true;
    }
    
    private void removeFromRegistry() {
        TesseractRegistry.getInstance().getTesseractTilesMap().get(this.owner).remove(this);
        this.registered = false;
    }
    
    public void setOwner(GameProfile owner) {
        this.owner = owner;
        IC2.network.get(true).updateTileEntityField(this, "owner");
    }
    
    public int gaugeEnergy(int pixels) {
        return (int)(pixels * this.energy.getEnergy() / this.energy.getCapacity());
    }
    
    public Energy getEnergyRef() {
        return this.energy;
    }
    
    public Fluids.InternalFluidTank getFluidTank_1() {
        return this.fluidTank_1;
    }
    
    public Fluids.InternalFluidTank getFluidTank_2() {
        return this.fluidTank_2;
    }
    
    public Fluids.InternalFluidTank getFluidTank_3() {
        return this.fluidTank_3;
    }
    
    public int getChannelEnergy() {
        return this.channelEnergy;
    }
    
    public int getChannelFluid_1() {
        return this.channelFluid_1;
    }
    
    public int getChannelFluid_2() {
        return this.channelFluid_2;
    }
    
    public int getChannelFluid_3() {
        return this.channelFluid_3;
    }
    
    public GameProfile getOwner() {
        return this.owner;
    }
    
    public double getWirelessTransferAmount() {
        return this.wirelessTransferAmount;
    }
    
    private void changeWirelessTransfer(double amount) {
        this.wirelessTransferAmount = Math.max(0.0, this.wirelessTransferAmount + amount);
    }
    
    private void changeChannelEnergy(int amount) {
        this.channelEnergy = Math.max(1, this.channelEnergy + amount);
    }
    
    private void changeChannelFluid(int amount, byte tankNumber) {
        switch(tankNumber) {
            case 1:
                this.channelFluid_1 = Math.max(this.channelFluid_1 + amount, 1);
                break;
            case 2:
                this.channelFluid_2 = Math.max(this.channelFluid_2 + amount, 1);
                break;
            case 3:
                this.channelFluid_3 = Math.max(this.channelFluid_3 + amount, 1);
                break;
        }
    }
    
    private void invertFluidTankFillDrain(byte tankNumber) {
        switch(tankNumber) {
            case 1:
                this.sendFluid_1 = !this.sendFluid_1;
                break;
            case 2:
                this.sendFluid_2 = !this.sendFluid_2;
                break;
            case 3:
                this.sendFluid_3 = !this.sendFluid_3;
                break;
        }
    }
    
    protected boolean canEntityDestroy(Entity entity) {
        if(entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            return player.capabilities.isCreativeMode || player.getGameProfile().equals(this.owner);
        }
        return false;
    }
    
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if(nbt.hasKey("ownerGameProfile"))
            this.owner = NBTUtil.readGameProfileFromNBT(nbt.getCompoundTag("ownerGameProfile"));
        
        this.channelEnergy = nbt.getInteger("channelEnergy");
        this.channelFluid_1 = nbt.getInteger("channelFluid_1");
        this.channelFluid_2 = nbt.getInteger("channelFluid_2");
        this.channelFluid_3 = nbt.getInteger("channelFluid_3");
        this.sendEnergy = nbt.getBoolean("sendEnergy");
        this.sendFluid_1 = nbt.getBoolean("sendFluid_1");
        this.sendFluid_2 = nbt.getBoolean("sendFluid_2");
        this.sendFluid_3 = nbt.getBoolean("sendFluid_3");
        this.wirelessTransferAmount = nbt.getDouble("wirelessTransfer");
        this.energy.setSendingEnabled(!this.sendEnergy); // todo !!!
        this.energy.setReceivingEnabled(this.sendEnergy); // todo !!!
    }
    
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        if(this.owner != null /* Utilities.DEFAULT_UNKNOWN_OWNER */)
            nbt.setTag("ownerGameProfile", NBTUtil.writeGameProfile(new NBTTagCompound(), this.owner));
        
        nbt.setInteger("channelEnergy", this.channelEnergy);
        nbt.setInteger("channelFluid_1", this.channelFluid_1);
        nbt.setInteger("channelFluid_2", this.channelFluid_2);
        nbt.setInteger("channelFluid_3", this.channelFluid_3);
        nbt.setBoolean("sendEnergy", this.sendEnergy);
        nbt.setBoolean("sendFluid_1", this.sendFluid_1);
        nbt.setBoolean("sendFluid_2", this.sendFluid_2);
        nbt.setBoolean("sendFluid_3", this.sendFluid_3);
        nbt.setDouble("wirelessTransfer", this.wirelessTransferAmount);
        return nbt;
    }
    
    @Override
    public void onNetworkEvent(EntityPlayer player, int id) {
        /*
        0: увеличить на 500 (ЛКМ)
        1: увеличить на 100 (ПКМ)
        2: увеличить на 5000 (ЛКМ + Shift)
        3: увеличить на 1000 (ПКМ + Shift)
        4: увеличить на 50 (ЛКМ + Ctrl)
        5: увеличить на 10 (ПКМ + Ctrl)
        6: увеличить на 5 (ЛКМ + Alt)
        7: увеличить на 1 (ПКМ + Alt)
        
        8: уменьшить на 500 (ЛКМ)
        9: уменьшить на 100 (ПКМ)
        10: уменьшить на 5000 (ЛКМ + Shift)
        11: уменьшить на 1000 (ПКМ + Shift)
        12: уменьшить на 50 (ЛКМ + Ctrl)
        13: уменьшить на 10 (ПКМ + Ctrl)
        14: уменьшить на 5 (ЛКМ + Alt)
        15: уменьшить на 1 (ПКМ + Alt)
        */
        if(player.getGameProfile().equals(this.owner)) {
            switch(id) {
                case 0:
                    this.changeWirelessTransfer(500.0);
                    break;
                case 1:
                    this.changeWirelessTransfer(100.0);
                    break;
                case 2:
                    this.changeWirelessTransfer(5000.0);
                    break;
                case 3:
                    this.changeWirelessTransfer(1000.0);
                    break;
                case 4:
                    this.changeWirelessTransfer(50.0);
                    break;
                case 5:
                    this.changeWirelessTransfer(10.0);
                    break;
                case 6:
                    this.changeWirelessTransfer(5.0);
                    break;
                case 7:
                    this.changeWirelessTransfer(1.0);
                    break;
                
                case 8:
                    this.changeWirelessTransfer(-500.0);
                    break;
                case 9:
                    this.changeWirelessTransfer(-100.0);
                    break;
                case 10:
                    this.changeWirelessTransfer(-5000.0);
                    break;
                case 11:
                    this.changeWirelessTransfer(-1000.0);
                    break;
                case 12:
                    this.changeWirelessTransfer(-50.0);
                    break;
                case 13:
                    this.changeWirelessTransfer(-10.0);
                    break;
                case 14:
                    this.changeWirelessTransfer(-5.0);
                    break;
                case 15:
                    this.changeWirelessTransfer(-1.0);
                    break;
                
                case 16:
                    this.invertEnergyMode();
                    break;
                case 17:
                    this.changeChannelEnergy(5);
                    break;
                case 18:
                    this.changeChannelEnergy(1);
                    break;
                case 19:
                    this.changeChannelEnergy(-5);
                    break;
                case 20:
                    this.changeChannelEnergy(-1);
                    break;
                
                case 21:
                    this.changeChannelFluid(5, (byte)1);
                    break;
                case 22:
                    this.changeChannelFluid(1, (byte)1);
                    break;
                case 23:
                    this.changeChannelFluid(5, (byte)2);
                    break;
                case 24:
                    this.changeChannelFluid(1, (byte)2);
                    break;
                case 25:
                    this.changeChannelFluid(5, (byte)3);
                    break;
                case 26:
                    this.changeChannelFluid(1, (byte)3);
                    break;
                case 27:
                    this.changeChannelFluid(-5, (byte)1);
                    break;
                case 28:
                    this.changeChannelFluid(-1, (byte)1);
                    break;
                case 29:
                    this.changeChannelFluid(-5, (byte)2);
                    break;
                case 30:
                    this.changeChannelFluid(-1, (byte)2);
                    break;
                case 31:
                    this.changeChannelFluid(-5, (byte)3);
                    break;
                case 32:
                    this.changeChannelFluid(-1, (byte)3);
                    break;
                
                case 33:
                    this.invertFluidTankFillDrain((byte)1);
                    break;
                case 34:
                    this.invertFluidTankFillDrain((byte)2);
                    break;
                case 35:
                    this.invertFluidTankFillDrain((byte)3);
                    break;
            }
        }
        else
            player.sendMessage(new TextComponentTranslation("tesseract.press.buttons.not.allowed")
                    .setStyle(new Style().setColor(TextFormatting.RED)));
    }
    
    @Override
    public ContainerBase<TileTesseract> getGuiContainer(EntityPlayer player) {
        return new ContainerTesseract(player, this);
    }
    
    @SideOnly(Side.CLIENT)
    public GuiScreen getGui(EntityPlayer player, boolean b) {
        return new GuiTesseract(new ContainerTesseract(player, this));
    }
    
    @Override
    public void onGuiClosed(EntityPlayer player) {
    }
    
    public void invertEnergyMode() {
        this.sendEnergy = !this.sendEnergy;
        this.energy.setSendingEnabled(!this.sendEnergy);
        this.energy.setReceivingEnabled(this.sendEnergy);
    }
    
    public boolean isSendingEnergy() {
        return this.sendEnergy;
    }
    
    public boolean isSendingFluid_1() {
        return this.sendFluid_1;
    }
    
    public boolean isSendingFluid_2() {
        return this.sendFluid_2;
    }
    
    public boolean isSendingFluid_3() {
        return this.sendFluid_3;
    }
    
}
