package ru.wirelesstools.tileentities.othertes;

import com.mojang.authlib.GameProfile;
import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.init.Localization;
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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import ru.wirelesstools.config.ConfigWI;
import ru.wirelesstools.container.ContainerVacuumPlayerChest;
import ru.wirelesstools.gui.GuiVacuumPlayerChest;
import ru.wirelesstools.items.tools.ItemPlayerModule;
import ru.wirelesstools.slot.InvSlotPlayerModule;

import java.util.List;

public class TileVacuumPlayerChest extends TileEntityInventory
        implements IHasGui, INetworkClientTileEntityEventListener {
    
    private int collectradius;
    private AxisAlignedBB boundingbox;
    protected boolean isTurnedOn = false;
    public final InvSlot contentSlot = new InvSlot(this, "content", InvSlot.Access.IO, 27);
    public final InvSlotPlayerModule modulePlayerSlot = new InvSlotPlayerModule(this, 1);
    protected GameProfile owner = null;
    
    public TileVacuumPlayerChest() {
        this.collectradius = 5;
    }
    
    protected void onLoaded() {
        super.onLoaded();
        if(!this.world.isRemote) {
            this.boundingbox = this.reSetAABB();
        }
    }
    
    protected AxisAlignedBB reSetAABB() {
        return new AxisAlignedBB(this.getPos().getX() - this.collectradius,
                this.getPos().getY() - this.collectradius, this.getPos().getZ() - this.collectradius,
                this.getPos().getX() + this.collectradius + 1, this.getPos().getY() + this.collectradius + 1,
                this.getPos().getZ() + this.collectradius + 1);
    }
    
    protected void onUnloaded() {
        super.onUnloaded();
        this.boundingbox = null;
    }
    
    protected void updateEntityServer() {
        super.updateEntityServer();
        if(this.canWork())
            this.findPlayerAndCheckInv();
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, List<String> info, ITooltipFlag flag) {
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
            info.add(Localization.translate("info.VChP.only.owner.change"));
        else
            info.add(TextFormatting.ITALIC + Localization.translate("info.wi.press.lshift"));
        super.addInformation(stack, info, flag);
    }
    
    public boolean canWork() {
        if(this.world.isRemote)
            return false;
        
        return this.isTurnedOn && !this.isFullInv();
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
    
    private void findPlayerAndCheckInv() {
        if(this.boundingbox != null) {
            for(EntityPlayer player : this.world.getEntitiesWithinAABB(EntityPlayer.class, this.boundingbox)) {
                if(player != null) {
                    if(player.getGameProfile().equals(this.getOwner()))
                        this.sendPlayerInvContentToVChest(player);
                    
                    ItemStack modulestack = this.modulePlayerSlot.get();
                    if(modulestack != null && modulestack.getItem() instanceof ItemPlayerModule) {
                        NBTTagCompound nbt = StackUtil.getOrCreateNbtData(modulestack);
                        GameProfile playerGP = NBTUtil.readGameProfileFromNBT(nbt.getCompoundTag("playerModulegameprofile"));
                        if(playerGP != null) {
                            if(player.getGameProfile().equals(playerGP)
                                    && !player.getGameProfile().equals(this.getOwner())) {
                                this.sendPlayerInvContentToVChest(player);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void sendPlayerInvContentToVChest(EntityPlayer player) {
        for(int i = 9; i < player.inventory.mainInventory.size(); i++) { // this is for main inventory except hotbar (index from 9)
            ItemStack currentstack = player.inventory.mainInventory.get(i);
            if(currentstack.isEmpty())
                continue;
            // int numberInserted = StackUtil.putInInventory(this, EnumFacing.WEST, currentstack, true);
            if(StackUtil.putInInventory(this, EnumFacing.WEST, currentstack, true) > 0) {
                currentstack.shrink(StackUtil.putInInventory(this, EnumFacing.WEST, currentstack, false));
                player.inventoryContainer.detectAndSendChanges();
            }
        }
    }
    
    public void setOwner(GameProfile owner) {
        this.owner = owner;
        IC2.network.get(true).updateTileEntityField(this, "owner");
    }
    
    public GameProfile getOwner() {
        return this.owner;
    }
    
    public int getRadius() {
        return this.collectradius;
    }
    
    public boolean getIsWorking() {
        return this.isTurnedOn;
    }
    
    public boolean permitsAccess(GameProfile profile) {
        if(profile == null)
            return this.getOwner() == null;
        
        GameProfile teOwner = this.getOwner();
        if(!this.world.isRemote) {
            if(teOwner == null) {
                this.setOwner(profile);
                //IC2.network.get(true).updateTileEntityField(this, "owner");
                return true;
            }
        }
        
        boolean hasTrustedPlayer = false;
        ItemStack modulestack = this.modulePlayerSlot.get();
        if(modulestack != null && modulestack.getItem() instanceof ItemPlayerModule) {
            GameProfile fromModule = NBTUtil.readGameProfileFromNBT(
                    StackUtil.getOrCreateNbtData(modulestack).getCompoundTag("playerModulegameprofile"));
            if(fromModule != null) {
                if(profile.equals(fromModule))
                    hasTrustedPlayer = true;
            }
        }
        return teOwner.equals(profile) || hasTrustedPlayer;
    }
    
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("isTurnedOn");
        ret.add("collectradius");
        ret.add("owner");
        return ret;
    }
    
    public void onPlaced(ItemStack stack, EntityLivingBase placer, EnumFacing facing) {
        super.onPlaced(stack, placer, facing);
        if(!this.world.isRemote) {
            if(placer instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer)placer;
                this.setOwner(player.getGameProfile());
            }
        }
    }
    
    protected boolean onActivated(EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY,
                                  float hitZ) {
        if(!this.world.isRemote && !this.permitsAccess(player.getGameProfile())
                && !player.capabilities.isCreativeMode) {
            player.sendMessage(new TextComponentTranslation("access.vacuumchest.not.allowed"));
            return true;
        }
        return super.onActivated(player, hand, side, hitX, hitY, hitZ);
    }
    
    public void changeRadius(int value) {
        if(value < 0)
            this.collectradius = Math.max(this.collectradius + value, 1);
        else
            this.collectradius = Math.min(this.collectradius + value, ConfigWI.maxRadiusVacuumChest);
        this.boundingbox = this.reSetAABB();
    }
    
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        if(this.owner != null) {
            /*NBTTagCompound ownerNbt = new NBTTagCompound();
            NBTUtil.writeGameProfile(ownerNbt, this.owner);
            nbt.setTag("ownerGameProfile", ownerNbt);*/
            nbt.setTag("ownerGameProfile", NBTUtil.writeGameProfile(new NBTTagCompound(), this.owner));
        }
        nbt.setInteger("radius", this.collectradius);
        nbt.setBoolean("workstate", this.isTurnedOn);
        return nbt;
    }
    
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if(nbt.hasKey("ownerGameProfile")) {
            this.owner = NBTUtil.readGameProfileFromNBT(nbt.getCompoundTag("ownerGameProfile"));
        }
        this.isTurnedOn = nbt.getBoolean("workstate");
        this.collectradius = nbt.getInteger("radius");
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
                    this.isTurnedOn = !this.isTurnedOn;
                    break;
                case 3:
                
            }
        }
    }
    
    @SideOnly(value = Side.CLIENT)
    public GuiScreen getGui(EntityPlayer player, boolean arg1) {
        return new GuiVacuumPlayerChest(new ContainerVacuumPlayerChest(player, this));
    }
    
    @Override
    public ContainerBase<TileVacuumPlayerChest> getGuiContainer(EntityPlayer player) {
        return new ContainerVacuumPlayerChest(player, this);
    }
    
    @Override
    public void onGuiClosed(EntityPlayer player) {
    }
    
}
