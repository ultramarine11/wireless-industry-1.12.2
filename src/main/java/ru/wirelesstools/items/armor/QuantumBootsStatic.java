package ru.wirelesstools.items.armor;

import cofh.redstoneflux.api.IEnergyContainerItem;
import ic2.api.item.ElectricItem;
import ic2.api.item.IHazmatLike;
import ic2.core.IC2;
import ic2.core.init.BlocksItems;
import ic2.core.init.Localization;
import ic2.core.item.armor.ItemArmorElectric;
import ic2.core.ref.ItemName;
import ic2.core.util.StackUtil;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import ru.wirelesstools.MainWI;
import ru.wirelesstools.Reference;
import ru.wirelesstools.config.ConfigWI;
import ru.wirelesstools.utils.Utilities;

import java.util.List;

public class QuantumBootsStatic extends ItemArmorElectric implements IHazmatLike, IEnergyContainerItem {
    
    public QuantumBootsStatic(String name) {
        super(null, null, EntityEquipmentSlot.FEET, 10000000.0, 20000.0, 4);
        this.setCreativeTab(MainWI.tab);
        MinecraftForge.EVENT_BUS.register(this);
        BlocksItems.registerItem(this, new ResourceLocation(Reference.MOD_ID, name)).setUnlocalizedName(name);
    }
    
    @SideOnly(value = Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            tooltip.add(Localization.translate("info.quantumstaticboots.about"));
            tooltip.add(Localization.translate("info.quantumstaticboots.static.charge") + " " + (int)ConfigWI.chargingStaticBoots + " EU");
        }
        else
            tooltip.add(TextFormatting.ITALIC + Localization.translate("info.wi.press.lshift"));
        
        tooltip.add(Utilities.tooltipChargeRF(stack));
    }
    
    public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
        if(!world.isRemote) {
            NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
            if(!(player.isRiding() || player.isInWater()))
                this.chargeStatic(player, nbt);
            if(player.world.provider.getDimension() == 1)
                ElectricItem.manager.charge(stack, 4.0, Integer.MAX_VALUE, true, false);
        }
    }
    
    private void chargeStatic(EntityPlayer player, NBTTagCompound nbt) {
        if(!nbt.hasKey("crdX"))
            nbt.setDouble("crdX", player.posX);
        if(!nbt.hasKey("crdZ"))
            nbt.setDouble("crdZ", player.posZ);
        double distance = Math.sqrt(Math.pow(nbt.getDouble("crdX") - player.posX, 2) + Math.pow(nbt.getDouble("crdZ") - player.posZ, 2));
        if(distance < 4.0) {
            return;
        }
        nbt.setDouble("crdX", player.posX);
        nbt.setDouble("crdZ", player.posZ);
        for(ItemStack armorStack : player.inventory.armorInventory) {
            if(!armorStack.isEmpty()) {
                ElectricItem.manager.charge(armorStack,
                        distance * ConfigWI.chargingStaticBoots / 4.0, Integer.MAX_VALUE, true, false);
            }
        }
        player.inventoryContainer.detectAndSendChanges();
    }
    
    @SubscribeEvent
    public void onEntityLivingFallEvent(LivingFallEvent event) {
        if(IC2.platform.isSimulating() && event.getEntity() instanceof EntityLivingBase) {
            EntityLivingBase entity = (EntityLivingBase)event.getEntity();
            ItemStack armor = entity.getItemStackFromSlot(EntityEquipmentSlot.FEET);
            if(armor.getItem() == this) {
                int fallDamage = Math.max((int)event.getDistance() - 10, 0);
                double energyCost = this.getEnergyPerDamage() * fallDamage;
                if(energyCost <= ElectricItem.manager.getCharge(armor)) {
                    ElectricItem.manager.discharge(armor, energyCost, Integer.MAX_VALUE, true, false, false);
                    event.setCanceled(true);
                }
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return MainWI.Rarity_Multi;
    }
    
    @SideOnly(value = Side.CLIENT)
    public void registerModels(ItemName name) {
        ModelLoader.setCustomModelResourceLocation(this, 0,
                new ModelResourceLocation(Reference.MOD_ID + ":ender_quantum_static_boots", null));
    }
    
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return "wirelesstools:textures/armour/ender_quantum_static.png";
    }
    
    public ArmorProperties getProperties(EntityLivingBase entity, ItemStack armor, DamageSource source, double damage, int slot) {
        int damageLimit = (int)Math.min(Integer.MAX_VALUE, 25.0D * ElectricItem.manager.getCharge(armor) / (double)this.getEnergyPerDamage());
        if(source == DamageSource.FALL) {
            if(this.armorType == EntityEquipmentSlot.FEET) {
                return new ArmorProperties(10, 1.0D, damageLimit);
            }
        }
        return new ArmorProperties(8, this.getBaseAbsorptionRatio() * this.getDamageAbsorptionRatio(), damageLimit);
    }
    
    @Override
    public double getDamageAbsorptionRatio() {
        return 1.0;
    }
    
    @Override
    public int getEnergyPerDamage() {
        return 20000;
    }
    
    @Override
    public boolean addsProtection(EntityLivingBase entityLivingBase, EntityEquipmentSlot entityEquipmentSlot, ItemStack stack) {
        return ElectricItem.manager.getCharge(stack) > 0.0;
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
