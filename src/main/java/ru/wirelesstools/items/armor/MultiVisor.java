package ru.wirelesstools.items.armor;

import cofh.redstoneflux.api.IEnergyContainerItem;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.core.IC2;
import ic2.core.init.BlocksItems;
import ic2.core.init.Localization;
import ic2.core.item.ElectricItemManager;
import ic2.core.item.armor.ItemArmorUtility;
import ic2.core.ref.ItemName;
import ic2.core.util.StackUtil;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import ru.wirelesstools.MainWI;
import ru.wirelesstools.Reference;
import ru.wirelesstools.utils.Utilities;

import java.util.List;

public class MultiVisor extends ItemArmorUtility implements IElectricItem, INightVisionAuto, IEnergyContainerItem {
    
    public MultiVisor(String name) {
        super(null, null, EntityEquipmentSlot.HEAD);
        this.setMaxDamage(27);
        this.setMaxStackSize(1);
        this.setCreativeTab(MainWI.tab);
        this.setNoRepair();
        BlocksItems.registerItem(this, new ResourceLocation(Reference.MOD_ID, name)).setUnlocalizedName(name);
    }
    
    @SideOnly(value = Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            switch(nbt.getShort("nvmode")) {
                case 0:
                    tooltip.add(TextFormatting.RED + Localization.translate("wi.info.multivisor.nv.off"));
                    break;
                case 1:
                    tooltip.add(TextFormatting.DARK_AQUA + Localization.translate("wi.info.multivisor.nv.auto"));
                    break;
                case 2:
                    tooltip.add(TextFormatting.DARK_GREEN + Localization.translate("wi.info.multivisor.nv.on"));
                    break;
            }
            
            if(nbt.getBoolean("showparticles"))
                tooltip.add(TextFormatting.YELLOW + Localization.translate("wi.info.multivisor.particles.on"));
            else
                tooltip.add(TextFormatting.DARK_GRAY + Localization.translate("wi.info.multivisor.particles.off"));
            
            tooltip.add(Localization.translate("wi.info.multivisor.energy.usage") + " " + 1 + " Eu/t");
        }
        else
            tooltip.add(TextFormatting.ITALIC + Localization.translate("info.wi.press.lshift"));
        
        if(Keyboard.isKeyDown(Keyboard.KEY_LMENU)) {
            tooltip.add(TextFormatting.ITALIC + Localization.translate("info.press.switchkey") + " " + KeyBinding.getDisplayString("Mode Switch Key").get()
                    + " + " + KeyBinding.getDisplayString("ALT Key").get() + Localization.translate("info.to.change.nvmode"));
            tooltip.add(TextFormatting.ITALIC + Localization.translate("info.press.altkey") + " " + KeyBinding.getDisplayString("ALT Key").get()
                    + " " + Localization.translate("info.to.toggle.particles"));
        }
        else
            tooltip.add(TextFormatting.ITALIC + Localization.translate("press.leftalt"));
        
        tooltip.add(Utilities.tooltipChargeRF(stack));
    }
    
    public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
        NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
        byte toggleTimer = nbt.getByte("toggleTimer");
        short nvMode = nbt.getShort("nvmode");
        boolean showParticles = nbt.getBoolean("showparticles");
        
        if(IC2.keyboard.isModeSwitchKeyDown(player) && IC2.keyboard.isAltKeyDown(player) && toggleTimer == 0) {
            toggleTimer = 10;
            if(!world.isRemote) {
                if(++nvMode > 2) nvMode = 0;
                nbt.setShort("nvmode", nvMode); // 0 = off, 1 = auto, 2 = on
                switch(nvMode) {
                    case 0:
                        player.sendMessage(new TextComponentTranslation("chat.multivisor.nvmode.off"));
                        break;
                    case 1:
                        player.sendMessage(new TextComponentTranslation("chat.multivisor.nvmode.auto")
                                .setStyle(new Style().setColor(TextFormatting.DARK_AQUA)));
                        break;
                    case 2:
                        player.sendMessage(new TextComponentTranslation("chat.multivisor.nvmode.on")
                                .setStyle(new Style().setColor(TextFormatting.DARK_GREEN)));
                        break;
                }
            }
        }
        
        if(IC2.keyboard.isAltKeyDown(player) && player.isSneaking() && toggleTimer == 0) {
            toggleTimer = 10;
            if(!world.isRemote) {
                showParticles = !showParticles;
                nbt.setBoolean("showparticles", showParticles);
                if(showParticles) {
                    player.sendMessage(new TextComponentTranslation("chat.multivisor.particles.on")
                            .setStyle(new Style().setColor(TextFormatting.GRAY)));
                }
                else {
                    player.sendMessage(new TextComponentTranslation("chat.multivisor.particles.off")
                            .setStyle(new Style().setColor(TextFormatting.GRAY)));
                }
            }
        }
        
        if(!world.isRemote) {
            if(ElectricItem.manager.canUse(stack, 1.0)) {
                switch(nvMode) {
                    case 1:
                        if(this.nightVisionAuto(player, showParticles) <= 8)
                            ElectricItem.manager.use(stack, 1.0, player);
                        player.inventoryContainer.detectAndSendChanges();
                        break;
                    case 2:
                        player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 302, 0, true, showParticles));
                        ElectricItem.manager.use(stack, 1.0, player);
                        player.inventoryContainer.detectAndSendChanges();
                        break;
                }
            }
        }
        
        if(IC2.platform.isSimulating() && toggleTimer > 0)
            nbt.setByte("toggleTimer", --toggleTimer);
    }
    
    @SideOnly(value = Side.CLIENT)
    public void registerModels(ItemName name) {
        ModelLoader.setCustomModelResourceLocation(this, 0,
                new ModelResourceLocation(Reference.MOD_ID + ":multivisor", null));
    }
    
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return "wirelesstools:textures/armour/multivisor.png";
    }
    
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
        if(this.isInCreativeTab(tab)) {
            ElectricItemManager.addChargeVariants(this, subItems);
        }
    }
    
    @Override
    public boolean canProvideEnergy(ItemStack itemStack) {
        return false;
    }
    
    @Override
    public double getMaxCharge(ItemStack itemStack) {
        return 2000000.0;
    }
    
    @Override
    public int getTier(ItemStack itemStack) {
        return 2;
    }
    
    @Override
    public double getTransferLimit(ItemStack itemStack) {
        return 2000.0;
    }
    
    @Override
    public int receiveEnergy(ItemStack stack, int amountRF, boolean simulate) {
        double needsEnergyEU = Utilities.needsEnergyElectricItem(stack, amountRF / 4.0, false);
        if(!simulate)
            return (int)(Utilities.chargeElectricItem(stack, needsEnergyEU, Integer.MAX_VALUE, false, false) * 4);
        return (int)(needsEnergyEU * 4);
    }
    
    @Override
    public int extractEnergy(ItemStack stack, int amount, boolean simulate) {
        return 0;
    }
    
    @Override
    public int getEnergyStored(ItemStack stack) {
        return (int)(Utilities.getCharge(stack) * 4);
    }
    
    @Override
    public int getMaxEnergyStored(ItemStack stack) {
        return (int)(this.getMaxCharge(stack) * 4);
    }
}
