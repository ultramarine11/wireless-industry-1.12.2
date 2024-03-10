package ru.wirelesstools.general;

import cofh.thermalexpansion.block.device.BlockDevice;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.block.storage.BlockCell;
import cofh.thermalexpansion.block.storage.BlockTank;
import cofh.thermalfoundation.item.ItemMaterial;
import cofh.thermalfoundation.item.ItemUpgrade;
import com.brandon3055.draconicevolution.DEFeatures;
import ic2.api.item.IC2Items;
import ic2.core.util.StackUtil;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import ru.wirelesstools.MainWI;
import ru.wirelesstools.items.ItemsForCraft;
import ru.wirelesstools.items.WI_Items;
import ru.wirelesstools.tileentities.EnumWITEs;

public class IntegrationDEThExp {
    
    public static void addBothThExpDeIntegration() {
        RecipesWI.addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.wirelesschargepad),
                new Object[] {
                        "EEE",
                        "CDC",
                        "ABA",
                        Character.valueOf('A'), ItemMaterial.gearEnderium,
                        Character.valueOf('B'), IC2Items.getItem("te", "chargepad_mfsu"),
                        Character.valueOf('C'), new ItemStack(DEFeatures.energyCrystal, 1, 1),
                        Character.valueOf('D'), DEFeatures.energyInfuser,
                        Character.valueOf('E'), ItemMaterial.plateLumium});
        
        RecipesWI.addShapedRecipe(StackUtil.copyWithWildCard(new ItemStack(WI_Items.ender_quantum_boots.getInstance())),
                new Object[] {
                        "DAD",
                        "BCB",
                        "DAD",
                        Character.valueOf('A'), ItemUpgrade.upgradeFull[3],
                        Character.valueOf('B'), IC2Items.getItem("upgrade", "overclocker"),
                        Character.valueOf('C'), StackUtil.copyWithWildCard(IC2Items.getItem("quantum_boots")),
                        Character.valueOf('D'), OreDictionary.getOres("plateEnderium")});
    
        RecipesWI.addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.xpgenerator), new Object[] {
                "ADA",
                "ACA",
                "ABA",
                Character.valueOf('A'), IC2Items.getItem("fluid_cell"),
                Character.valueOf('B'), IC2Items.getItem("te", "mfsu"),
                Character.valueOf('C'), IC2Items.getItem("te", "matter_generator"),
                Character.valueOf('D'), BlockDevice.deviceExpCollector});
    
        RecipesWI.addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.wireless_machine_charger),
                new Object[] {
                        " A ",
                        "ABA",
                        "DCD",
                        Character.valueOf('A'), WI_Items.CRAFTING.getItemStack(ItemsForCraft.Craftings.wirelessmodule),
                        Character.valueOf('B'), IC2Items.getItem("te", "mfsu"),
                        Character.valueOf('C'), BlockMachine.machineCharger,
                        Character.valueOf('D'), IC2Items.getItem("crafting", "iridium")});
    
        RecipesWI.addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.remote_item_collector),
                new Object[] {
                        "CEC",
                        "ABA",
                        "ADA",
                        Character.valueOf('A'), Items.ENDER_EYE,
                        Character.valueOf('B'), IC2Items.getItem("resource", "advanced_machine"),
                        Character.valueOf('C'), Blocks.CHEST,
                        Character.valueOf('D'), BlockDevice.deviceItemCollector,
                        Character.valueOf('E'), ItemUpgrade.upgradeIncremental[0]});
        
        RecipesWI.addShapedRecipe(StackUtil.copyWithWildCard(new ItemStack(WI_Items.absorbing_saber.getInstance())),
                new Object[] {
                        "DAD",
                        "BCB",
                        "DAD",
                        Character.valueOf('A'), ItemUpgrade.upgradeFull[3],
                        Character.valueOf('B'), IC2Items.getItem("upgrade", "overclocker"),
                        Character.valueOf('C'), DEFeatures.wyvernSword,
                        Character.valueOf('D'), OreDictionary.getOres("plateEnderium")});
    
        RecipesWI.addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.energy_dispatcher),
                new Object[] {
                        "   ",
                        "ABA",
                        "   ",
                        Character.valueOf('A'), MainWI.wiTiles.getItemStack(EnumWITEs.wireless_machine_charger),
                        Character.valueOf('B'), DEFeatures.energyInfuser});
    
        RecipesWI.addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.wireless_charger_player),
                new Object[] {
                        "CAC",
                        "DBD",
                        "CEC",
                        Character.valueOf('A'), Items.ENDER_EYE,
                        Character.valueOf('B'), IC2Items.getItem("te", "mfsu"),
                        Character.valueOf('C'), IC2Items.getItem("charging_lapotron_crystal"),
                        Character.valueOf('D'), DEFeatures.energyInfuser,
                        Character.valueOf('E'), ItemUpgrade.upgradeFull[1]});
    
        RecipesWI.addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.tesseract),
                new Object[] {
                        "CDC",
                        "ABA",
                        "CDC",
                        Character.valueOf('A'), DEFeatures.energyInfuser,
                        Character.valueOf('B'), new ItemStack(DEFeatures.energyCrystal, 1, 2),
                        Character.valueOf('C'), IC2Items.getItem("te", "mfsu"),
                        Character.valueOf('D'), BlockTank.tank[0]});
    
        RecipesWI.addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.advanced_solar_panel_personal),
                new Object[] {
                        "ABA",
                        "DFD",
                        "CEC",
                        Character.valueOf('A'), IC2Items.getItem("glass", "reinforced"),
                        Character.valueOf('B'), IC2Items.getItem("misc_resource", "iridium_ore"),
                        Character.valueOf('C'), IC2Items.getItem("te", "solar_generator"),
                        Character.valueOf('D'), DEFeatures.draconicCore,
                        Character.valueOf('E'), BlockCell.cell[1],
                        Character.valueOf('F'), WI_Items.CRAFTING.getItemStack(ItemsForCraft.Craftings.wirelessmodule)});
    }
    
}
