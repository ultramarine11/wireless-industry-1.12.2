package ru.wirelesstools.general;

import com.chocohead.gravisuite.GS_Items;
import com.chocohead.gravisuite.items.ItemCraftingThings;
import ic2.api.item.IC2Items;
import ic2.core.util.StackUtil;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import ru.wirelesstools.items.WI_Items;

public class GSIntegration {
    
    public static void initGSRecipes() {
        RecipesWI.addShapedRecipe(StackUtil.copyWithWildCard(new ItemStack(WI_Items.luckyvajra.getInstance())),
                new Object[] {"DCD", "BAB", "DCD",
                        Character.valueOf('A'), StackUtil.copyWithWildCard(new ItemStack(GS_Items.VAJRA.getInstance())),
                        Character.valueOf('B'), IC2Items.getItem("upgrade", "overclocker"),
                        Character.valueOf('C'), IC2Items.getItem("plate", "dense_obsidian"),
                        Character.valueOf('D'), StackUtil.copyWithWildCard(IC2Items.getItem("mining_laser"))});
    
        RecipesWI.addShapedRecipe(StackUtil.copyWithWildCard(new ItemStack(WI_Items.multichestplate.getInstance())),
                new Object[] {"DCD",
                        "BAB",
                        "DCD",
                        Character.valueOf('A'), StackUtil.copyWithWildCard(new ItemStack(GS_Items.GRAVI_CHESTPLATE.getInstance())),
                        Character.valueOf('B'), GS_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.VAJRA_CORE),
                        Character.valueOf('C'), Blocks.DIAMOND_BLOCK,
                        Character.valueOf('D'), GS_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.SUPERCONDUCTOR)});
    
        /*RecipesWI.addShapedRecipe(MainWI.wiTiles.getItemStack(EnumOtherTEs.eu_point_1),
                "ACA", "DBD", "ACA",
                Character.valueOf('A'), GS_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.SUPERCONDUCTOR),
                Character.valueOf('B'), MainWI.wiTiles.getItemStack(TEWSB.wireless_storage_personal),
                Character.valueOf('C'), IC2Items.getItem("te", "teleporter"),
                Character.valueOf('D'), Items.ENDER_PEARL);
    
        RecipesWI.addShapedRecipe(MainWI.wiTiles.getItemStack(EnumOtherTEs.eu_point_2),
                "ACA", "CBC", "ACA",
                Character.valueOf('A'), MainWI.wiTiles.getItemStack(EnumOtherTEs.eu_point_1),
                Character.valueOf('B'), IC2Items.getItem("fluid_cell", "ic2uu_matter"),
                Character.valueOf('C'), GS_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.COOLING_CORE));*/
    }
    
}
