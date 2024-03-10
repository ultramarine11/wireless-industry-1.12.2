package ru.wirelesstools.config;

import net.minecraftforge.common.config.Configuration;
import ru.wirelesstools.MainWI;
import ru.wirelesstools.items.armor.QuantumMultiChestplate;
import ru.wirelesstools.tileentities.SolarConfig;
import ru.wirelesstools.tileentities.WSBConfig;

import java.io.File;

public class ConfigWI {
    
    public static int maxChargeVajra;
    public static double maxChargeQChestPlate;
    public static int chestplateHealAmplifier;
    public static int chestplateSpeedAmplifier;
    public static int chestplateHasteAmplifier;
    public static int chestplateStrengthAmplifier;
    public static int chestplateJumpAmplifier;
    public static int chestplateResistanceAmplifier;
    public static int chestplateLuckAmplifier;
    public static int xPos;
    public static int yPos;
    public static boolean allowDisplaying;
    public static int tierPoint1;
    public static int tierPoint2;
    public static double maxCapacityPoint1;
    public static double maxCapacityPoint2;
    public static double maxLimitPoint1;
    public static double maxLimitPoint2;
    public static int maxRadiusRemoteItemCollector;
    public static int maxRadiusVacuumChest;
    public static boolean enableVajraChatMsgs;
    public static int xpTransmitterTier;
    public static int energyForXP;
    public static int fortuneLevel;
    public static int xpLimit;
    public static double maxStorageMachinesCharger;
    public static int machinesChargerTier;
    public static double maxStorageEnergyDispatcher;
    public static int energyDispatcherTier;
    public static int maxStorageRFChargerPlayer;
    public static double wirelessTransferTesseract;
    public static double chargingStaticBoots;
    public static double dayPowerSolarMachinesCharger;
    public static double nightPowerSolarMachinesCharger;
    public static double maxStorageEUChargerPlayer;
    public static int tierCharger;
    public static double dayPowerSolarFurnace;
    public static double nightPowerSolarFurnace;
    public static double dayPowerSolarXPTransmitter;
    public static double nightPowerSolarXPTransmitter;
    public static boolean enableExpCapitalization;
    public static int xpCapitalizationPercent;
    public static int xpCapitalizationTicks;
    
    public static SolarConfig settingsAdvanced;
    public static SolarConfig settingsHybrid;
    public static SolarConfig settingsUltimate;
    public static SolarConfig settingsQuantum;
    public static SolarConfig settingsProton;
    public static SolarConfig settingsSpectral;
    public static SolarConfig settingsSingular;
    public static SolarConfig settingsAbsorbing;
    public static SolarConfig settingsPhotonic;
    public static SolarConfig settingsNeutron;
    
    public static WSBConfig settingsWSB1;
    public static WSBConfig settingsWSB2;
    public static WSBConfig settingsWSB3;
    public static WSBConfig settingsWSB4;
    
    public static int energyEUStolenSword;
    
    public static void loadConfig(File config) {
        ConfigWI.loadNormalConfig(config);
    }
    
    private static void loadNormalConfig(File configFile) {
        Configuration config = new Configuration(configFile);
        try {
            config.load();
            settingsAdvanced = new SolarConfig(config.get(MainWI.category_SOLAR_PANELS, "AdvPanelGenDay", 16).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "AdvPanelGenNight", 8).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "AdvPanelStorage", 4096).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "AdvPanelTier", 1).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "AdvPanelWirelessTransmitLimit", 16).getInt());
    
            settingsHybrid = new SolarConfig(config.get(MainWI.category_SOLAR_PANELS, "HybridPanelGenDay", 64).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "HybridPanelGenNight", 32).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "HybridPanelStorage", 10000).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "HybridPanelTier", 2).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "HybridPanelWirelessTransmitLimit", 64).getInt());
            
            settingsUltimate = new SolarConfig(config.get(MainWI.category_SOLAR_PANELS, "UltimatePanelGenDay", 256).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "UltimatePanelGenNight", 128).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "UltimatePanelStorage", 100000).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "UltimatePanelTier", 3).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "UltimatePanelWirelessTransmitLimit", 256).getInt());
            
            settingsQuantum = new SolarConfig(config.get(MainWI.category_SOLAR_PANELS, "QuantumPanelGenDay", 1024).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "QuantumPanelGenNight", 512).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "QuantumPanelStorage", 200000).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "QuantumPanelTier", 4).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "QuantumPanelWirelessTransmitLimit", 1024).getInt());
            
            settingsProton = new SolarConfig(config.get(MainWI.category_SOLAR_PANELS, "ProtonPanelGenDay", 16384).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "ProtonPanelGenNight", 6144).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "ProtonPanelStorage", 5000000).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "ProtonPanelTier", 6).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "ProtonPanelWirelessTransmitLimit", 16384).getInt());
            
            settingsSpectral = new SolarConfig(config.get(MainWI.category_SOLAR_PANELS, "SpectralPanelGenDay", 4096).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "SpectralPanelGenNight", 1344).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "SpectralPanelStorage", 500000).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "SpecrtalPanelTier", 5).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "SpecrtalPanelWirelessTransmitLimit", 4096).getInt());
            
            settingsSingular = new SolarConfig(config.get(MainWI.category_SOLAR_PANELS, "SingularPanelGenDay", 65536).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "SingularPanelGenNight", 65536).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "SingularPanelStorage", 10000000).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "SingularPanelTier", 7).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "SingularPanelWirelessTransmitLimit", 65536).getInt());
            
            settingsAbsorbing = new SolarConfig(config.get(MainWI.category_SOLAR_PANELS, "AbsorbtionPanelGenDay", 262144).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "AbsorbtionPanelGenNight", 262144).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "AbsorbtionPanelStorage", 20000000).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "AbsorbtionPanelTier", 8).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "AbsorbtionPanelWirelessTransmitLimit", 262144).getInt());
            
            settingsPhotonic = new SolarConfig(config.get(MainWI.category_SOLAR_PANELS, "PhotonicPanelGenDay", 786432).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "PhotonicPanelGenNight", 786432).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "PhotonicPanelStorage", 80000000).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "PhotonicPanelTier", 9).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "PhotonicPanelWirelessTransmitLimit", 786432).getInt());
            
            settingsNeutron = new SolarConfig(config.get(MainWI.category_SOLAR_PANELS, "NeutronPanelGenDay", 3145728).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "NeutronPanelGenNight", 3145728).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "NeutronPanelStorage", 400000000).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "NeutronPanelTier", 10).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "NeutronPanelWirelessTransmitLimit", 3145728).getInt());
            
            /*TileWSpectralPanelPersonal.settings = new TileEntityWPBase.SolarConfig(
                    config.get(MainWI.category_SOLAR_PANELS, "SpectralPanelGenDay", 4096).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "SpectralPanelGenNight", 1344).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "SpectralPanelStorage", 500000).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "SpecrtalPanelTier", 5).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "SpecrtalPanelWirelessTransmitLimit", 4096).getInt());
            
            TileWSingularPanelPersonal.settings = new TileEntityWPBase.SolarConfig(
                    config.get(MainWI.category_SOLAR_PANELS, "SingularPanelGenDay", 65536).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "SingularPanelGenNight", 65536).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "SingularPanelStorage", 10000000).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "SingularPanelTier", 7).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "SingularPanelWirelessTransmitLimit", 65536).getInt());
            
            TileWAbsorbingPanelPersonal.settings = new TileEntityWPBase.SolarConfig(
                    config.get(MainWI.category_SOLAR_PANELS, "AbsorbtionPanelGenDay", 262144).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "AbsorbtionPanelGenNight", 262144).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "AbsorbtionPanelStorage", 20000000).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "AbsorbtionPanelTier", 8).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "AbsorbtionPanelWirelessTransmitLimit", 262144).getInt());
            
            TileWPhotonicPanelPersonal.settings = new TileEntityWPBase.SolarConfig(
                    config.get(MainWI.category_SOLAR_PANELS, "PhotonicPanelGenDay", 786432).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "PhotonicPanelGenNight", 786432).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "PhotonicPanelStorage", 80000000).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "PhotonicPanelTier", 9).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "PhotonicPanelWirelessTransmitLimit", 786432).getInt());
            
            TileWNeutronPanelPersonal.settings = new TileEntityWPBase.SolarConfig(
                    config.get(MainWI.category_SOLAR_PANELS, "NeutronPanelGenDay", 3145728).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "NeutronPanelGenNight", 3145728).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "NeutronPanelStorage", 400000000).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "NeutronPanelTier", 10).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "NeutronPanelWirelessTransmitLimit", 3145728).getInt());
            
            TileWAdvancedPanelPersonal.settings = new TileEntityWPBase.SolarConfig(
                    config.get(MainWI.category_SOLAR_PANELS, "AdvPanelGenDay", 16).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "AdvPanelGenNight", 8).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "AdvPanelStorage", 4096).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "AdvPanelTier", 1).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "AdvPanelWirelessTransmitLimit", 16).getInt());
            
            TileWHybridPanelPersonal.settings = new TileEntityWPBase.SolarConfig(
                    config.get(MainWI.category_SOLAR_PANELS, "HybridPanelGenDay", 64).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "HybridPanelGenNight", 32).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "HybridPanelStorage", 10000).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "HybridPanelTier", 2).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "HybridPanelWirelessTransmitLimit", 64).getInt());
            
            TileWUltimatePanelPersonal.settings = new TileEntityWPBase.SolarConfig(
                    config.get(MainWI.category_SOLAR_PANELS, "UltimatePanelGenDay", 256).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "UltimatePanelGenNight", 128).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "UltimatePanelStorage", 100000).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "UltimatePanelTier", 3).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "UltimatePanelWirelessTransmitLimit", 256).getInt());
            
            TileWQuantumPanelPersonal.settings = new TileEntityWPBase.SolarConfig(
                    config.get(MainWI.category_SOLAR_PANELS, "QuantumPanelGenDay", 1024).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "QuantumPanelGenNight", 512).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "QuantumPanelStorage", 200000).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "QuantumPanelTier", 4).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "QuantumPanelWirelessTransmitLimit", 1024).getInt());
            
            TileWProtonPanelPersonal.settings = new TileEntityWPBase.SolarConfig(
                    config.get(MainWI.category_SOLAR_PANELS, "ProtonPanelGenDay", 16384).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "ProtonPanelGenNight", 6144).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "ProtonPanelStorage", 5000000).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "ProtonPanelTier", 6).getInt(),
                    config.get(MainWI.category_SOLAR_PANELS, "ProtonPanelWirelessTransmitLimit", 16384).getInt());*/
            
            QuantumMultiChestplate.selfChargeRate = config.getInt("RechargeSpeed", MainWI.category_ARMOR_AND_TOOLS, 4, 1, Integer.MAX_VALUE,
                    "Self-recharge rate of Quantum Multi Chestplate (Eu/t)");
            
            QuantumMultiChestplate.radius = config.getInt("Radius", MainWI.category_ARMOR_AND_TOOLS, 10, 1, Integer.MAX_VALUE,
                    "Radius of wireless charging and healing of Quantum Multi Chestplate (blocks)");
            
            settingsWSB1 = new WSBConfig(config.get(MainWI.category_RECEIVER, "MaximumStorage1", 50000000.0, "Maximum storage of Wireless Receiver").getDouble(),
                    config.get(MainWI.category_RECEIVER, "Tier1", 4, "Tier of Wireless Receiver").getInt());
            
            settingsWSB2 = new WSBConfig(config.get(MainWI.category_RECEIVER, "MaximumStorage2", 500000000.0, "Maximum storage of Advanced Wireless Receiver").getDouble(),
                    config.get(MainWI.category_RECEIVER, "Tier2", 7, "Tier of Advanced Wireless Receiver").getInt());
            
            settingsWSB3 = new WSBConfig(config.get(MainWI.category_RECEIVER, "MaximumStorage3", 1000000000.0, "Maximum storage of Ultimate Wireless Receiver").getDouble(),
                    config.get(MainWI.category_RECEIVER, "Tier3", 10, "Tier of Ultimate Wireless Receiver").getInt());
            
            settingsWSB4 = new WSBConfig(config.get(MainWI.category_RECEIVER, "MaximumStorage4", 2000000000.0, "Maximum storage of Quantum Wireless Reciever").getDouble(),
                    config.get(MainWI.category_RECEIVER, "Tier4", 11, "Tier of Quantum Wireless Reciever").getInt());
            
            maxStorageEUChargerPlayer = config
                    .get(MainWI.category_WIRELESS, "MaxStorageEU", 500000000.0, "Wireless player charger max EU storage").getDouble();
            
            tierCharger = config.get(MainWI.category_WIRELESS, "Tier", 9, "Wireless player charger tier")
                    .getInt();
            
            maxChargeVajra = config.get(MainWI.category_ARMOR_AND_TOOLS, "MaxChargeVajra", 60000000, "Maximum charge of Lucky Vajra")
                    .getInt();
            
            maxChargeQChestPlate = config.get(MainWI.category_ARMOR_AND_TOOLS, "MaxChargeChestplate", 5000000000.0, "Maximum charge of Quantum Multi Chestplate")
                    .getDouble();
            
            chestplateHealAmplifier = Math.max(config.get(MainWI.category_ARMOR_AND_TOOLS, "Heal_amplifier", 2, "Healing effect amplifier of Quantum Multi Chestplate")
                    .getInt(), 1) - 1;
            
            chestplateSpeedAmplifier = Math.max(config.get(MainWI.category_ARMOR_AND_TOOLS, "Speed_amplifier", 1, "Speed effect amplifier of Quantum Multi Chestplate")
                    .getInt(), 1) - 1;
            
            chestplateHasteAmplifier = Math.max(config.get(MainWI.category_ARMOR_AND_TOOLS, "Haste_amplifier", 2, "Haste effect amplifier of Quantum Multi Chestplate")
                    .getInt(), 1) - 1;
            
            chestplateStrengthAmplifier = Math.max(config.get(MainWI.category_ARMOR_AND_TOOLS, "Strength_amplifier", 1, "Strength effect amplifier of Quantum Multi Chestplate")
                    .getInt(), 1) - 1;
            
            chestplateJumpAmplifier = Math.max(config.get(MainWI.category_ARMOR_AND_TOOLS, "Jump_amplifier", 1, "Jump effect amplifier of Quantum Multi Chestplate")
                    .getInt(), 1) - 1;
            
            chestplateResistanceAmplifier = Math.max(config.get(MainWI.category_ARMOR_AND_TOOLS, "Resistance_amplifier", 1, "Resistance effect amplifier of Quantum Multi Chestplate")
                    .getInt(), 1) - 1;
            
            chestplateLuckAmplifier = Math.max(config.get(MainWI.category_ARMOR_AND_TOOLS, "Luck_amplifier", 1, "Luck effect amplifier of Quantum Multi Chestplate")
                    .getInt(), 1) - 1;
            
            maxLimitPoint1 = config.get(MainWI.category_WIRELESS, "WirelessLimit_1", 16384.0, "Limit of wirelessly sending energy for EU Point 1")
                    .getDouble();
            
            maxLimitPoint2 = config.get(MainWI.category_WIRELESS, "WirelessLimit_2", 131072.0, "Limit of wirelessly sending energy for EU Point 2")
                    .getDouble();
            
            tierPoint1 = config.get(MainWI.category_WIRELESS, "Tier_1", 4, "Tier of EU Point")
                    .getInt();
            
            tierPoint2 = config.get(MainWI.category_WIRELESS, "Tier_2", 8, "Tier of Advanced EU Point")
                    .getInt();
            
            xPos = Math.max(config.get(MainWI.category_ARMOR_AND_TOOLS, "X_pos", 1, "X position of Vajra charge percentage inscription on the screen")
                    .getInt(), 0);
            
            yPos = Math.max(config.get(MainWI.category_ARMOR_AND_TOOLS, "Y_pos", 1, "Y position of Vajra charge percentage inscription on the screen")
                    .getInt(), 0);
            
            allowDisplaying = config.get(MainWI.category_ARMOR_AND_TOOLS,
                    "Display", true, "Enables displaying the charge percentage of Lucky Vajra on the screen").getBoolean();
            
            maxCapacityPoint1 = config.get(MainWI.category_WIRELESS, "Capacity_1", 40000000.0, "Maximum energy capacity of EU Point")
                    .getDouble();
            
            maxCapacityPoint2 = config.get(MainWI.category_WIRELESS, "Capacity_2", 160000000.0, "Maximum energy capacity of Advanced EU Point")
                    .getDouble();
            
            maxRadiusRemoteItemCollector = config.get(MainWI.category_OTHER, "Collector_max_radius", 10, "Maximum radius of Remote Item Collector")
                    .getInt();
            
            maxRadiusVacuumChest = config.get(MainWI.category_OTHER, "Vacuum_max_radius", 10, "Maximum radius of Vacuum Player Chest")
                    .getInt();
            
            enableVajraChatMsgs = config.get(MainWI.category_ARMOR_AND_TOOLS,
                    "VajraMsgs", true, "Enables chat messages of Lucky Vajra").getBoolean();
            
            energyForXP = config.getInt("EU_for_XP", MainWI.category_OTHER, 1000000, 1, Integer.MAX_VALUE, "Required EU energy for 1 XP generation");
            
            fortuneLevel = config.getInt("Fortune_Level", MainWI.category_ARMOR_AND_TOOLS, 6, 1, Integer.MAX_VALUE, "The level of Fortune enchantment for Lucky Vajra");
            
            xpTransmitterTier = config.getInt("XP_Tier", MainWI.category_OTHER, 9, 1, Integer.MAX_VALUE, "The tier of Experience Transmitter");
            
            xpLimit = config.getInt("Maximum_XP", MainWI.category_OTHER, 20000000, 1, Integer.MAX_VALUE, "Limit of XP stored in Experience Transmitter");
            
            maxStorageMachinesCharger = config.get(MainWI.category_WIRELESS, "MaxCharge_MCh", 1000000000.0, "Maximum Storage of Wireless Machines Charger").getDouble();
            
            machinesChargerTier = config.getInt("MCh_Tier", MainWI.category_WIRELESS, 10, 1, Integer.MAX_VALUE, "The tier of Wireless Machines Charger");
            
            maxStorageEnergyDispatcher = config.get(MainWI.category_WIRELESS, "MaxStorage_EnDisp", 1.0E12, "Maximum Storage of Energy Dispatcher").getDouble();
            
            energyDispatcherTier = config.getInt("EnDisp_Tier", MainWI.category_WIRELESS, 10, 1, Integer.MAX_VALUE, "The tier of Energy Dispatcher");
            
            maxStorageRFChargerPlayer = config.getInt("RF_Max", MainWI.category_WIRELESS, 2000000000, 1, Integer.MAX_VALUE, "Wireless player charger max RF storage");
            
            wirelessTransferTesseract = config.get(MainWI.category_WIRELESS, "WirelessTransfer_T", 8192.0, "Default amount of wirelessly sending energy for Tesseract").getDouble();
            
            chargingStaticBoots = config.get(MainWI.category_ARMOR_AND_TOOLS, "Static_charge", 16.0, "Amount of electrostatic charging of ender boots").getDouble();
            
            dayPowerSolarMachinesCharger = config.get(MainWI.category_SOLAR_PANELS, "DayPower", 2048.0, "Day energy generation of Solar Machines Charger").getDouble();
            nightPowerSolarMachinesCharger = config.get(MainWI.category_SOLAR_PANELS, "NightPower", 512.0, "Night energy generation of Solar Machines Charger").getDouble();
            
            dayPowerSolarFurnace = config.get(MainWI.category_SOLAR_PANELS, "SF_Gen_Day", 3.0, "Day energy generation of Solar Furnace").getDouble();
            nightPowerSolarFurnace = config.get(MainWI.category_SOLAR_PANELS, "SF_Gen_Night", 1.0, "Night energy generation of Solar Furnace").getDouble();
            
            dayPowerSolarXPTransmitter = config.get(MainWI.category_SOLAR_PANELS, "XPT_Gen_Day", 512.0, "Day XP generation of Solar XP Transmitter").getDouble();
            nightPowerSolarXPTransmitter = config.get(MainWI.category_SOLAR_PANELS, "XPT_Gen_Night", 128.0, "Night XP generation of Solar XP Transmitter").getDouble();
            
            enableExpCapitalization = config.getBoolean("XpCapital", MainWI.category_ARMOR_AND_TOOLS, true, "Enable experience capitalization feature for Quantum Multi Chestplate");
            xpCapitalizationPercent = config.getInt("Percent", MainWI.category_ARMOR_AND_TOOLS, 1, 1, 4, "XP capitalization percentage value");
            xpCapitalizationTicks = config.getInt("CapitalTicks", MainWI.category_ARMOR_AND_TOOLS, 1200, 1, Integer.MAX_VALUE, "Number of ticks after which xp capitalization occurs");
    
            energyEUStolenSword = config.getInt("EU_Stolen", MainWI.category_OTHER, 100000, 1, Integer.MAX_VALUE, "EU stolen from armor part when hit by Quantum Energy Absorption Sword");
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            if(config.hasChanged())
                config.save();
        }
    }
}
