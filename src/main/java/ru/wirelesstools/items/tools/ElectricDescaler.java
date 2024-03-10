package ru.wirelesstools.items.tools;

import cofh.redstoneflux.api.IEnergyContainerItem;
import ic2.api.item.ElectricItem;
import ic2.core.block.machine.tileentity.TileEntitySteamGenerator;
import ic2.core.init.BlocksItems;
import ic2.core.init.Localization;
import ic2.core.item.BaseElectricItem;
import ic2.core.ref.ItemName;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.wirelesstools.MainWI;
import ru.wirelesstools.Reference;
import ru.wirelesstools.utils.Utilities;

import java.util.List;

public class ElectricDescaler extends BaseElectricItem implements IEnergyContainerItem {
    
    public ElectricDescaler(String name) {
        super(null, 30000, 300, 2);
        this.setMaxStackSize(1);
        this.setCreativeTab(MainWI.tab);
        BlocksItems.registerItem(this, new ResourceLocation(Reference.MOD_ID, name)).setUnlocalizedName(name);
    }
    
    @SideOnly(value = Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(TextFormatting.DARK_GRAY + Localization.translate("info.electricdescaler.how.to.use"));
        tooltip.add(Utilities.tooltipChargeRF(stack));
    }
    
    @SideOnly(value = Side.CLIENT)
    public void registerModels(ItemName name) {
        ModelLoader.setCustomModelResourceLocation(this, 0,
                new ModelResourceLocation(Reference.MOD_ID + ":electricdescaler", null));
    }
    
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side,
                                           float hitX, float hitY, float hitZ, EnumHand hand) {
        if(!world.isRemote) {
            ItemStack stack = player.getHeldItem(hand);
            TileEntity tile = world.getTileEntity(pos);
            if(tile instanceof TileEntitySteamGenerator) {
                TileEntitySteamGenerator steamgen = (TileEntitySteamGenerator)tile;
                if(ElectricItem.manager.canUse(stack, 1000.0)) {
                    try {
                        int calcification = ReflectionHelper.<Integer, TileEntitySteamGenerator>getPrivateValue(TileEntitySteamGenerator.class, steamgen, "calcification");
                        if(calcification > 0) {
                            ReflectionHelper.setPrivateValue(TileEntitySteamGenerator.class, steamgen, 0, "calcification");
                            if(!player.capabilities.isCreativeMode)
                                ElectricItem.manager.use(stack, 1000.0, player);
                            player.sendMessage(new TextComponentTranslation("chat.descaler.calcification.cleared")
                                    .appendSibling(new TextComponentString(": "))
                                    .appendSibling(new TextComponentString(String.format("%.2f", (double)calcification / 1000.0) + "%").setStyle(new Style().setColor(TextFormatting.YELLOW))));
                        }
                    }
                    catch(Exception e) {
                        player.sendMessage(new TextComponentTranslation("chat.descaler.error").setStyle(new Style().setColor(TextFormatting.RED)));
                    }
                    return EnumActionResult.SUCCESS;
                }
                return EnumActionResult.PASS;
            }
            return EnumActionResult.PASS;
        }
        return EnumActionResult.PASS;
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
