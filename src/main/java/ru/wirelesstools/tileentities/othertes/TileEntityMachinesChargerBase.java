package ru.wirelesstools.tileentities.othertes;

import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.comp.Energy;
import ic2.core.init.Localization;
import ic2.core.ref.TeBlock;
import ic2.core.util.StackUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.wirelesstools.api.WirelessTransfer;
import ru.wirelesstools.tileentities.wireless.IChargerDispatcherTile;

import java.util.List;

public abstract class TileEntityMachinesChargerBase extends TileEntityInventory
        implements IHasGui, INetworkClientTileEntityEventListener, IChargerDispatcherTile {
    
    protected final Energy energy;
    private Iterable<BlockPos.MutableBlockPos> poses;
    protected int radius;
    protected short mode;
    protected double chargeRate;
    private int energyTilesQuantity;
    protected boolean isOn;
    protected int offsetXZ;
    protected int offsetY;
    protected boolean isAsymmetricCharging;
    
    public TileEntityMachinesChargerBase(double maxstorage, int tier) {
        this.energy = this.addComponent(Energy.asBasicSink(this, maxstorage, tier));
        this.mode = 0;
        this.chargeRate = 512.0;
        this.radius = 15;
        this.energyTilesQuantity = 0;
        this.offsetXZ = 15;
        this.offsetY = 15;
        this.isAsymmetricCharging = false;
        this.isOn = true;
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
            NBTTagCompound nbt = StackUtil.getOrCreateNbtData(drop);
            nbt.setDouble("energy", this.energy.getEnergy());
            nbt.setBoolean("isOn", this.isOn);
        }
        return drop;
    }
    
    public void onPlaced(ItemStack stack, EntityLivingBase placer, EnumFacing facing) {
        super.onPlaced(stack, placer, facing);
        if(!this.world.isRemote) {
            NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
            this.isOn = !nbt.hasKey("isOn") || nbt.getBoolean("isOn");
            this.energy.addEnergy(nbt.getDouble("energy"));
        }
    }
    
    protected void updateEntityServer() {
        super.updateEntityServer();
        this.energyTilesQuantity = 0;
        if(this.isOn) {
            if(this.poses != null) {
                for(BlockPos.MutableBlockPos pos : this.poses) {
                    TileEntity te = this.world.getTileEntity(pos);
                    WirelessTransfer.transfer.transferEnergyTEMC(te, this);
                }
            }
        }
    }
    
    private void changeChargeRate(boolean increment) {
        if(increment) {
            if(++this.mode > 8) // 8 = automatic mode
                this.mode = 0;
        }
        else {
            if(--this.mode < 0)
                this.mode = 8; // 8 = automatic mode
        }
        if(this.mode < 8)
            this.chargeRate = 512.0 * Math.pow(2.0, this.mode);
    }
    
    public void incrementTEQuantity() {
        this.energyTilesQuantity++;
    }
    
    public Energy getEnergyReference() {
        return this.energy;
    }
    
    public double getChargeRate() {
        return this.chargeRate;
    }
    
    public int getEnergyTilesQuantity() {
        return this.energyTilesQuantity;
    }
    
    public short getMode() {
        return this.mode;
    }
    
    public int getRadius() {
        return this.radius;
    }
    
    public int getOffsetXZ() {
        return this.offsetXZ;
    }
    
    public int getOffsetY() {
        return this.offsetY;
    }
    
    public boolean getIsAsymmetricCharging() {
        return this.isAsymmetricCharging;
    }
    
    protected void toggleWork() {
        this.isOn = !this.isOn;
    }
    
    protected void onLoaded() {
        super.onLoaded();
        if(!this.world.isRemote) {
            this.poses = this.setIterableBlockPoses();
        }
    }
    
    protected void onUnloaded() {
        super.onUnloaded();
        if(!this.world.isRemote) {
            this.poses = null;
        }
    }
    
    @Override
    public void onNetworkEvent(EntityPlayer player, int event) {
        switch(event) {
            case 1:
                this.changeRadius(1);
                break;
            case 2:
                this.changeRadius(-1);
                break;
            case 3:
                this.changeChargeRate(true);
                break;
            case 4:
                this.changeChargeRate(false);
                break;
            case 5:
                this.toggleWork();
                break;
            case 6:
                this.changeOffsetsXZ(1);
                break;
            case 7:
                this.changeOffsetsXZ(-1);
                break;
            case 8:
                this.changeOffsetsY(1);
                break;
            case 9:
                this.changeOffsetsY(-1);
                break;
            case 10:
                this.isAsymmetricCharging = !this.isAsymmetricCharging;
                this.poses = this.setIterableBlockPoses();
                break;
        }
    }
    
    protected Iterable<BlockPos.MutableBlockPos> setIterableBlockPoses() {
        return this.isAsymmetricCharging ? this.setIterableBlockPosesAsymmetric() : this.setIterableBlockPosesDefault();
    }
    
    protected Iterable<BlockPos.MutableBlockPos> setIterableBlockPosesDefault() {
        return BlockPos.getAllInBoxMutable(
                this.getPos().getX() - this.radius, this.getPos().getY() - this.radius, this.getPos().getZ() - this.radius,
                this.getPos().getX() + this.radius, this.getPos().getY() + this.radius, this.getPos().getZ() + this.radius);
    }
    
    protected Iterable<BlockPos.MutableBlockPos> setIterableBlockPosesAsymmetric() {
        return BlockPos.getAllInBoxMutable(
                this.getPos().getX() - this.offsetXZ, this.getPos().getY() - this.offsetY, this.getPos().getZ() - this.offsetXZ,
                this.getPos().getX() + this.offsetXZ, this.getPos().getY() + this.offsetY, this.getPos().getZ() + this.offsetXZ);
    }
    
    protected void changeRadius(int value) {
        this.radius = value < 0 ? Math.max(this.radius + value, 0) : Math.min(this.radius + value, 25);
        this.poses = this.setIterableBlockPoses();
    }
    
    protected void changeOffsetsXZ(int value) {
        this.offsetXZ = value < 0 ? Math.max(this.offsetXZ + value, 0) : Math.min(this.offsetXZ + value, 25);
        this.poses = this.setIterableBlockPoses();
    }
    
    protected void changeOffsetsY(int value) {
        this.offsetY = value < 0 ? Math.max(this.offsetY + value, 0) : Math.min(this.offsetY + value, 25);
        this.poses = this.setIterableBlockPoses();
    }
    
    public boolean getIsOn() {
        return this.isOn;
    }
    
    @Override
    public void onGuiClosed(EntityPlayer player) {
    }
    
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.radius = nbt.getInteger("radius");
        this.mode = nbt.getShort("workMode");
        this.chargeRate = nbt.getDouble("chargeRate");
        this.offsetXZ = nbt.getInteger("offsetXZ");
        this.offsetY = nbt.getInteger("offsetY");
        this.isOn = nbt.getBoolean("isOn");
        this.isAsymmetricCharging = nbt.getBoolean("isAsymmetricCharging");
    }
    
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("radius", this.radius);
        nbt.setShort("workMode", this.mode);
        nbt.setDouble("chargeRate", this.chargeRate);
        nbt.setInteger("offsetXZ", this.offsetXZ);
        nbt.setInteger("offsetY", this.offsetY);
        nbt.setBoolean("isOn", this.isOn);
        nbt.setBoolean("isAsymmetricCharging", this.isAsymmetricCharging);
        return nbt;
    }
    
}
