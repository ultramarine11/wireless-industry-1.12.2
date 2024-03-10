package ru.wirelesstools.items.tools;

import cofh.redstoneflux.api.IEnergyContainerItem;
import com.denfop.componets.AdvEnergy;
import com.denfop.tiles.base.TileEntityInventory;
import ic2.api.energy.EnergyNet;
import ic2.api.item.ElectricItem;
import ic2.core.IC2;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.comp.Energy;
import ic2.core.init.BlocksItems;
import ic2.core.init.Localization;
import ic2.core.item.BaseElectricItem;
import ic2.core.ref.ItemName;
import ic2.core.util.StackUtil;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
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
import ru.wirelesstools.utils.Utilities;

import java.util.List;

public class PortableElectricCharger extends BaseElectricItem implements IEnergyContainerItem {
    
    public PortableElectricCharger(String name) {
        super(null, 80000000.0, 20000.0, 4);
        this.setCreativeTab(MainWI.tab);
        BlocksItems.registerItem(this, new ResourceLocation(Reference.MOD_ID, name)).setUnlocalizedName(name);
    }
    
    @SideOnly(value = Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
            tooltip.add(Localization.translate("info.wi.qwcharger.about"));
            tooltip.add(Localization.translate("info.wi.qwcharger.radius") + ": " + nbt.getInteger("radius")
                    + " " + Localization.translate("info.wi.blocks"));
            switch(nbt.getByte("qwchmode")) { // 0 = 4096, 1 = 8192, 2 = 16384, 3 = 32768, 4 = 65536, 5 = as sink tier
                case 0:
                    tooltip.add(Localization.translate("info.wi.qwcharger.charge.rate") + ": 4096 EU/t");
                    break;
                case 1:
                    tooltip.add(Localization.translate("info.wi.qwcharger.charge.rate") + ": 8192 EU/t");
                    break;
                case 2:
                    tooltip.add(Localization.translate("info.wi.qwcharger.charge.rate") + ": 16384 EU/t");
                    break;
                case 3:
                    tooltip.add(Localization.translate("info.wi.qwcharger.charge.rate") + ": 32768 EU/t");
                    break;
                case 4:
                    tooltip.add(Localization.translate("info.wi.qwcharger.charge.rate") + ": 65536 EU/t");
                    break;
                case 5:
                    tooltip.add(Localization.translate("info.wi.qwcharger.charge.rate") + ": "
                            + Localization.translate("info.wi.qwcharger.sink.tier"));
                    break;
            }
            tooltip.add(Localization.translate("info.wi.qwcharger.percentage") + ": "
                    + String.format("%.1f", 100.0 * ElectricItem.manager.getCharge(stack) / this.getMaxCharge(stack)) + " %");
            
            tooltip.add(nbt.getBoolean("enabled_on") ? TextFormatting.GREEN + Localization.translate("info.wi.qwcharger.on")
                    : TextFormatting.RED + Localization.translate("info.wi.qwcharger.off"));
        }
        else
            tooltip.add(TextFormatting.ITALIC + Localization.translate("info.wi.press.lshift"));
        
        if(Keyboard.isKeyDown(Keyboard.KEY_LMENU)) {
            tooltip.add(Localization.translate("qwch.tooltip.change.radius") + " "
                    + KeyBinding.getDisplayString("Mode Switch Key").get() + " + "
                    + Localization.translate("switcher.tooltip.press.rmb"));
            
            tooltip.add(Localization.translate("qwch.tooltip.change.rate") + " "
                    + KeyBinding.getDisplayString("ALT Key").get() + " + "
                    + Localization.translate("switcher.tooltip.press.rmb"));
            
            tooltip.add(Localization.translate("qwch.tooltip.toggle") + " "
                    + Localization.translate("switcher.tooltip.press.rmb") + " + "
                    + KeyBinding.getDisplayString("key.sneak").get());
            
        }
        else
            tooltip.add(TextFormatting.ITALIC + Localization.translate("press.leftalt"));
        
        tooltip.add(Utilities.tooltipChargeRF(stack));
    }
    
    @SideOnly(value = Side.CLIENT)
    public void registerModels(ItemName name) {
        ModelResourceLocation model_100_66 = new ModelResourceLocation(Reference.MOD_ID + ":qwcharger_full", null);
        ModelResourceLocation model_66_33 = new ModelResourceLocation(Reference.MOD_ID + ":qwcharger_med", null);
        ModelResourceLocation model_33_0 = new ModelResourceLocation(Reference.MOD_ID + ":qwcharger_low", null);
        ModelLoader.setCustomMeshDefinition(this, (stack) -> {
            int chargeLevel = (int)Math.round(StackUtil.getOrCreateNbtData(stack).getDouble("charge") / this.getMaxCharge(stack) * 100);
            if(chargeLevel >= 66)
                return model_100_66;
            else if(chargeLevel >= 33)
                return model_66_33;
            else
                return model_33_0;
        });
        ModelBakery.registerItemVariants(this, model_100_66);
        ModelBakery.registerItemVariants(this, model_66_33);
        ModelBakery.registerItemVariants(this, model_33_0);
    }
    
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        NBTTagCompound nbt = Utilities.getNBTOfItemInHand(player, hand);
        if(!world.isRemote) {
            if(player.isSneaking()) {
                boolean enabled = nbt.getBoolean("enabled_on");
                enabled = !enabled;
                nbt.setBoolean("enabled_on", enabled);
                if(enabled)
                    player.sendMessage(new TextComponentTranslation("wi.message.qwcharger.on")
                            .setStyle(new Style().setColor(TextFormatting.GREEN)));
                else
                    player.sendMessage(new TextComponentTranslation("wi.message.qwcharger.off")
                            .setStyle(new Style().setColor(TextFormatting.RED)));
            }
        }
        
        if(IC2.keyboard.isModeSwitchKeyDown(player)) {
            if(!world.isRemote) {
                int radius = nbt.getInteger("radius");
                if(++radius > 15) radius = 1;
                nbt.setInteger("radius", radius);
                player.sendMessage(new TextComponentTranslation("wi.message.qwcharger.radius")
                        .appendSibling(new TextComponentString(": " + radius + " "))
                        .appendSibling(new TextComponentTranslation("wi.message.qwcharger.blocks")));
            }
        }
        
        if(IC2.keyboard.isAltKeyDown(player)) {
            if(!world.isRemote) {
                byte mode = nbt.getByte("qwchmode");
                if(++mode > 5) mode = 0; // 0 = 4096, 1 = 8192, 2 = 16384, 3 = 32768, 4 = 65536, 5 = as sink tier
                nbt.setByte("qwchmode", mode);
                switch(mode) {
                    case 0:
                        player.sendMessage(new TextComponentTranslation("wi.message.qwcharger.rate")
                                .appendSibling(new TextComponentString(": 4096 EU/t")));
                        break;
                    case 1:
                        player.sendMessage(new TextComponentTranslation("wi.message.qwcharger.rate")
                                .appendSibling(new TextComponentString(": 8192 EU/t")));
                        break;
                    case 2:
                        player.sendMessage(new TextComponentTranslation("wi.message.qwcharger.rate")
                                .appendSibling(new TextComponentString(": 16384 EU/t")));
                        break;
                    case 3:
                        player.sendMessage(new TextComponentTranslation("wi.message.qwcharger.rate")
                                .appendSibling(new TextComponentString(": 32768 EU/t")));
                        break;
                    case 4:
                        player.sendMessage(new TextComponentTranslation("wi.message.qwcharger.rate")
                                .appendSibling(new TextComponentString(": 65536 EU/t")));
                        break;
                    case 5:
                        player.sendMessage(new TextComponentTranslation("wi.message.qwcharger.rate")
                                .appendSibling(new TextComponentString(": "))
                                .appendSibling(new TextComponentTranslation("info.wi.qwcharger.sink.tier")));
                        break;
                }
            }
        }
        
        return super.onItemRightClick(world, player, hand);
    }
    
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        if(!world.isRemote)
            StackUtil.getOrCreateNbtData(stack).setInteger("radius", 10);
    }
    
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
        if(this.isInCreativeTab(tab)) {
            ItemStack emptyQWCh = new ItemStack(this);
            ItemStack fullQWCh = new ItemStack(this);
            ElectricItem.manager.charge(emptyQWCh, 0.0, 2147483647, true, false);
            ElectricItem.manager.charge(fullQWCh, Double.MAX_VALUE, 2147483647, true, false);
            StackUtil.getOrCreateNbtData(emptyQWCh).setInteger("radius", 10);
            StackUtil.getOrCreateNbtData(fullQWCh).setInteger("radius", 10);
            subItems.add(emptyQWCh);
            subItems.add(fullQWCh);
        }
    }
    
    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if(!world.isRemote)
            if(entity instanceof EntityPlayer) {
                NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
                EntityPlayer player = (EntityPlayer)entity;
                if(nbt.getBoolean("enabled_on"))
                    for(BlockPos.MutableBlockPos pos : Utilities.getMutablePosesInRadius(player.getPosition(), nbt.getInteger("radius"))) {
                        TileEntity te = world.getTileEntity(pos);
                        if(te instanceof TileEntityBlock) {
                            TileEntityBlock teBlock = (TileEntityBlock)te;
                            if(teBlock instanceof TileEntityInventory) {
                                TileEntityInventory teInv = (TileEntityInventory)teBlock;
                                if(teInv.hasComp(AdvEnergy.class)) {
                                    AdvEnergy advEnergy = teInv.getComp(AdvEnergy.class);
                                    if(!advEnergy.getSinkDirs().isEmpty() && advEnergy.getSourceDirs().isEmpty()) {
                                        if(advEnergy.getFreeEnergy() > 0.0) {
                                            double toSend = 0.0;
                                            switch(nbt.getByte("qwchmode")) {
                                                case 0:
                                                    toSend = Math.min(ElectricItem.manager.getCharge(stack), 4096.0);
                                                    break;
                                                case 1:
                                                    toSend = Math.min(ElectricItem.manager.getCharge(stack), 8192.0);
                                                    break;
                                                case 2:
                                                    toSend = Math.min(ElectricItem.manager.getCharge(stack), 16384.0);
                                                    break;
                                                case 3:
                                                    toSend = Math.min(ElectricItem.manager.getCharge(stack), 32768.0);
                                                    break;
                                                case 4:
                                                    toSend = Math.min(ElectricItem.manager.getCharge(stack), 65536.0);
                                                    break;
                                                case 5:
                                                    toSend = Math.min(ElectricItem.manager.getCharge(stack),
                                                            EnergyNet.instance.getPowerFromTier(advEnergy.getSinkTier()));
                                                    break;
                                            }
                                            if(toSend > 0.0)
                                                ElectricItem.manager.discharge(stack,
                                                        advEnergy.addEnergy(toSend), Integer.MAX_VALUE, true, false, false);
                                        }
                                    }
                                }
                            }
                            else if(teBlock.hasComponent(Energy.class)) {
                                Energy energy = teBlock.getComponent(Energy.class);
                                if(!energy.getSinkDirs().isEmpty() && energy.getSourceDirs().isEmpty() && !energy.isMultiSource()) {
                                    if(energy.getFreeEnergy() > 0.0) {
                                        double toSend = 0.0;
                                        switch(nbt.getByte("qwchmode")) {
                                            case 0:
                                                toSend = Math.min(ElectricItem.manager.getCharge(stack), 4096.0);
                                                break;
                                            case 1:
                                                toSend = Math.min(ElectricItem.manager.getCharge(stack), 8192.0);
                                                break;
                                            case 2:
                                                toSend = Math.min(ElectricItem.manager.getCharge(stack), 16384.0);
                                                break;
                                            case 3:
                                                toSend = Math.min(ElectricItem.manager.getCharge(stack), 32768.0);
                                                break;
                                            case 4:
                                                toSend = Math.min(ElectricItem.manager.getCharge(stack), 65536.0);
                                                break;
                                            case 5:
                                                toSend = Math.min(ElectricItem.manager.getCharge(stack),
                                                        EnergyNet.instance.getPowerFromTier(energy.getSinkTier()));
                                                break;
                                        }
                                        if(toSend > 0.0)
                                            ElectricItem.manager.discharge(stack,
                                                    energy.addEnergy(toSend), Integer.MAX_VALUE, true, false, false);
                                    }
                                }
                            }
                            /*if(teBlock.hasComponent(Energy.class)) {
                                Energy energy = teBlock.getComponent(Energy.class);
                                if(!energy.getSinkDirs().isEmpty() && energy.getSourceDirs().isEmpty() && !energy.isMultiSource()) {
                                    if(energy.getFreeEnergy() > 0.0) {
                                        double toSend = 0.0;
                                        switch(nbt.getByte("qwchmode")) {
                                            case 0:
                                                toSend = Math.min(ElectricItem.manager.getCharge(stack), 4096.0);
                                                break;
                                            case 1:
                                                toSend = Math.min(ElectricItem.manager.getCharge(stack), 8192.0);
                                                break;
                                            case 2:
                                                toSend = Math.min(ElectricItem.manager.getCharge(stack), 16384.0);
                                                break;
                                            case 3:
                                                toSend = Math.min(ElectricItem.manager.getCharge(stack), 32768.0);
                                                break;
                                            case 4:
                                                toSend = Math.min(ElectricItem.manager.getCharge(stack), 65536.0);
                                                break;
                                            case 5:
                                                toSend = Math.min(ElectricItem.manager.getCharge(stack),
                                                        EnergyNet.instance.getPowerFromTier(energy.getSinkTier()));
                                                break;
                                        }
                                        if(toSend > 0.0)
                                            ElectricItem.manager.discharge(stack,
                                                    energy.addEnergy(toSend), Integer.MAX_VALUE, true, false, false);
                                    }
                                }
                            }
                            if(teBlock.hasComponent(AdvEnergy.class)) {
                                AdvEnergy advEnergy = teBlock.getComponent(AdvEnergy.class);
                                if(!advEnergy.getSinkDirs().isEmpty() && advEnergy.getSourceDirs().isEmpty()) {
                                    if(advEnergy.getFreeEnergy() > 0.0) {
                                        double toSend = 0.0;
                                        switch(nbt.getByte("qwchmode")) {
                                            case 0:
                                                toSend = Math.min(ElectricItem.manager.getCharge(stack), 4096.0);
                                                break;
                                            case 1:
                                                toSend = Math.min(ElectricItem.manager.getCharge(stack), 8192.0);
                                                break;
                                            case 2:
                                                toSend = Math.min(ElectricItem.manager.getCharge(stack), 16384.0);
                                                break;
                                            case 3:
                                                toSend = Math.min(ElectricItem.manager.getCharge(stack), 32768.0);
                                                break;
                                            case 4:
                                                toSend = Math.min(ElectricItem.manager.getCharge(stack), 65536.0);
                                                break;
                                            case 5:
                                                toSend = Math.min(ElectricItem.manager.getCharge(stack),
                                                        EnergyNet.instance.getPowerFromTier(advEnergy.getSinkTier()));
                                                break;
                                        }
                                        if(toSend > 0.0)
                                            ElectricItem.manager.discharge(stack,
                                                    advEnergy.addEnergy(toSend), Integer.MAX_VALUE, true, false, false);
                                    }
                                }
                            }*/
                        }
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
