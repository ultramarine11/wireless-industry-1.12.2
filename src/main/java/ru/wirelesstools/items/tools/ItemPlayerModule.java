package ru.wirelesstools.items.tools;

import ic2.core.IC2;
import ic2.core.init.BlocksItems;
import ic2.core.init.Localization;
import ic2.core.item.ItemIC2;
import ic2.core.ref.ItemName;
import ic2.core.util.StackUtil;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
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

import java.util.List;

public class ItemPlayerModule extends ItemIC2 {
    
    private final String name;
    
    public ItemPlayerModule(String name) {
        super(null);
        this.name = name;
        this.setMaxStackSize(1);
        this.setCreativeTab(MainWI.tab);
        BlocksItems.registerItem(this, new ResourceLocation(Reference.MOD_ID, name)).setUnlocalizedName(name);
    }
    
    @SideOnly(value = Side.CLIENT)
    public void registerModels(ItemName name) {
        ModelLoader.setCustomModelResourceLocation(this, 0,
                new ModelResourceLocation(Reference.MOD_ID + ":" + this.name, null));
    }
    
    @SideOnly(value = Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
        if(NBTUtil.readGameProfileFromNBT(nbt.getCompoundTag("playerModulegameprofile")) != null) {
            tooltip.add(TextFormatting.GREEN + Localization.translate("info.playermodule.owner.is") + ": "
                    + NBTUtil.readGameProfileFromNBT(nbt.getCompoundTag("playerModulegameprofile")).getName());
        }
        else {
            tooltip.add(TextFormatting.RED + Localization.translate("info.playermodule.empty"));
        }
        
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            tooltip.add(Localization.translate("info.playermodule.howto.use"));
            tooltip.add(Localization.translate("info.playermodule.use.self"));
            tooltip.add(TextFormatting.DARK_GRAY + Localization.translate("info.playermodule.may.be.problems"));
        }
        else {
            tooltip.add(TextFormatting.ITALIC + Localization.translate("info.wi.press.lshift"));
        }
        
    }
    
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
        if(IC2.keyboard.isAltKeyDown(player)) {
            if(!world.isRemote) {
                NBTTagCompound ownerNbt = new NBTTagCompound();
                NBTUtil.writeGameProfile(ownerNbt, player.getGameProfile());
                nbt.setTag("playerModulegameprofile", ownerNbt);
                player.sendMessage(new TextComponentTranslation("chat.message.module.player.set")
                        .appendSibling(new TextComponentString(": " + player.getGameProfile().getName()))
                        .setStyle(new Style().setColor(TextFormatting.DARK_GREEN)));
                return new ActionResult<>(EnumActionResult.SUCCESS, stack);
            }
        }
        return super.onItemRightClick(world, player, hand);
    }
    
    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target,
                                            EnumHand hand) {
        if(target.world.isRemote)
            return false;
        NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
        if(target instanceof EntityPlayer) {
            if(player.isSneaking()) {
                EntityPlayer playertarget = (EntityPlayer)target;
                NBTTagCompound ownerNbt = new NBTTagCompound();
                NBTUtil.writeGameProfile(ownerNbt, playertarget.getGameProfile());
                nbt.setTag("playerModulegameprofile", ownerNbt);
                player.sendMessage(new TextComponentTranslation("chat.message.module.player.set")
                        .appendSibling(new TextComponentString(": " + playertarget.getGameProfile().getName()))
                        .setStyle(new Style().setColor(TextFormatting.DARK_GREEN)));
                playertarget.sendMessage(new TextComponentTranslation("chat.message.you.were.set.to.module")
                        .appendSibling(new TextComponentString(" " + player.getGameProfile().getName()))
                        .setStyle(new Style().setColor(TextFormatting.YELLOW)));
                return true;
            }
            else {
                player.sendMessage(new TextComponentTranslation("chat.message.module.sneak")
                        .setStyle(new Style().setColor(TextFormatting.RED)));
            }
        }
        else {
            player.sendMessage(new TextComponentTranslation("chat.message.module.only.player")
                    .setStyle(new Style().setColor(TextFormatting.LIGHT_PURPLE)));
        }
        
        return false;
    }
    
}
