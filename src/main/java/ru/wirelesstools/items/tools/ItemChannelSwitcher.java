package ru.wirelesstools.items.tools;

import ic2.api.network.INetworkItemEventListener;
import ic2.core.IC2;
import ic2.core.audio.PositionSpec;
import ic2.core.init.BlocksItems;
import ic2.core.init.Localization;
import ic2.core.item.ItemIC2;
import ic2.core.ref.ItemName;
import ic2.core.util.StackUtil;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import ru.wirelesstools.MainWI;
import ru.wirelesstools.Reference;
import ru.wirelesstools.tileentities.wireless.panels.TileEntityWPBase;
import ru.wirelesstools.tileentities.wireless.receivers.TileEntityWSBPersonal;

import java.util.List;

public class ItemChannelSwitcher extends ItemIC2 implements INetworkItemEventListener {
    
    public ItemChannelSwitcher(String name) {
        super(null);
        this.setMaxStackSize(1);
        this.setCreativeTab(MainWI.tab);
        BlocksItems.registerItem(this, new ResourceLocation(Reference.MOD_ID, name)).setUnlocalizedName(name);
    }
    
    @SideOnly(value = Side.CLIENT)
    public void registerModels(ItemName name) {
        ModelResourceLocation read_m = new ModelResourceLocation(Reference.MOD_ID + ":" + "channel_switcher_r", null);
        ModelResourceLocation write_m = new ModelResourceLocation(Reference.MOD_ID + ":" + "channel_switcher_w", null);
        ModelLoader.setCustomMeshDefinition(this, (stack) -> {
            NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
            short mode = nbt.getShort("switcher_mode");
            switch(mode) {
                case 0:
                    return read_m;
                case 1:
                    return write_m;
            }
            return read_m;
        });
        ModelBakery.registerItemVariants(this, read_m);
        ModelBakery.registerItemVariants(this, write_m);
    }
    
    @SideOnly(value = Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            if(nbt.hasKey("channel")) {
                tooltip.add(TextFormatting.GREEN + Localization.translate("switcher.channel.stored.is") + ": "
                        + nbt.getInteger("channel"));
            }
            else
                tooltip.add(TextFormatting.RED + Localization.translate("switcher.no.channel"));
            
            switch(nbt.getShort("switcher_mode")) {
                case 0:
                    tooltip.add(TextFormatting.AQUA + Localization.translate("switcher.mode.read"));
                    break;
                case 1:
                    tooltip.add(TextFormatting.GOLD + Localization.translate("switcher.mode.write"));
                    break;
            }
            
            if(nbt.getBoolean("mute_mode")) {
                tooltip.add(TextFormatting.DARK_GRAY + Localization.translate("switcher.mode.mute"));
            }
            else
                tooltip.add(TextFormatting.DARK_GRAY + Localization.translate("switcher.mode.sound"));
        }
        else
            tooltip.add(TextFormatting.ITALIC + Localization.translate("info.wi.press.lshift"));
        
        if(Keyboard.isKeyDown(Keyboard.KEY_LMENU)) {
            tooltip.add(Localization.translate("switcher.tooltip.change.mode") + " "
                    + Localization.translate("switcher.tooltip.press.rmb") + " + "
                    + KeyBinding.getDisplayString("key.sneak").get());
            tooltip.add(Localization.translate("switcher.tooltip.mute.change") + " "
                    + KeyBinding.getDisplayString("Mode Switch Key").get() + " + "
                    + Localization.translate("switcher.tooltip.press.rmb"));
        }
        else
            tooltip.add(TextFormatting.ITALIC + Localization.translate("press.leftalt"));
    }
    
    @SideOnly(Side.CLIENT)
    public boolean isFull3D() {
        return true;
    }
    
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if(!world.isRemote && player.isSneaking()) {
            ItemStack stack = player.getHeldItem(hand);
            NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
            short mode = nbt.getShort("switcher_mode"); // 0 = read channel, 1 = write channel
            if(++mode > 1) mode = 0;
            nbt.setShort("switcher_mode", mode);
            switch(mode) {
                case 0:
                    player.sendMessage(new TextComponentTranslation("switcher.mode.read"));
                    break;
                case 1:
                    player.sendMessage(new TextComponentTranslation("switcher.mode.write"));
                    break;
            }
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        if(IC2.keyboard.isModeSwitchKeyDown(player)) {
            if(!world.isRemote) {
                ItemStack stack = player.getHeldItem(hand);
                NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
                boolean mute = nbt.getBoolean("mute_mode");
                mute = !mute;
                nbt.setBoolean("mute_mode", mute);
                if(mute)
                    player.sendMessage(new TextComponentTranslation("switcher.mode.mute")
                            .setStyle(new Style().setColor(TextFormatting.DARK_GRAY)));
                else
                    player.sendMessage(new TextComponentTranslation("switcher.mode.sound")
                            .setStyle(new Style().setColor(TextFormatting.DARK_GRAY)));
            }
        }
        return super.onItemRightClick(world, player, hand);
    }
    
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        if(!world.isRemote) {
            ItemStack stack = player.getHeldItem(hand);
            NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
            TileEntity te = world.getTileEntity(pos);
            switch(nbt.getShort("switcher_mode")) { // 0 = read channel, 1 = write channel
                case 0:
                    if(te instanceof TileEntityWPBase) {
                        TileEntityWPBase panel = (TileEntityWPBase)te;
                        if(player.getGameProfile().equals(panel.getOwner())) {
                            int channel_panel = panel.getChannel();
                            nbt.setInteger("channel", channel_panel);
                            player.sendMessage(new TextComponentTranslation("switcher.channel.panel.read").appendSibling(new TextComponentString(": " + channel_panel)));
                            if(!nbt.getBoolean("mute_mode"))
                                IC2.network.get(true).initiateItemEvent(player, stack, 0, true);
                            return EnumActionResult.SUCCESS;
                        }
                        else {
                            player.sendMessage(new TextComponentTranslation("switcher.player.not.owner"));
                            return EnumActionResult.FAIL;
                        }
                    }
                    else if(te instanceof TileEntityWSBPersonal) {
                        TileEntityWSBPersonal storage = (TileEntityWSBPersonal)te;
                        if(player.getGameProfile().equals(storage.getOwner())) {
                            int channel_storage = storage.getChannel();
                            nbt.setInteger("channel", channel_storage);
                            player.sendMessage(new TextComponentTranslation("switcher.channel.storage.read").appendSibling(new TextComponentString(": " + channel_storage)));
                            if(!nbt.getBoolean("mute_mode"))
                                IC2.network.get(true).initiateItemEvent(player, stack, 0, true);
                            return EnumActionResult.SUCCESS;
                        }
                        else {
                            player.sendMessage(new TextComponentTranslation("switcher.player.not.owner"));
                            return EnumActionResult.FAIL;
                        }
                    }
                    break;
                case 1:
                    if(te instanceof TileEntityWPBase) {
                        TileEntityWPBase panel = (TileEntityWPBase)te;
                        if(player.getGameProfile().equals(panel.getOwner())) {
                            if(nbt.hasKey("channel")) {
                                int channel_switcher = nbt.getInteger("channel");
                                panel.setChannel(channel_switcher);
                                player.sendMessage(new TextComponentTranslation("switcher.channel.panel.set").appendSibling(new TextComponentString(": " + channel_switcher)));
                                if(!nbt.getBoolean("mute_mode"))
                                    IC2.network.get(true).initiateItemEvent(player, stack, 1, true);
                                return EnumActionResult.SUCCESS;
                            }
                            else {
                                player.sendMessage(new TextComponentTranslation("switcher.channel.is.empty"));
                                return EnumActionResult.FAIL;
                            }
                        }
                        else {
                            player.sendMessage(new TextComponentTranslation("switcher.player.not.owner"));
                            return EnumActionResult.FAIL;
                        }
                    }
                    else if(te instanceof TileEntityWSBPersonal) {
                        TileEntityWSBPersonal storage = (TileEntityWSBPersonal)te;
                        if(player.getGameProfile().equals(storage.getOwner())) {
                            if(nbt.hasKey("channel")) {
                                int channel_switcher = nbt.getInteger("channel");
                                storage.setChannel(channel_switcher);
                                player.sendMessage(new TextComponentTranslation("switcher.channel.storage.set").appendSibling(new TextComponentString(": " + channel_switcher)));
                                if(!nbt.getBoolean("mute_mode"))
                                    IC2.network.get(true).initiateItemEvent(player, stack, 1, true);
                                return EnumActionResult.SUCCESS;
                            }
                            else {
                                player.sendMessage(new TextComponentTranslation("switcher.channel.is.empty"));
                                return EnumActionResult.FAIL;
                            }
                        }
                        else {
                            player.sendMessage(new TextComponentTranslation("switcher.player.not.owner"));
                            return EnumActionResult.FAIL;
                        }
                    }
                    break;
            }
        }
        
        return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
    }
    
    @Override
    public void onNetworkEvent(ItemStack stack, EntityPlayer player, int event) {
        switch(event) {
            case 0:
                IC2.audioManager.playOnce(player, PositionSpec.Hand, Reference.MOD_ID + ":" + "channel_switcher_in.ogg", true, IC2.audioManager.getDefaultVolume());
                break;
            case 1:
                IC2.audioManager.playOnce(player, PositionSpec.Hand, Reference.MOD_ID + ":" + "channel_switcher_out.ogg", true, IC2.audioManager.getDefaultVolume());
                break;
        }
    }
}
