package ru.wirelesstools.tileentities.othertes;

import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.core.ContainerBase;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.comp.Energy;
import ic2.core.block.invslot.InvSlot;
import ic2.core.util.StackUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.wirelesstools.config.ConfigWI;
import ru.wirelesstools.container.ContainerRemoteCollector;
import ru.wirelesstools.gui.GuiRemoteItemCollector;

public class TileRemoteItemCollector extends TileEntityInventory
        implements IHasGui, INetworkClientTileEntityEventListener {

    protected final Energy energy;
    private int collectRadius;
    private AxisAlignedBB boundingBox;
    private final int energyPerOneItem;
    protected boolean isTurnedOn = false;
    public final InvSlot contentSlot = new InvSlot(this, "content", InvSlot.Access.IO, 27);

    public TileRemoteItemCollector() {
        this.energy = this.addComponent(Energy.asBasicSink(this, 500000, 6));
        this.energyPerOneItem = 1;
        this.collectRadius = 5;
    }

    protected void updateEntityServer() {
        super.updateEntityServer();
        if(this.canWork())
            this.doCollectItems();
    }

    public boolean canWork() {
        if(!this.world.isRemote)
            return this.isTurnedOn && !this.isFullInv();
        return false;
    }

    private boolean isFullInv() {
        for(int i = 0; i < this.contentSlot.size(); ++i) {
            ItemStack stack = this.contentSlot.get(i);
            if(!StackUtil.isEmpty(stack) && StackUtil.getSize(stack) >= Math.min(stack.getMaxStackSize(),
                    this.contentSlot.getStackSizeLimit()))
                continue;
            return false;
        }
        return true;
    }

    private void doCollectItems() {
        if(this.boundingBox != null) {
            for(EntityItem entityItem : this.world.getEntitiesWithinAABB(EntityItem.class, this.boundingBox)) {
                if(entityItem != null && !entityItem.isDead) {
                    this.pickEntityItem(entityItem);
                }
            }
        }
    }

    private void pickEntityItem(EntityItem ent) {
        ItemStack stack = ent.getItem().copy();
        int numberInsert = StackUtil.putInInventory(this, EnumFacing.WEST, stack, true);
        if(numberInsert > 0 && this.energy.useEnergy(numberInsert * this.energyPerOneItem)) {
            stack.shrink(StackUtil.putInInventory(this, EnumFacing.WEST, stack, false));
            ent.setItem(stack);
            if(StackUtil.isEmpty(stack))
                ent.setDead();
        }
    }

    public boolean getIsWorking() {
        return this.isTurnedOn;
    }

    public int getRadius() {
        return this.collectRadius;
    }

    public void changeRadius(int value) {
        this.collectRadius = value < 0 ? Math.max(this.collectRadius + value, 1)
                : Math.min(this.collectRadius + value, ConfigWI.maxRadiusRemoteItemCollector);
        this.boundingBox = this.reSetAABB();
        // this.recheckBB();
    }

    protected AxisAlignedBB reSetAABB() {
        return new AxisAlignedBB(this.getPos().getX() - this.collectRadius,
                this.getPos().getY() - this.collectRadius, this.getPos().getZ() - this.collectRadius,
                this.getPos().getX() + this.collectRadius + 1, this.getPos().getY() + this.collectRadius + 1,
                this.getPos().getZ() + this.collectRadius + 1);
    }

    /*private void recheckBB() {
        this.boundingBox = new AxisAlignedBB(this.getPos().getX() - this.collectRadius,
                this.getPos().getY() - this.collectRadius, this.getPos().getZ() - this.collectRadius,
                this.getPos().getX() + this.collectRadius + 1, this.getPos().getY() + this.collectRadius + 1,
                this.getPos().getZ() + this.collectRadius + 1);
    }*/

    protected void onLoaded() {
        super.onLoaded();
        if(!this.world.isRemote) {
            this.boundingBox = this.reSetAABB();
        }
    }

    protected void onUnloaded() {
        super.onUnloaded();
        this.boundingBox = null;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return super.isItemValidForSlot(index, stack);
    }

    @SideOnly(value = Side.CLIENT)
    public GuiScreen getGui(EntityPlayer player, boolean arg1) {
        return new GuiRemoteItemCollector(new ContainerRemoteCollector(player, this));
    }

    @Override
    public ContainerBase<TileRemoteItemCollector> getGuiContainer(EntityPlayer player) {
        return new ContainerRemoteCollector(player, this);
    }

    @Override
    public void onGuiClosed(EntityPlayer player) {
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("radius", this.collectRadius);
        nbt.setBoolean("workstate", this.isTurnedOn);
        return nbt;
    }

    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.isTurnedOn = nbt.getBoolean("workstate");
        this.collectRadius = nbt.getInteger("radius");
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
                this.isTurnedOn = !this.isTurnedOn;
                break;
        }
    }

}
