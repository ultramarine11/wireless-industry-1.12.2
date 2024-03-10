package ru.wirelesstools.items.weapon;

import cofh.redstoneflux.api.IEnergyContainerItem;
import ic2.api.item.ElectricItem;
import ic2.core.IC2;
import ic2.core.init.BlocksItems;
import ic2.core.init.Localization;
import ic2.core.item.armor.ItemArmorElectric;
import ic2.core.item.tool.HarvestLevel;
import ic2.core.item.tool.ItemElectricTool;
import ic2.core.item.tool.ToolClass;
import ic2.core.ref.ItemName;
import ic2.core.util.StackUtil;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
import ru.wirelesstools.config.ConfigWI;
import ru.wirelesstools.utils.GUIUtility;
import ru.wirelesstools.utils.Utilities;

import java.util.EnumSet;
import java.util.List;

public class QuantumEnergyAbsorptionSword extends ItemElectricTool implements IEnergyContainerItem {
    
    public QuantumEnergyAbsorptionSword(String name) {
        super(null, 3000, HarvestLevel.Iridium, EnumSet.of(ToolClass.Sword));
        this.maxCharge = 500000;
        this.transferLimit = 4000;
        this.tier = 4;
        this.setCreativeTab(MainWI.tab);
        BlocksItems.registerItem(this, new ResourceLocation(Reference.MOD_ID, name)).setUnlocalizedName(name);
    }
    
    @SideOnly(value = Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            tooltip.add(TextFormatting.DARK_GRAY + Localization.translate("info.quantumsword.about"));
            //tooltip.add(TextFormatting.DARK_GRAY + Localization.translate("info.quantumsword.wip"));
        }
        else
            tooltip.add(TextFormatting.ITALIC + Localization.translate("info.wi.press.lshift"));
        
        tooltip.add(Utilities.tooltipChargeRF(stack));
    }
    
    @SideOnly(value = Side.CLIENT)
    public void registerModels(ItemName name) {
        ModelLoader.setCustomModelResourceLocation(this, 0,
                new ModelResourceLocation(Reference.MOD_ID + ":absorbingsword", null));
    }
    
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }
    
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase source) {
        NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
        if(IC2.platform.isSimulating()) {
            if(ElectricItem.manager.use(stack, 1000.0, source)) {
                if(source instanceof EntityPlayer) {
                    EntityPlayer playerSource = (EntityPlayer)source;
                    boolean sourceFoundElectric = false;
                    for(ItemStack armorStack : playerSource.inventory.armorInventory) {
                        if(armorStack.getItem() instanceof ItemArmorElectric) {
                            sourceFoundElectric = true;
                            break;
                        }
                    }
                    if(sourceFoundElectric) {
                        double chargeSum = 0.0;
                        boolean targetFoundElectric = false;
                        if(target instanceof EntityPlayer) {
                            EntityPlayer playerTarget = (EntityPlayer)target;
                            for(ItemStack armorStack : playerTarget.inventory.armorInventory) {
                                if(armorStack.getItem() instanceof ItemArmorElectric) {
                                    chargeSum += ElectricItem.manager.discharge(armorStack, ConfigWI.energyEUStolenSword, Integer.MAX_VALUE, true, false, false);
                                    if(!targetFoundElectric)
                                        targetFoundElectric = true;
                                }
                            }
                        }
                        else if(target instanceof EntityLiving) {
                            EntityLiving entityLivingTarget = (EntityLiving)target;
                            for(ItemStack armorStack : entityLivingTarget.getArmorInventoryList()) {
                                if(armorStack.getItem() instanceof ItemArmorElectric) {
                                    chargeSum += ElectricItem.manager.discharge(armorStack, ConfigWI.energyEUStolenSword, Integer.MAX_VALUE, true, false, false);
                                    if(!targetFoundElectric)
                                        targetFoundElectric = true;
                                }
                            }
                        }
                        if(targetFoundElectric) {
                            if(chargeSum > 0.0) {
                                this.absorbEnergyEU(playerSource, chargeSum);
                                nbt.setBoolean("afterHit", true);
                                nbt.setByte("hitTimer", (byte)60);
                                nbt.setDouble("chargeSum", nbt.getDouble("chargeSum") + chargeSum);
                            }
                            else
                                playerSource.sendMessage(new TextComponentTranslation("wi.message.target.armor.empty"));
                        }
                        else
                            playerSource.sendMessage(new TextComponentTranslation("wi.message.target.not.electric.armor"));
                    }
                    else {
                        playerSource.sendMessage(new TextComponentTranslation("wi.message.your.armorlist.empty"));
                        return true;
                    }
                }
            }
        }
        
        if(IC2.platform.isRendering()) {
            IC2.platform.playSoundSp(this.getRandomSwingSound(), 1.0F, 1.0F);
        }
        
        return true;
    }
    
    private void absorbEnergyEU(EntityPlayer source, double energySum) {
        int armorCount = 0;
        for(ItemStack sourceArmorStack : source.inventory.armorInventory) {
            if(sourceArmorStack.getItem() instanceof ItemArmorElectric) armorCount++;
        }
        if(armorCount > 0) {
            double energyPerArmor = Math.floor(energySum / armorCount);
            for(ItemStack armorStack : source.inventory.armorInventory) {
                if(armorStack.getItem() instanceof ItemArmorElectric) {
                    ElectricItem.manager.charge(armorStack, energyPerArmor, Integer.MAX_VALUE, true, false);
                }
            }
        }
    }
    
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean flagEquipped) {
        super.onUpdate(stack, world, entity, itemSlot, flagEquipped);
        NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
        if(!world.isRemote) {
            if(entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer)entity;
                byte timer = nbt.getByte("hitTimer");
                if(timer > 0)
                    nbt.setByte("hitTimer", --timer);
                
                if(nbt.getBoolean("afterHit") && (nbt.getByte("hitTimer") == 0)) {
                    player.sendMessage(
                            new TextComponentTranslation("wi.message.sword.absorbed").setStyle(new Style().setColor(TextFormatting.GREEN))
                                    .appendSibling(new TextComponentString(": " + GUIUtility.formatNumber(nbt.getDouble("chargeSum")) + " EU")
                                            .setStyle(new Style().setColor(TextFormatting.GREEN))));
                    nbt.setBoolean("afterHit", false);
                    nbt.setDouble("chargeSum", 0.0);
                }
            }
        }
    }
    
    private String getRandomSwingSound() {
        switch(IC2.random.nextInt(3)) {
            case 1:
                return "Tools/Nanosabre/NanosabreSwing2.ogg";
            case 2:
                return "Tools/Nanosabre/NanosabreSwing3.ogg";
            default:
                return "Tools/Nanosabre/NanosabreSwing1.ogg";
        }
    }
    
    public boolean isFull3D() {
        return true;
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
