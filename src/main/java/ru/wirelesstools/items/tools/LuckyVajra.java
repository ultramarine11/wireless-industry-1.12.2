package ru.wirelesstools.items.tools;

import cofh.redstoneflux.api.IEnergyContainerItem;
import ic2.api.item.ElectricItem;
import ic2.core.IC2;
import ic2.core.init.BlocksItems;
import ic2.core.init.Localization;
import ic2.core.item.tool.HarvestLevel;
import ic2.core.item.tool.ItemElectricTool;
import ic2.core.item.tool.ToolClass;
import ic2.core.ref.ItemName;
import ic2.core.util.StackUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
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
import ru.wirelesstools.config.ConfigWI;
import ru.wirelesstools.utils.Utilities;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public class LuckyVajra extends ItemElectricTool implements IEnergyContainerItem {
    
    // public static boolean fortuneVIenabled = true;
    
    public LuckyVajra(String name) {
        super(null, 3000, HarvestLevel.Iridium, EnumSet.of(ToolClass.Pickaxe, ToolClass.Shovel, ToolClass.Axe));
        this.maxCharge = ConfigWI.maxChargeVajra;
        this.transferLimit = Math.max(ConfigWI.maxChargeVajra / 400, 100); // was 150000
        this.tier = 4;
        this.efficiency = 20000.0F;
        this.setCreativeTab(MainWI.tab);
        BlocksItems.registerItem(this, new ResourceLocation(Reference.MOD_ID, name)).setUnlocalizedName(name);
    }
    
    @SideOnly(value = Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            tooltip.add(TextFormatting.ITALIC + Localization.translate("wsp.info.vajra.about1"));
            tooltip.add(TextFormatting.ITALIC + Localization.translate("wi.info.press.key") + " "
                    + KeyBinding.getDisplayString("ALT Key").get() + " " + Localization.translate("wi.info.to.switch.safemode"));
            tooltip.add(TextFormatting.GOLD + Localization.translate("wsp.info.vajra.about.extinguish"));
            /*tooltip.add(LuckyVajra.fortuneVIenabled
                    ? (TextFormatting.UNDERLINE + Localization.translate("wsp.info.vajra.fortune6"))
                    : (TextFormatting.UNDERLINE + Localization.translate("wsp.info.vajra.fortune5")));*/
        }
        else
            tooltip.add(TextFormatting.ITALIC + Localization.translate("info.wi.press.lshift"));
        
        tooltip.add(StackUtil.getOrCreateNbtData(stack).getBoolean("safemode")
                ? TextFormatting.DARK_GREEN + Localization.translate("wi.info.vajra.is.safe")
                : TextFormatting.RED + Localization.translate("wi.info.vajra.unsafe"));
        tooltip.add(Utilities.tooltipChargeRF(stack));
    }
    
    @SideOnly(value = Side.CLIENT)
    public void registerModels(ItemName name) {
        ModelResourceLocation fortune = new ModelResourceLocation(Reference.MOD_ID + ":luckyvajra_fortune", null);
        ModelResourceLocation silk_touch = new ModelResourceLocation(Reference.MOD_ID + ":luckyvajra_silktouch", null);
        ModelResourceLocation common = new ModelResourceLocation(Reference.MOD_ID + ":luckyvajra", null);
        ModelLoader.setCustomMeshDefinition(this, (stack) -> {
            Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(stack);
            if(enchants.containsKey(Enchantments.FORTUNE)) {
                return fortune;
            }
            else if(enchants.containsKey(Enchantments.SILK_TOUCH)) {
                return silk_touch;
            }
            else {
                return common;
            }
        });
        ModelBakery.registerItemVariants(this, fortune);
        ModelBakery.registerItemVariants(this, silk_touch);
        ModelBakery.registerItemVariants(this, common);
    }
    
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {
        NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
        return nbt.getBoolean("safemode") ? this.checkTE(pos, player, nbt)
                : super.onBlockStartBreak(stack, pos, player);
    }
    
    private boolean checkTE(BlockPos pos, EntityPlayer player, NBTTagCompound nbt) {
        if(!player.world.isAirBlock(pos) && player.world.getTileEntity(pos) != null) {
            if(!player.world.isRemote) {
                if(ConfigWI.enableVajraChatMsgs) {
                    player.sendMessage(new TextComponentTranslation("wi.message.cannot.break.te"));
                }
                ((EntityPlayerMP)player).connection.sendPacket(new SPacketBlockChange(player.world, pos));
            }
            return true;
        }
        return false;
    }
    
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
        if(!world.isRemote && player.isSneaking()) {
            short multiMode = nbt.getShort("enchmultimode");
            if(ElectricItem.manager.getCharge(stack) >= 200000.0) {
                if(++multiMode > 2) multiMode = 0;
            }
            else {
                if(ConfigWI.enableVajraChatMsgs)
                    player.sendMessage(new TextComponentTranslation("wi.message.cannot.enchant")
                            .setStyle(new Style().setColor(TextFormatting.RED)));
                return new ActionResult<>(EnumActionResult.PASS, stack);
            }
            nbt.setShort("enchmultimode", multiMode);
            Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(stack);
            switch(multiMode) {
                case 0:
                    enchants.remove(Enchantments.SILK_TOUCH);
                    if(ConfigWI.enableVajraChatMsgs)
                        player.sendMessage(new TextComponentTranslation("wsp.message.enchantments.off")
                                .setStyle(new Style().setColor(TextFormatting.LIGHT_PURPLE)));
                    break;
                case 1:
                    enchants.put(Enchantments.FORTUNE, ConfigWI.fortuneLevel);
                    ElectricItem.manager.use(stack, 2000.0, player);
                    if(ConfigWI.enableVajraChatMsgs)
                        player.sendMessage(new TextComponentTranslation("wsp.message.fortune.on")
                                .setStyle(new Style().setColor(TextFormatting.AQUA)));
                    break;
                case 2:
                    enchants.remove(Enchantments.FORTUNE);
                    enchants.put(Enchantments.SILK_TOUCH, 1);
                    ElectricItem.manager.use(stack, 2000.0, player);
                    if(ConfigWI.enableVajraChatMsgs)
                        player.sendMessage(new TextComponentTranslation("wsp.message.silktouch.on")
                                .setStyle(new Style().setColor(TextFormatting.GOLD)));
                    break;
            }
            
            EnchantmentHelper.setEnchantments(enchants, stack);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        
        if(IC2.keyboard.isAltKeyDown(player)) {
            if(!world.isRemote) {
                boolean safeMode = nbt.getBoolean("safemode");
                safeMode = !safeMode;
                nbt.setBoolean("safemode", safeMode);
                if(ConfigWI.enableVajraChatMsgs) {
                    if(safeMode)
                        player.sendMessage(new TextComponentTranslation("wi.message.safe.vajra")
                                .setStyle(new Style().setColor(TextFormatting.GREEN)));
                    else
                        player.sendMessage(new TextComponentTranslation("wi.message.unsafe.vajra")
                                .setStyle(new Style().setColor(TextFormatting.DARK_RED)));
                }
                
                return new ActionResult<>(EnumActionResult.SUCCESS, stack);
            }
        }
        
        return super.onItemRightClick(world, player, hand);
    }
    
    public boolean canHarvestBlock(IBlockState state, ItemStack stack) {
        return state.getBlock() != Blocks.BEDROCK;
    }
    
    public boolean hitEntity(ItemStack itemstack, EntityLivingBase target, EntityLivingBase attacker) {
        if(attacker instanceof EntityPlayer) {
            if(ElectricItem.manager.use(itemstack, this.operationEnergyCost * 2.0, attacker)) {
                target.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)attacker), 25.0F);
            }
            else {
                target.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)attacker), 1.0F);
            }
        }
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }
    
    public void onUpdate(ItemStack stack, World world, Entity entityIn, int itemSlot, boolean flagEquipped) {
        super.onUpdate(stack, world, entityIn, itemSlot, flagEquipped);
        if(!world.isRemote) {
            if(entityIn instanceof EntityPlayer && flagEquipped) {
                EntityPlayer player = (EntityPlayer)entityIn;
                if(!player.capabilities.isCreativeMode && player.isBurning()
                        && ElectricItem.manager.getCharge(stack) > 0) {
                    player.extinguish();
                    player.addPotionEffect(new PotionEffect(MobEffects.INSTANT_HEALTH, 22, 0, true, true));
                    player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 42, 0, true, true));
                    if(world.getTotalWorldTime() % 5 == 0) {
                        ElectricItem.manager.discharge(stack, 5000, Integer.MAX_VALUE, false, false, false);
                    }
                }
            }
            if(ElectricItem.manager.getCharge(stack) < 200000.0) {
                Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(stack);
                enchants.remove(Enchantments.FORTUNE);
                enchants.remove(Enchantments.SILK_TOUCH);
                EnchantmentHelper.setEnchantments(enchants, stack);
            }
        }
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
