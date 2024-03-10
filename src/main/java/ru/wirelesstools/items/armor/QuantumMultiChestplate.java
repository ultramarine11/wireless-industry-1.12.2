package ru.wirelesstools.items.armor;

import com.mojang.authlib.GameProfile;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.IHazmatLike;
import ic2.core.IC2;
import ic2.core.init.BlocksItems;
import ic2.core.init.Localization;
import ic2.core.item.armor.ItemArmorElectric;
import ic2.core.item.tool.ItemDebug;
import ic2.core.ref.ItemName;
import ic2.core.util.StackUtil;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import ru.wirelesstools.MainWI;
import ru.wirelesstools.Reference;
import ru.wirelesstools.config.ConfigWI;
import ru.wirelesstools.utils.ExperienceUtil;
import ru.wirelesstools.utils.Utilities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class QuantumMultiChestplate extends ItemArmorElectric implements IHazmatLike {
    
    public static int selfChargeRate;
    public static int radius;
    
    public QuantumMultiChestplate(String name) {
        super(null, null, EntityEquipmentSlot.CHEST, ConfigWI.maxChargeQChestPlate, 500000.0, 4);
        this.setCreativeTab(MainWI.tab);
        MinecraftForge.EVENT_BUS.register(this);
        BlocksItems.registerItem(this, new ResourceLocation(Reference.MOD_ID, name)).setUnlocalizedName(name);
    }
    
    @SideOnly(value = Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            tooltip.add(TextFormatting.ITALIC + Localization.translate("info.press.switch") + " " + KeyBinding.getDisplayString("Mode Switch Key").get()
                    + " " + Localization.translate("info.and.sneak.to.changemode"));
            tooltip.add(TextFormatting.ITALIC + Localization.translate("info.press.alt") + " " + KeyBinding.getDisplayString("ALT Key").get()
                    + " " + Localization.translate("info.and.sneak.to.changeprivacy"));
            tooltip.add(TextFormatting.ITALIC + Localization.translate("info.press.boost") + " " + KeyBinding.getDisplayString("Boost Key").get()
                    + " " + Localization.translate("info.and.sneak.to.capitalization"));
            
            if(!ConfigWI.enableExpCapitalization) {
                tooltip.add(TextFormatting.DARK_RED + Localization.translate("wi.info.capitalization.disabled"));
            }
            else if(!nbt.getBoolean("enableCapitalization")) {
                tooltip.add(TextFormatting.RED + Localization.translate("wi.info.capitalization.off"));
            }
            else {
                tooltip.add(Localization.translate("info.xp.capitalization") + ": " + String.format("%.0f", (double)nbt.getShort("capitalization") / ConfigWI.xpCapitalizationTicks * 100.0) + " %");
            }
        }
        else
            tooltip.add(TextFormatting.ITALIC + Localization.translate("info.wi.press.lshift"));
        
        switch(nbt.getShort("chestplatemode")) {
            case 0:
                tooltip.add(Localization.translate("wi.info.multiqchestplate.mode.common"));
                break;
            case 1:
                tooltip.add(TextFormatting.GREEN + Localization.translate("wi.info.multiqchestplate.mode.wireless"));
                break;
            case 2:
                tooltip.add(TextFormatting.YELLOW + Localization.translate("wi.info.multiqchestplate.mode.buffs"));
                break;
            case 3:
                tooltip.add(TextFormatting.DARK_AQUA + Localization.translate("wi.info.multiqchestplate.mode.recharge")
                        + ", " + Localization.translate("wi.info.rate.eu") + " " + selfChargeRate + " Eu/t");
                break;
            case 4:
                tooltip.add(TextFormatting.DARK_RED + Localization.translate("wi.info.multiqchestplate.mode.heal.around"));
                break;
        }
    }
    
    public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
        NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
        byte toggleTimer = nbt.getByte("toggleTimer");
        short chestplatemode = nbt.getShort("chestplatemode");
        boolean privacymode = nbt.getBoolean("privacymode");
        short capitalizationTickCounter = nbt.getShort("capitalization");
        boolean enableCapitalization = nbt.getBoolean("enableCapitalization");
        
        if(!world.isRemote) {
            if(NBTUtil.readGameProfileFromNBT(nbt.getCompoundTag("ownerGameProfile")) == null) {
                NBTTagCompound ownerNbt = new NBTTagCompound();
                NBTUtil.writeGameProfile(ownerNbt, player.getGameProfile());
                nbt.setTag("ownerGameProfile", ownerNbt);
            }
        }
        
        if(IC2.keyboard.isBoostKeyDown(player) && player.isSneaking() && toggleTimer == 0) {
            toggleTimer = 10;
            if(!world.isRemote) {
                exitlabel:
                {
                    if(privacymode) {
                        if(NBTUtil.readGameProfileFromNBT(nbt.getCompoundTag("ownerGameProfile")) != null
                                && !NBTUtil.readGameProfileFromNBT(nbt.getCompoundTag("ownerGameProfile")).equals(player.getGameProfile())) {
                            break exitlabel;
                        }
                    }
                    enableCapitalization = !enableCapitalization;
                    nbt.setBoolean("enableCapitalization", enableCapitalization);
                    if(enableCapitalization) {
                        player.sendMessage(new TextComponentTranslation("chat.multiqchestplate.capitalization.on").setStyle(new Style().setColor(TextFormatting.AQUA)));
                    }
                    else {
                        player.sendMessage(new TextComponentTranslation("chat.multiqchestplate.capitalization.off").setStyle(new Style().setColor(TextFormatting.LIGHT_PURPLE)));
                    }
                }
            }
        }
        
        if(IC2.keyboard.isAltKeyDown(player) && player.isSneaking() && toggleTimer == 0) {
            toggleTimer = 10;
            if(!world.isRemote) {
                if(NBTUtil.readGameProfileFromNBT(nbt.getCompoundTag("ownerGameProfile")) != null
                        && NBTUtil.readGameProfileFromNBT(nbt.getCompoundTag("ownerGameProfile")).equals(player.getGameProfile())) {
                    privacymode = !privacymode;
                    nbt.setBoolean("privacymode", privacymode);
                    if(privacymode) {
                        player.sendMessage(new TextComponentTranslation("chat.multiqchestplate.privacy.on").setStyle(new Style().setColor(TextFormatting.BLUE)));
                    }
                    else {
                        player.sendMessage(new TextComponentTranslation("chat.multiqchestplate.privacy.off").setStyle(new Style().setColor(TextFormatting.DARK_PURPLE)));
                    }
                }
                else {
                    player.sendMessage(new TextComponentTranslation("chat.multiqchestplate.cannot.change.privacy").setStyle(new Style().setColor(TextFormatting.RED)));
                }
            }
        }
        
        if(IC2.keyboard.isModeSwitchKeyDown(player) && player.isSneaking() && toggleTimer == 0) {
            toggleTimer = 10;
            if(!world.isRemote) {
                exitlabel:
                {
                    if(privacymode) {
                        if(NBTUtil.readGameProfileFromNBT(nbt.getCompoundTag("ownerGameProfile")) != null
                                && !NBTUtil.readGameProfileFromNBT(nbt.getCompoundTag("ownerGameProfile")).equals(player.getGameProfile())) {
                            break exitlabel;
                        }
                    }
                    
                    if(++chestplatemode > 4)
                        chestplatemode = 0;
                    nbt.setShort("chestplatemode", chestplatemode); // 0 = none, 1 = wireless charge, 2 = buffs, 3 = self-charge, 4 = healing
                    switch(chestplatemode) {
                        case 0:
                            player.sendMessage(new TextComponentTranslation("chat.multiqchestplate.mode.common"));
                            break;
                        case 1:
                            player.sendMessage(new TextComponentTranslation("chat.multiqchestplate.mode.wireless")
                                    .setStyle(new Style().setColor(TextFormatting.GREEN)));
                            break;
                        case 2:
                            player.sendMessage(new TextComponentTranslation("chat.multiqchestplate.mode.buffs")
                                    .setStyle(new Style().setColor(TextFormatting.YELLOW)));
                            break;
                        case 3:
                            player.sendMessage(new TextComponentTranslation("chat.multiqchestplate.mode.recharge")
                                    .setStyle(new Style().setColor(TextFormatting.DARK_AQUA)));
                            break;
                        case 4:
                            player.sendMessage(new TextComponentTranslation("chat.multiqchestplate.mode.heal.around")
                                    .setStyle(new Style().setColor(TextFormatting.DARK_RED)));
                            break;
                    }
                }
            }
        }
        
        if(!world.isRemote) {
            exitlabel:
            {
                if(privacymode) {
                    if(NBTUtil.readGameProfileFromNBT(nbt.getCompoundTag("ownerGameProfile")) != null
                            && !NBTUtil.readGameProfileFromNBT(nbt.getCompoundTag("ownerGameProfile")).equals(player.getGameProfile()))
                        break exitlabel;
                }
                
                switch(chestplatemode) {
                    case 1:
                        this.checkAndChargePlayers(player, world, stack);
                        break;
                    case 2:
                        this.selfBuff(player, stack, privacymode);
                        break;
                    case 3:
                        ElectricItem.manager.charge(stack, selfChargeRate, Integer.MAX_VALUE, false, false);
                        break;
                    case 4:
                        if(ElectricItem.manager.getCharge(stack) > 50)
                            this.healAllPlayersAround(player, world, stack);
                        break;
                }
                
                if(ConfigWI.enableExpCapitalization && enableCapitalization && ElectricItem.manager.getCharge(stack) > 80000) {
                    capitalizationTickCounter++;
                    ElectricItem.manager.discharge(stack, 80000, Integer.MAX_VALUE, true, false, false);
                    if(capitalizationTickCounter >= ConfigWI.xpCapitalizationTicks) {
                        capitalizationTickCounter = 0;
                        player.addExperience((int)Math.ceil(Utilities.percentageNumber(ExperienceUtil.getPlayerXP(player), ConfigWI.xpCapitalizationPercent)));
                    }
                    nbt.setShort("capitalization", capitalizationTickCounter);
                }
            }
        }
        
        if(IC2.platform.isSimulating() && toggleTimer > 0)
            nbt.setByte("toggleTimer", --toggleTimer);
    }
    
    protected void checkAndChargePlayers(EntityPlayer player, World world, ItemStack thisarmor) {
        List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(player, new AxisAlignedBB(player.posX - radius, player.posY - radius,
                player.posZ - radius, player.posX + radius, player.posY + radius, player.posZ + radius));
        for(Entity entityInList : list) {
            if(entityInList instanceof EntityPlayer) {
                EntityPlayer playerInList = (EntityPlayer)entityInList;
                for(ItemStack current : Utilities.getAllPlayerInventory(playerInList)) {
                    if(current.getItem() instanceof IElectricItem && !(current.getItem() instanceof ItemDebug)) {
                        if(ElectricItem.manager.getCharge(thisarmor) > 0)
                            ElectricItem.manager.discharge(thisarmor,
                                    ElectricItem.manager.charge(current, Integer.MAX_VALUE, Integer.MAX_VALUE, false, false),
                                    Integer.MAX_VALUE, true, false, false);
                    }
                }
            }
        }
    }
    
    protected void selfBuff(EntityPlayer player, ItemStack thisArmor, boolean isPrivate) {
        List<PotionEffect> lst = new ArrayList<>(player.getActivePotionEffects());
        if(!lst.isEmpty()) {
            for(Iterator<PotionEffect> iterator = lst.iterator(); iterator.hasNext(); ) {
                PotionEffect effect = iterator.next();
                Potion potion = effect.getPotion();
                if(potion.isBadEffect() && ElectricItem.manager.getCharge(thisArmor) > 500) {
                    player.removePotionEffect(potion);
                    ElectricItem.manager.discharge(thisArmor, 500, Integer.MAX_VALUE, true, false, false);
                }
            }
        }
        /*if(!player.getActivePotionEffects().isEmpty()) {
            for(PotionEffect effect : player.getActivePotionEffects()) {
                Potion potion = effect.getPotion();
                if(potion.isBadEffect() && ElectricItem.manager.getCharge(thisArmor) > 500) {
                    player.removePotionEffect(potion);
                    ElectricItem.manager.discharge(thisArmor, 500, Integer.MAX_VALUE, true, false, false);
                }
            }
        }*/
        
        if(ElectricItem.manager.getCharge(thisArmor) > 500) {
            player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 45, ConfigWI.chestplateSpeedAmplifier, true, !isPrivate));
            player.addPotionEffect(new PotionEffect(MobEffects.HASTE, 45, ConfigWI.chestplateHasteAmplifier, true, !isPrivate));
            player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 45, ConfigWI.chestplateStrengthAmplifier, true, !isPrivate));
            player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 45, ConfigWI.chestplateJumpAmplifier, true, !isPrivate));
            player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 45, ConfigWI.chestplateResistanceAmplifier, true, !isPrivate));
            player.addPotionEffect(new PotionEffect(MobEffects.LUCK, 45, ConfigWI.chestplateLuckAmplifier, true, !isPrivate));
            
            ElectricItem.manager.discharge(thisArmor, 500, Integer.MAX_VALUE, true, false, false);
        }
    }
    
    protected void healAllPlayersAround(EntityPlayer player, World world, ItemStack thisarmor) {
        List<EntityPlayer> list = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(player.posX - radius, player.posY - radius,
                player.posZ - radius, player.posX + radius, player.posY + radius, player.posZ + radius));
        for(EntityPlayer playerInList : list) {
            if(playerInList != null && ElectricItem.manager.getCharge(thisarmor) > 500) {
                playerInList.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 62, ConfigWI.chestplateHealAmplifier, true, true));
                ElectricItem.manager.discharge(thisarmor, 500, Integer.MAX_VALUE, true, false, false);
            }
        }
    }
    
    @Override
    public double getDamageAbsorptionRatio() {
        return 1.1;
    }
    
    public ArmorProperties getProperties(EntityLivingBase entity, ItemStack armor, DamageSource source, double damage, int slot) {
        int damageLimit = (int)Math.min(Integer.MAX_VALUE, 25.0 * ElectricItem.manager.getCharge(armor) / (double)this.getEnergyPerDamage());
        return new ArmorProperties(8, this.getBaseAbsorptionRatio() * this.getDamageAbsorptionRatio(), damageLimit);
    }
    
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return MainWI.Rarity_Multi;
    }
    
    @Override
    public int getEnergyPerDamage() {
        return 20000;
    }
    
    @Override
    public boolean canProvideEnergy(ItemStack stack) {
        return true;
    }
    
    @SideOnly(value = Side.CLIENT)
    public void registerModels(ItemName name) {
        ModelLoader.setCustomModelResourceLocation(this, 0,
                new ModelResourceLocation(Reference.MOD_ID + ":quantumchestmulti", null));
    }
    
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
        if(nbt.getBoolean("privacymode")) {
            return "wirelesstools:textures/armour/qmchestplate_private.png";
        }
        else {
            switch(nbt.getShort("chestplatemode")) {
                case 1: //wireless
                    return "wirelesstools:textures/armour/qmchestplate_wireless.png";
                case 2: //buffs
                    return "wirelesstools:textures/armour/qmchestplate_buffs.png";
                case 3: //recharge
                    return "wirelesstools:textures/armour/qmchestplate_recharge.png";
                case 4: //heal
                    return "wirelesstools:textures/armour/qmchestplate_heal.png";
                default:
                    return "wirelesstools:textures/armour/qmchestplate_common.png";
            }
        }
    }
    
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void extraTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if(stack.getItem() instanceof QuantumMultiChestplate) {
            NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
            List<String> info = event.getToolTip();
            EntityPlayer player = event.getEntityPlayer();
            GameProfile owner = NBTUtil.readGameProfileFromNBT(nbt.getCompoundTag("ownerGameProfile"));
            if(nbt.getBoolean("privacymode")) {
                if(owner == null) {
                    info.add(Localization.translate("wi.info.multiqchestplate.privacy.no.owner"));
                }
                else if(player != null && player.getGameProfile().equals(owner)) {
                    info.add(TextFormatting.DARK_GREEN + Localization.translate("wi.info.multiqchestplate.privacy.on"));
                    info.add(TextFormatting.DARK_GREEN + Localization.translate("wi.info.multiqchestplate.privacy.you.owner"));
                }
                else {
                    info.add(TextFormatting.DARK_RED + Localization.translate("wi.info.multiqchestplate.privacy.owned"));
                    info.add(TextFormatting.DARK_RED + Localization.translate("wi.info.multiqchestplate.privacy.owner.is")
                            + ": " + owner.getName());
                }
            }
            else {
                info.add(TextFormatting.YELLOW + Localization.translate("wi.info.multiqchestplate.privacy.none"));
            }
        }
    }
    
    @Override
    public boolean addsProtection(EntityLivingBase entityLivingBase, EntityEquipmentSlot entityEquipmentSlot, ItemStack stack) {
        return ElectricItem.manager.getCharge(stack) > 0.0;
    }
}
