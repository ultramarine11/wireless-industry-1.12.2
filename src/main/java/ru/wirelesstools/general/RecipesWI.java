package ru.wirelesstools.general;

import cofh.thermalexpansion.block.device.BlockDevice;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.block.storage.BlockCell;
import cofh.thermalexpansion.block.storage.BlockTank;
import cofh.thermalfoundation.item.ItemMaterial;
import cofh.thermalfoundation.item.ItemUpgrade;
import com.brandon3055.draconicevolution.DEFeatures;
import com.chocohead.gravisuite.GS_Items;
import com.chocohead.gravisuite.items.ItemCraftingThings;
import ic2.api.item.IC2Items;
import ic2.core.util.StackUtil;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.oredict.OreDictionary;
import ru.wirelesstools.MainWI;
import ru.wirelesstools.items.ItemsForCraft;
import ru.wirelesstools.items.WI_Items;
import ru.wirelesstools.tileentities.CommonTEs;
import ru.wirelesstools.tileentities.EnumWITEs;

public class RecipesWI {
    
    public RecipesWI() {
        this.addCraftingRecipesAll();
    }
    
    protected void addCraftingRecipesAll() {
        
        if(MainWI.areBothThExpAndDELoaded())
            IntegrationDEThExp.addBothThExpDeIntegration();
        else if(MainWI.isIsThExpModLoaded())
            this.addThExpIntegration();
        else if(MainWI.isIsDEModLoaded())
            this.addDEIntegration();
        else
            this.addNormalRecipes1();
        
        if(MainWI.isIsGSModLoaded())
            this.initGSIntegration();
        else
            this.addNormalRecipes2();
    
        addShapedRecipe(StackUtil.copyWithWildCard(new ItemStack(WI_Items.electric_descaler.getInstance())),
                new Object[] {"CDC", "BAB", "CDC",
                        Character.valueOf('A'), StackUtil.copyWithWildCard(IC2Items.getItem("electric_wrench")),
                        Character.valueOf('B'), Blocks.WOOL,
                        Character.valueOf('C'), IC2Items.getItem("casing", "gold"),
                        Character.valueOf('D'), IC2Items.getItem("crafting", "advanced_circuit")});
    
        addShapedRecipe(new ItemStack(WI_Items.channel_switcher.getInstance()),
                new Object[] {"   ", " A ", " B ",
                        Character.valueOf('A'), StackUtil.copyWithWildCard(IC2Items.getItem("electric_wrench")),
                        Character.valueOf('B'), WI_Items.CRAFTING.getItemStack(ItemsForCraft.Craftings.wirelessmodule)});
    
        addShapedRecipe(StackUtil.copyWithWildCard(new ItemStack(WI_Items.multivisor.getInstance())),
                new Object[] {" C ", "BAB", " C ",
                        Character.valueOf('A'), StackUtil.copyWithWildCard(IC2Items.getItem("nightvision_goggles")),
                        Character.valueOf('B'), IC2Items.getItem("upgrade", "overclocker"),
                        Character.valueOf('C'), IC2Items.getItem("tri_heat_storage")});
    
        addShapedRecipe(MainWI.commonTiles.getItemStack(CommonTEs.vacuum_player_chest),
                "CCC",
                        "ABA",
                        "DDD",
                Character.valueOf('A'), Blocks.IRON_BLOCK,
                Character.valueOf('B'), IC2Items.getItem("resource", "advanced_machine"),
                Character.valueOf('C'), Blocks.CHEST,
                Character.valueOf('D'), IC2Items.getItem("crafting", "circuit"));
    
        addShapedRecipe(MainWI.commonTiles.getItemStack(CommonTEs.solar_furnace),
                new Object[] {"CAC", "EBE", "CDC",
                        Character.valueOf('A'), IC2Items.getItem("te", "solar_generator"),
                        Character.valueOf('B'), IC2Items.getItem("re_battery"),
                        Character.valueOf('C'), IC2Items.getItem("plate", "iron"),
                        Character.valueOf('D'), Blocks.FURNACE,
                        Character.valueOf('E'), Items.REDSTONE});
    
        addShapedRecipe(new ItemStack(WI_Items.golden_wrench.getInstance()),
                new Object[] {"A A", "ABA", " B ",
                        Character.valueOf('A'), new ItemStack(Items.GOLD_INGOT), Character.valueOf('B'), new ItemStack(Items.IRON_INGOT)});
    
        addShapeLessRecipe(IC2Items.getItem("electric_wrench"), WI_Items.golden_wrench.getInstance(),
                IC2Items.getItem("crafting", "small_power_unit"));
    
        addShapedRecipe(WI_Items.CRAFTING.getItemStack(ItemsForCraft.Craftings.wirelessmodule),
                new Object[] {
                        "ABA",
                        "DCD",
                        "ABA",
                        Character.valueOf('A'), IC2Items.getItem("upgrade", "overclocker"),
                        Character.valueOf('B'), Blocks.REDSTONE_BLOCK,
                        Character.valueOf('C'), Items.ENDER_PEARL,
                        Character.valueOf('D'), IC2Items.getItem("crafting", "advanced_circuit")});
    
        addShapedRecipe(StackUtil.copyWithWildCard(new ItemStack(WI_Items.portablecharger.getInstance())),
                new Object[] {
                        " B ",
                        "ABA",
                        " B ",
                        Character.valueOf('A'), IC2Items.getItem("te", "mfsu"),
                        Character.valueOf('B'), WI_Items.CRAFTING.getItemStack(ItemsForCraft.Craftings.wirelessmodule)});
        
        for(int i = 0; i < 5; i++) {
            addShapedRecipe(new ItemStack(WI_Items.playermodule.getInstance()),
                    new Object[] {"BBB", "BAB", "BCB",
                            Character.valueOf('A'), new ItemStack(Items.SKULL, 1, i),
                            Character.valueOf('B'), IC2Items.getItem("plate", "dense_iron"),
                            Character.valueOf('C'), IC2Items.getItem("crafting", "circuit")});
        }
        
        addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.solarxpgen), new Object[] {
                " A ",
                " B ",
                "   ",
                Character.valueOf('A'), MainWI.wiTiles.getItemStack(EnumWITEs.ultimate_solar_panel_personal),
                Character.valueOf('B'), MainWI.wiTiles.getItemStack(EnumWITEs.xpgenerator)});
        
        addShapeLessRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.solar_wireless_charger_player),
                MainWI.wiTiles.getItemStack(EnumWITEs.wireless_machine_charger),
                MainWI.wiTiles.getItemStack(EnumWITEs.quantum_solar_panel_personal));
        
        addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.wireless_storage_personal),
                new Object[] {" A ", "ABA", " A ",
                        Character.valueOf('A'),
                        WI_Items.CRAFTING.getItemStack(ItemsForCraft.Craftings.wirelessmodule),
                        Character.valueOf('B'), IC2Items.getItem("te", "mfsu")});
        
        addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.wireless_storage_personal2),
                new Object[] {" A ", "AAA", " A ",
                        Character.valueOf('A'), MainWI.wiTiles.getItemStack(EnumWITEs.wireless_storage_personal)});
        
        addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.wireless_storage_personal3),
                new Object[] {" A ", "AAA", " A ",
                        Character.valueOf('A'), MainWI.wiTiles.getItemStack(EnumWITEs.wireless_storage_personal2)});
        
        addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.wireless_storage_personal4),
                new Object[] {" A ", "AAA", " A ",
                        Character.valueOf('A'), MainWI.wiTiles.getItemStack(EnumWITEs.wireless_storage_personal3)});
        
        addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.proton_solar_panel_personal),
                new Object[] {" A ", "A A", " A ",
                        Character.valueOf('A'),
                        MainWI.wiTiles.getItemStack(EnumWITEs.spectral_solar_panel_personal)});
    
        addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.spectral_solar_panel_personal),
                new Object[] {" A ", "A A", " A ",
                        Character.valueOf('A'),
                        MainWI.wiTiles.getItemStack(EnumWITEs.quantum_solar_panel_personal)});
    
        addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.quantum_solar_panel_personal),
                new Object[] {" A ", "A A", " A ",
                        Character.valueOf('A'),
                        MainWI.wiTiles.getItemStack(EnumWITEs.ultimate_solar_panel_personal)});
    
        addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.ultimate_solar_panel_personal),
                new Object[] {" A ", "A A", " A ",
                        Character.valueOf('A'),
                        MainWI.wiTiles.getItemStack(EnumWITEs.hybrid_solar_panel_personal)});
    
        addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.hybrid_solar_panel_personal),
                new Object[] {" A ", "A A", " A ",
                        Character.valueOf('A'),
                        MainWI.wiTiles.getItemStack(EnumWITEs.advanced_solar_panel_personal)});
    
        addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.singular_solar_panel_personal),
                new Object[] {" A ", "A A", " A ",
                        Character.valueOf('A'),
                        MainWI.wiTiles.getItemStack(EnumWITEs.proton_solar_panel_personal)});
    
        addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.absorbing_solar_panel_personal),
                new Object[] {" A ", "A A", " A ",
                        Character.valueOf('A'),
                        MainWI.wiTiles.getItemStack(EnumWITEs.singular_solar_panel_personal)});
    
        addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.photonic_solar_panel_personal),
                new Object[] {" A ", " A ", " A ",
                        Character.valueOf('A'),
                        MainWI.wiTiles.getItemStack(EnumWITEs.absorbing_solar_panel_personal)});
    
        addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.neutron_solar_panel_personal),
                new Object[] {" A ", "A A", " A ",
                        Character.valueOf('A'),
                        MainWI.wiTiles.getItemStack(EnumWITEs.photonic_solar_panel_personal)});
    }
    
    @Optional.Method(modid = "thermalexpansion")
    private void addDefaultIntegrationRecipes() {
        addShapedRecipe(StackUtil.copyWithWildCard(new ItemStack(WI_Items.ender_quantum_boots.getInstance())),
                new Object[] {
                        "DAD",
                        "BCB",
                        "DAD",
                        Character.valueOf('A'), ItemUpgrade.upgradeFull[3],
                        Character.valueOf('B'), IC2Items.getItem("upgrade", "overclocker"),
                        Character.valueOf('C'), StackUtil.copyWithWildCard(IC2Items.getItem("quantum_boots")),
                        Character.valueOf('D'), OreDictionary.getOres("plateEnderium")});
        
        addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.xpgenerator), new Object[] {
                "ADA",
                "ACA",
                "ABA",
                Character.valueOf('A'), IC2Items.getItem("fluid_cell"),
                Character.valueOf('B'), IC2Items.getItem("te", "mfsu"),
                Character.valueOf('C'), IC2Items.getItem("te", "matter_generator"),
                Character.valueOf('D'), BlockDevice.deviceExpCollector});
        
        addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.wireless_machine_charger),
                new Object[] {
                        " A ",
                        "ABA",
                        "DCD",
                        Character.valueOf('A'), WI_Items.CRAFTING.getItemStack(ItemsForCraft.Craftings.wirelessmodule),
                        Character.valueOf('B'), IC2Items.getItem("te", "mfsu"),
                        Character.valueOf('C'), BlockMachine.machineCharger,
                        Character.valueOf('D'), IC2Items.getItem("crafting", "iridium")});
    
        addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.remote_item_collector),
                new Object[] {
                        "CEC",
                        "ABA",
                        "ADA",
                        Character.valueOf('A'), Items.ENDER_EYE,
                        Character.valueOf('B'), IC2Items.getItem("resource", "advanced_machine"),
                        Character.valueOf('C'), Blocks.CHEST,
                        Character.valueOf('D'), BlockDevice.deviceItemCollector,
                        Character.valueOf('E'), ItemUpgrade.upgradeIncremental[0]});
    }
    
    /*private void addBothThExpDeIntegration() {
        this.addDefaultIntegrationRecipes();
        
        addShapedRecipe(StackUtil.copyWithWildCard(new ItemStack(WI_Items.absorbing_saber.getInstance())),
                new Object[] {
                        "DAD",
                        "BCB",
                        "DAD",
                        Character.valueOf('A'), ItemUpgrade.upgradeFull[3],
                        Character.valueOf('B'), IC2Items.getItem("upgrade", "overclocker"),
                        Character.valueOf('C'), DEFeatures.wyvernSword,
                        Character.valueOf('D'), OreDictionary.getOres("plateEnderium")});
        
        addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.energy_dispatcher),
                new Object[] {
                        "   ",
                        "ABA",
                        "   ",
                        Character.valueOf('A'), MainWI.wiTiles.getItemStack(EnumWITEs.wireless_machine_charger),
                        Character.valueOf('B'), DEFeatures.energyInfuser});
        
        addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.wireless_charger_player),
                new Object[] {
                        "CAC",
                        "DBD",
                        "CEC",
                        Character.valueOf('A'), Items.ENDER_EYE,
                        Character.valueOf('B'), IC2Items.getItem("te", "mfsu"),
                        Character.valueOf('C'), IC2Items.getItem("charging_lapotron_crystal"),
                        Character.valueOf('D'), DEFeatures.energyInfuser,
                        Character.valueOf('E'), ItemUpgrade.upgradeFull[1]});
    
        addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.tesseract),
                new Object[] {
                        "CDC",
                        "ABA",
                        "CDC",
                        Character.valueOf('A'), DEFeatures.energyInfuser,
                        Character.valueOf('B'), new ItemStack(DEFeatures.energyCrystal, 1, 2),
                        Character.valueOf('C'), IC2Items.getItem("te", "mfsu"),
                        Character.valueOf('D'), BlockTank.tank[0]});
    
        addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.advanced_solar_panel_personal),
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
    }*/
    
    @Optional.Method(modid = "thermalexpansion")
    private void addThExpIntegration() {
        this.addDefaultIntegrationRecipes();
        
        RecipesWI.addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.wirelesschargepad),
                new Object[] {
                        "EEE",
                        "CDC",
                        "ABA",
                        Character.valueOf('A'), ItemMaterial.gearEnderium,
                        Character.valueOf('B'), IC2Items.getItem("te", "chargepad_mfsu"),
                        Character.valueOf('C'), ItemMaterial.gearPlatinum,
                        Character.valueOf('D'), BlockMachine.machineCharger,
                        Character.valueOf('E'), ItemMaterial.plateLumium});
    
        addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.energy_dispatcher),
                new Object[] {
                        "   ",
                        "ABA",
                        " C ",
                        Character.valueOf('A'), MainWI.wiTiles.getItemStack(EnumWITEs.wireless_machine_charger),
                        Character.valueOf('B'), IC2Items.getItem("te", "mfsu"),
                        Character.valueOf('C'), BlockMachine.machineCharger});
        
        addShapedRecipe(StackUtil.copyWithWildCard(new ItemStack(WI_Items.absorbing_saber.getInstance())),
                new Object[] {
                        "DAD",
                        "BCB",
                        "DAD",
                        Character.valueOf('A'), ItemUpgrade.upgradeFull[3],
                        Character.valueOf('B'), IC2Items.getItem("upgrade", "overclocker"),
                        Character.valueOf('C'), StackUtil.copyWithWildCard(IC2Items.getItem("nano_saber")),
                        Character.valueOf('D'), OreDictionary.getOres("plateEnderium")});
        
        addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.wireless_charger_player),
                new Object[] {
                        "CAC",
                        "DBD",
                        "CEC",
                        Character.valueOf('A'), Items.ENDER_EYE,
                        Character.valueOf('B'), IC2Items.getItem("te", "mfsu"),
                        Character.valueOf('C'), IC2Items.getItem("charging_lapotron_crystal"),
                        Character.valueOf('D'), BlockMachine.machineCharger,
                        Character.valueOf('E'), ItemUpgrade.upgradeFull[1]});
    
        addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.tesseract),
                new Object[] {
                        "CDC",
                        "ABA",
                        "CDC",
                        Character.valueOf('A'), ItemUpgrade.upgradeFull[1],
                        Character.valueOf('B'), BlockMachine.machineCharger,
                        Character.valueOf('C'), IC2Items.getItem("te", "mfsu"),
                        Character.valueOf('D'), BlockTank.tank[1]});
    
        addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.advanced_solar_panel_personal),
                new Object[] {
                        "ABA",
                        "DFD",
                        "CEC",
                        Character.valueOf('A'), IC2Items.getItem("glass", "reinforced"),
                        Character.valueOf('B'), IC2Items.getItem("misc_resource", "iridium_ore"),
                        Character.valueOf('C'), IC2Items.getItem("te", "solar_generator"),
                        Character.valueOf('D'), BlockMachine.machineFurnace,
                        Character.valueOf('E'), BlockCell.cell[1],
                        Character.valueOf('F'), WI_Items.CRAFTING.getItemStack(ItemsForCraft.Craftings.wirelessmodule)});
    }
    
    @Optional.Method(modid = "draconicevolution")
    private void addDEIntegration() {
        RecipesWI.addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.wirelesschargepad),
                new Object[] {
                        "   ",
                        "CDC",
                        "ABA",
                        Character.valueOf('A'), DEFeatures.wyvernCore,
                        Character.valueOf('B'), IC2Items.getItem("te", "chargepad_mfsu"),
                        Character.valueOf('C'), new ItemStack(DEFeatures.energyCrystal, 1, 2),
                        Character.valueOf('D'), DEFeatures.energyInfuser});
        
        addShapedRecipe(StackUtil.copyWithWildCard(new ItemStack(WI_Items.absorbing_saber.getInstance())),
                new Object[] {
                        "DAD",
                        "BCB",
                        "DAD",
                        Character.valueOf('A'), DEFeatures.wyvernCore,
                        Character.valueOf('B'), IC2Items.getItem("upgrade", "overclocker"),
                        Character.valueOf('C'), DEFeatures.wyvernSword,
                        Character.valueOf('D'), DEFeatures.draconicIngot});
        
        addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.energy_dispatcher),
                new Object[] {
                        "   ",
                        "ABA",
                        "   ",
                        Character.valueOf('A'), MainWI.wiTiles.getItemStack(EnumWITEs.wireless_machine_charger),
                        Character.valueOf('B'), DEFeatures.energyInfuser});
    
        addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.tesseract),
                new Object[] {
                        "CDC",
                        "ABA",
                        "CDC",
                        Character.valueOf('A'), DEFeatures.energyInfuser,
                        Character.valueOf('B'), new ItemStack(DEFeatures.energyCrystal, 1, 2),
                        Character.valueOf('C'), IC2Items.getItem("te", "mfsu"),
                        Character.valueOf('D'), DEFeatures.fusionCraftingCore});
    
        addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.advanced_solar_panel_personal),
                new Object[] {
                        "ABA",
                        "DFD",
                        "CEC",
                        Character.valueOf('A'), IC2Items.getItem("glass", "reinforced"),
                        Character.valueOf('B'), IC2Items.getItem("misc_resource", "iridium_ore"),
                        Character.valueOf('C'), IC2Items.getItem("te", "solar_generator"),
                        Character.valueOf('D'), DEFeatures.draconicCore,
                        Character.valueOf('E'), DEFeatures.generator,
                        Character.valueOf('F'), WI_Items.CRAFTING.getItemStack(ItemsForCraft.Craftings.wirelessmodule)});
    }
    
    private void addNormalRecipes1() {
        addShapedRecipe(StackUtil.copyWithWildCard(new ItemStack(WI_Items.ender_quantum_boots.getInstance())),
                new Object[] {
                        "DAD",
                        "BCB",
                        "DAD",
                        Character.valueOf('A'), IC2Items.getItem("crafting", "iridium"),
                        Character.valueOf('B'), IC2Items.getItem("upgrade", "overclocker"),
                        Character.valueOf('C'), StackUtil.copyWithWildCard(IC2Items.getItem("quantum_boots")),
                        Character.valueOf('D'), IC2Items.getItem("crafting", "advanced_circuit")});
        
        addShapedRecipe(StackUtil.copyWithWildCard(new ItemStack(WI_Items.absorbing_saber.getInstance())),
                new Object[] {
                        "DAD",
                        "BCB",
                        "DAD",
                        Character.valueOf('A'), StackUtil.copyWithWildCard(IC2Items.getItem("charging_lapotron_crystal")),
                        Character.valueOf('B'), IC2Items.getItem("upgrade", "overclocker"),
                        Character.valueOf('C'), StackUtil.copyWithWildCard(IC2Items.getItem("nano_saber")),
                        Character.valueOf('D'), IC2Items.getItem("crafting", "iridium")});
        
        addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.xpgenerator), new Object[] {
                "ADA",
                "ACA",
                "ABA",
                Character.valueOf('A'), IC2Items.getItem("fluid_cell"),
                Character.valueOf('B'), IC2Items.getItem("te", "mfe"),
                Character.valueOf('C'), IC2Items.getItem("te", "matter_generator"),
                Character.valueOf('D'), Blocks.LAPIS_BLOCK});
        
        addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.wireless_machine_charger),
                new Object[] {
                        " A ",
                        "ABA",
                        "CCC",
                        Character.valueOf('A'), WI_Items.CRAFTING.getItemStack(ItemsForCraft.Craftings.wirelessmodule),
                        Character.valueOf('B'), IC2Items.getItem("te", "mfsu"),
                        Character.valueOf('C'), IC2Items.getItem("crafting", "iridium")});
        
        addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.remote_item_collector),
                new Object[] {
                        "CAC",
                        "ABA",
                        "CAC",
                        Character.valueOf('A'), Items.ENDER_EYE,
                        Character.valueOf('B'), IC2Items.getItem("resource", "advanced_machine"),
                        Character.valueOf('C'), Blocks.CHEST});
        
        addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.wireless_charger_player),
                new Object[] {
                        "CAC",
                        "DBD",
                        "CAC",
                        Character.valueOf('A'), Items.ENDER_EYE,
                        Character.valueOf('B'), IC2Items.getItem("te", "mfsu"),
                        Character.valueOf('C'), IC2Items.getItem("charging_lapotron_crystal"),
                        Character.valueOf('D'), IC2Items.getItem("te", "ev_transformer")});
    
        addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.energy_dispatcher),
                new Object[] {
                        "B B",
                        "ABA",
                        "B B",
                        Character.valueOf('A'), MainWI.wiTiles.getItemStack(EnumWITEs.wireless_machine_charger),
                        Character.valueOf('B'), IC2Items.getItem("te", "mfsu")});
    
        addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.tesseract),
                new Object[] {
                        "CDC",
                        "ABA",
                        "CDC",
                        Character.valueOf('A'), MainWI.wiTiles.getItemStack(EnumWITEs.eu_point_2),
                        Character.valueOf('B'), Items.ENDER_EYE,
                        Character.valueOf('C'), IC2Items.getItem("te", "mfsu"),
                        Character.valueOf('D'), IC2Items.getItem("te", "ev_transformer")});
    
        addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.advanced_solar_panel_personal),
                new Object[] {"AAA", "ABA", "AAA",
                        Character.valueOf('A'),
                        WI_Items.CRAFTING.getItemStack(ItemsForCraft.Craftings.wirelessmodule),
                        Character.valueOf('B'), IC2Items.getItem("te", "solar_generator")});
    }
    
    @Optional.Method(modid = "gravisuite")
    private void initGSIntegration() {
        addShapedRecipe(StackUtil.copyWithWildCard(new ItemStack(WI_Items.luckyvajra.getInstance())),
                new Object[] {"DCD", "BAB", "DCD",
                        Character.valueOf('A'), StackUtil.copyWithWildCard(new ItemStack(GS_Items.VAJRA.getInstance())),
                        Character.valueOf('B'), IC2Items.getItem("upgrade", "overclocker"),
                        Character.valueOf('C'), IC2Items.getItem("plate", "dense_obsidian"),
                        Character.valueOf('D'), StackUtil.copyWithWildCard(IC2Items.getItem("mining_laser"))});
        
        addShapedRecipe(StackUtil.copyWithWildCard(new ItemStack(WI_Items.multichestplate.getInstance())),
                new Object[] {"DCD",
                        "BAB",
                        "DCD",
                        Character.valueOf('A'), StackUtil.copyWithWildCard(new ItemStack(GS_Items.GRAVI_CHESTPLATE.getInstance())),
                        Character.valueOf('B'), GS_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.VAJRA_CORE),
                        Character.valueOf('C'), Blocks.DIAMOND_BLOCK,
                        Character.valueOf('D'), GS_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.SUPERCONDUCTOR)});
        
        addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.eu_point_1),
                "ACA", "DBD", "ACA",
                Character.valueOf('A'), GS_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.SUPERCONDUCTOR),
                Character.valueOf('B'), MainWI.wiTiles.getItemStack(EnumWITEs.wireless_storage_personal),
                Character.valueOf('C'), IC2Items.getItem("te", "teleporter"),
                Character.valueOf('D'), Items.ENDER_PEARL);
        
        addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.eu_point_2),
                "ACA", "CBC", "ACA",
                Character.valueOf('A'), MainWI.wiTiles.getItemStack(EnumWITEs.eu_point_1),
                Character.valueOf('B'), IC2Items.getItem("fluid_cell", "ic2uu_matter"),
                Character.valueOf('C'), GS_Items.CRAFTING.getItemStack(ItemCraftingThings.CraftingTypes.COOLING_CORE));
    }
    
    private void addNormalRecipes2() {
        addShapedRecipe(StackUtil.copyWithWildCard(new ItemStack(WI_Items.luckyvajra.getInstance())),
                new Object[] {"DCD", "BAB", "DCD",
                        Character.valueOf('A'), StackUtil.copyWithWildCard(IC2Items.getItem("iridium_drill")),
                        Character.valueOf('B'), IC2Items.getItem("upgrade", "overclocker"),
                        Character.valueOf('C'), IC2Items.getItem("plate", "dense_obsidian"),
                        Character.valueOf('D'), StackUtil.copyWithWildCard(IC2Items.getItem("mining_laser"))});
        
        addShapedRecipe(StackUtil.copyWithWildCard(new ItemStack(WI_Items.multichestplate.getInstance())),
                new Object[] {"DCD",
                        "BAB",
                        "DCD",
                        Character.valueOf('A'), StackUtil.copyWithWildCard(IC2Items.getItem("quantum_chestplate")),
                        Character.valueOf('B'), IC2Items.getItem("te", "hv_transformer"),
                        Character.valueOf('C'), Blocks.DIAMOND_BLOCK,
                        Character.valueOf('D'), IC2Items.getItem("cable", "type:glass,insulation:0")});
        
        addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.eu_point_1),
                "ACA", "DBD", "ACA",
                Character.valueOf('A'), IC2Items.getItem("cable", "type:glass,insulation:0"),
                Character.valueOf('B'), MainWI.wiTiles.getItemStack(EnumWITEs.wireless_storage_personal),
                Character.valueOf('C'), IC2Items.getItem("te", "teleporter"),
                Character.valueOf('D'), Items.ENDER_PEARL);
        
        addShapedRecipe(MainWI.wiTiles.getItemStack(EnumWITEs.eu_point_2),
                "ACA", "CBC", "ACA",
                Character.valueOf('A'), MainWI.wiTiles.getItemStack(EnumWITEs.eu_point_1),
                Character.valueOf('B'), IC2Items.getItem("fluid_cell", "ic2uu_matter"),
                Character.valueOf('C'), IC2Items.getItem("lapotron_crystal"));
        
    }
    
    
    public static void addShapedRecipe(ItemStack output, Object... inputs) {
        ic2.api.recipe.Recipes.advRecipes.addRecipe(output, inputs);
    }
    
    public static void addShapeLessRecipe(ItemStack output, Object... inputs) {
        ic2.api.recipe.Recipes.advRecipes.addShapelessRecipe(output, inputs);
    }
    
}
