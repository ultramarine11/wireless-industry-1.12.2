package ru.wirelesstools.items.tools;

import ic2.api.tile.IWrenchable;
import ic2.core.IC2;
import ic2.core.audio.PositionSpec;
import ic2.core.init.BlocksItems;
import ic2.core.init.Localization;
import ic2.core.init.MainConfig;
import ic2.core.item.ItemIC2;
import ic2.core.ref.ItemName;
import ic2.core.util.ConfigUtil;
import ic2.core.util.LogCategory;
import ic2.core.util.StackUtil;
import ic2.core.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import ru.wirelesstools.MainWI;
import ru.wirelesstools.Reference;

import java.util.List;

public class ItemGoldenWrench extends ItemIC2 {
    
    private static final boolean logEmptyWrenchDrops = ConfigUtil.getBool(MainConfig.get(), "debug/logEmptyWrenchDrops");
    
    public ItemGoldenWrench(String name) {
        super(null);
        this.setMaxDamage(200);
        this.setMaxStackSize(1);
        this.setCreativeTab(MainWI.tab);
        BlocksItems.registerItem(this, new ResourceLocation(Reference.MOD_ID, name)).setUnlocalizedName(name);
    }
    
    @SideOnly(value = Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(TextFormatting.DARK_GRAY + Localization.translate("info.goldenwrench.about"));
    }
    
    @SideOnly(value = Side.CLIENT)
    public void registerModels(ItemName name) {
        ModelLoader.setCustomModelResourceLocation(this, 0,
                new ModelResourceLocation(Reference.MOD_ID + ":goldenwrench", null));
    }
    
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        ItemStack stack = StackUtil.get(player, hand);
        ItemGoldenWrench.WrenchResult result = wrenchBlock(world, pos, side, player, true);
        if(result != ItemGoldenWrench.WrenchResult.Nothing) {
            if(!world.isRemote) {
                if(result != ItemGoldenWrench.WrenchResult.Rotated)
                    this.damage(stack, 10, player);
            }
            else {
                IC2.audioManager.playOnce(player, PositionSpec.Hand, "Tools/wrench.ogg", true, IC2.audioManager.getDefaultVolume());
            }
            return world.isRemote ? EnumActionResult.PASS : EnumActionResult.SUCCESS;
        }
        else {
            return EnumActionResult.FAIL;
        }
        
    }
    
    private static String getTeName(Object te) {
        return te != null ? te.getClass().getSimpleName().replace("TileEntity", "") : "none";
    }
    
    public void damage(ItemStack is, int damage, EntityPlayer player) {
        is.damageItem(damage, player);
    }
    
    public static ItemGoldenWrench.WrenchResult wrenchBlock(World world, BlockPos pos, EnumFacing side, EntityPlayer player, boolean remove) {
        IBlockState state = Util.getBlockState(world, pos);
        Block block = state.getBlock();
        if(!block.isAir(state, world, pos)) {
            if(block instanceof IWrenchable) {
                IWrenchable wrenchable = (IWrenchable)block;
                EnumFacing currentFacing = wrenchable.getFacing(world, pos);
                EnumFacing newFacing = currentFacing;
                int experience;
                if(!IC2.keyboard.isAltKeyDown(player)) {
                    if(player.isSneaking()) {
                        newFacing = side.getOpposite();
                    }
                    else {
                        newFacing = side;
                    }
                }
                else {
                    EnumFacing.Axis axis = side.getAxis();
                    if(side.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE && !player.isSneaking() || side.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE && player.isSneaking()) {
                        newFacing = currentFacing.rotateAround(axis);
                    }
                    else {
                        for(experience = 0; experience < 3; ++experience) {
                            newFacing = newFacing.rotateAround(axis);
                        }
                    }
                }
                
                if(newFacing != currentFacing && wrenchable.setFacing(world, pos, newFacing, player)) {
                    return WrenchResult.Rotated;
                }
                
                if(remove && wrenchable.wrenchCanRemove(world, pos, player)) {
                    if(!world.isRemote) {
                        TileEntity te = world.getTileEntity(pos);
                        if(ConfigUtil.getBool(MainConfig.get(), "protection/wrenchLogging")) {
                            String playerName = player.getGameProfile().getName() + "/" + player.getGameProfile().getId();
                            IC2.log.info(LogCategory.PlayerActivity, "Player %s used a wrench to remove the block %s (te %s) at %s.", playerName, state, getTeName(te), Util.formatPosition(world, pos));
                        }
                        
                        if(player instanceof EntityPlayerMP) {
                            experience = ForgeHooks.onBlockBreakEvent(world, ((EntityPlayerMP)player).interactionManager.getGameType(), (EntityPlayerMP)player, pos);
                            if(experience < 0) {
                                return WrenchResult.Nothing;
                            }
                        }
                        else {
                            experience = 0;
                        }
                        
                        block.onBlockHarvested(world, pos, state, player);
                        if(!block.removedByPlayer(state, world, pos, player, true)) {
                            return WrenchResult.Nothing;
                        }
                        
                        block.onBlockDestroyedByPlayer(world, pos, state);
                        List<ItemStack> drops = wrenchable.getWrenchDrops(world, pos, state, te, player, 0);
                        if(drops != null && !drops.isEmpty()) {
                            for(ItemStack stack : drops) {
                                StackUtil.dropAsEntity(world, pos, stack);
                            }
                        }
                        else if(logEmptyWrenchDrops) {
                            IC2.log.warn(LogCategory.General, "The block %s (te %s) at %s didn't yield any wrench drops.", state, getTeName(te), Util.formatPosition(world, pos));
                        }
                        
                        if(!player.capabilities.isCreativeMode && experience > 0) {
                            block.dropXpOnBlockBreak(world, pos, experience);
                        }
                    }
                    
                    return WrenchResult.Removed;
                }
            }
            else if(block.rotateBlock(world, pos, side)) {
                return WrenchResult.Rotated;
            }
            
        }
        return WrenchResult.Nothing;
    }
    
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return !repair.isEmpty() && OreDictionary.itemMatches(new ItemStack(Items.GOLD_INGOT), repair, false);
    }
    
    private enum WrenchResult {
        Rotated,
        Removed,
        Nothing;
        
        WrenchResult() {
        }
    }
}
