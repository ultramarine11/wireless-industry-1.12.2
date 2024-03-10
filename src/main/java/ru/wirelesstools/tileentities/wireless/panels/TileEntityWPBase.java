package ru.wirelesstools.tileentities.wireless.panels;

import com.mojang.authlib.GameProfile;
import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.comp.Energy;
import net.minecraft.client.gui.GuiScreen;
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
import ru.wirelesstools.api.IWirelessPanel;
import ru.wirelesstools.api.IWirelessStorage;
import ru.wirelesstools.api.WirelessTransfer;
import ru.wirelesstools.container.ContainerWPP;
import ru.wirelesstools.gui.GUIWP;
import ru.wirelesstools.tileentities.GenerationState;
import ru.wirelesstools.tileentities.SolarConfig;
import ru.wirelesstools.tileentities.wireless.receivers.TileEntityWSBPersonal;
import ru.wirelesstools.utils.Utilities;

public abstract class TileEntityWPBase extends TileEntityInventory implements IHasGui,
        INetworkClientTileEntityEventListener, IWirelessPanel {
    
    protected final int dayPower;
    protected final int nightPower;
    protected final int tier;
    protected GameProfile owner = null;
    protected boolean statePower;
    protected final Energy energy;
    protected int channel;
    protected int wirelesstransferlimit;
    protected GenerationState active = GenerationState.NONE;
    protected boolean canRain;
    protected boolean hasSky;
    
    public int getChannel() {
        return this.channel;
    }
    
    public TileEntityWPBase(SolarConfig config) {
        this(config.dayPower, config.nightPower, config.maxStorage, config.tier, config.wirelesstransferlimit);
    }
    
    public TileEntityWPBase(int dayPower, int nightPower, double maxstorage, int tier, int limit) {
        this.dayPower = dayPower;
        this.nightPower = nightPower;
        this.energy = this.addComponent(Energy.asBasicSource(this, maxstorage, tier));
        this.tier = tier;
        this.wirelesstransferlimit = limit;
        this.statePower = true;
    }
    
    protected void onLoaded() {
        super.onLoaded();
        if(!this.world.isRemote) {
            // this.addedToEnet = !MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
            this.canRain = (this.world.getBiome(this.pos).canRain() || this.world.getBiome(this.pos).getRainfall() > 0.0F);
            this.hasSky = !this.world.provider.isNether();
        }
    }
    
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if(nbt.hasKey("ownerGameProfile")) {
            this.owner = NBTUtil.readGameProfileFromNBT(nbt.getCompoundTag("ownerGameProfile"));
        }
        this.statePower = nbt.getBoolean("state_on_off");
        this.channel = nbt.getInteger("channel");
    }
    
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("channel", this.channel);
        nbt.setBoolean("state_on_off", this.statePower);
        if(this.owner != null) {
            NBTTagCompound ownerNbt = new NBTTagCompound();
            NBTUtil.writeGameProfile(ownerNbt, this.owner);
            nbt.setTag("ownerGameProfile", ownerNbt);
        }
        return nbt;
    }
    
    public int getDayPower() {
        return this.dayPower;
    }
    
    public int getNightPower() {
        return this.nightPower;
    }
    
    protected void updateEntityServer() {
        super.updateEntityServer();
        if(this.world.getTotalWorldTime() % 5 == 0)
            this.checkTheSky();
        switch(this.active) {
            case DAY:
                this.energy.addEnergy(this.dayPower);
                break;
            case NIGHT:
                this.energy.addEnergy(this.nightPower);
                break;
        }
        if(this.statePower && !TileEntityWSBPersonal.getInstancesWSB().isEmpty())
            this.operateWirelessTransfer();
    }
    
    protected void operateWirelessTransfer() {
        for(IWirelessStorage te : TileEntityWSBPersonal.getInstancesWSB()) {
            if(te != null) {
                if(Utilities.canSendEnergy(this.channel, te.getChannel(), this.owner, te.getOwner())) {
                    WirelessTransfer.handler.transferEnergyWirelessly(this, te);
                }
            }
        }
    }
    
    public void checkTheSky() {
        if(this.hasSky && this.world.canBlockSeeSky(this.pos.up())) {
            if(this.world.isDaytime() && (!this.canRain || (!this.world.isRaining() && !this.world.isThundering())))
                this.active = GenerationState.DAY;
            else
                this.active = GenerationState.NIGHT;
        }
        else
            this.active = GenerationState.NONE;
    }
    
    public Energy getEnergyRef() {
        return this.energy;
    }
    
    public int gaugeEnergy(int pixels) {
        return (int)(pixels * this.energy.getEnergy() / this.energy.getCapacity());
    }
    
    public GenerationState getGenState() {
        return this.active;
    }
    
    public double gaugeProgressScaled(double i) {
        return i * this.energy.getEnergy() / this.energy.getCapacity();
    }
    
    public ContainerBase<TileEntityWPBase> getGuiContainer(EntityPlayer player) {
        return new ContainerWPP(player, this);
    }
    
    @SideOnly(Side.CLIENT)
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GUIWP(new ContainerWPP(player, this));
    }
    
    public void onGuiClosed(EntityPlayer player) {
    }
    
    public void onPlaced(ItemStack stack, EntityLivingBase placer, EnumFacing facing) {
        super.onPlaced(stack, placer, facing);
        if(!this.world.isRemote) {
            if(placer instanceof EntityPlayer) {
                this.setOwner(((EntityPlayer)placer).getGameProfile());
            }
        }
    }
    
    protected boolean onActivated(EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY,
                                  float hitZ) {
        if(!this.world.isRemote && !this.permitsAccess(player.getGameProfile()) && !player.capabilities.isCreativeMode) {
            player.sendMessage(new TextComponentTranslation("access.solarpanel.not.allowed")
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
    
    public GameProfile getOwner() {
        return this.owner;
    }
    
    public void setOwner(GameProfile owner) {
        this.owner = owner;
        IC2.network.get(true).updateTileEntityField(this, "owner");
    }
    
    @Override
    public void onNetworkEvent(EntityPlayer nameEntityPlayer, int event) {
        switch(event) {
            case 1:
                this.changeChannel(1);
                break;
            case 2:
                this.changeChannel(-1);
                break;
            case 3:
                this.statePower = !this.statePower;
                break;
        }
    }
    
    public void changeChannel(int value) {
        this.channel = Math.max(this.channel + value, 0);
    }
    
    public void setChannel(int ch) {
        this.channel = ch;
    }
    
    public boolean getIsActivePanel() {
        return this.statePower;
    }
    
    @Override
    public double getCurrentEnergyInPanel() {
        return this.energy.getEnergy();
    }
    
    @Override
    public int getWirelessTransferLimit() {
        return this.wirelesstransferlimit;
    }
    
    @Override
    public void extractEnergy(double amount) {
        this.energy.useEnergy(amount);
    }
    
}
