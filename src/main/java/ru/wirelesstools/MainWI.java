package ru.wirelesstools;

import ic2.api.event.TeBlockFinalCallEvent;
import ic2.core.block.BlockTileEntity;
import ic2.core.block.ITeBlock;
import ic2.core.block.TeBlockRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ru.wirelesstools.api.WirelessTransfer;
import ru.wirelesstools.config.ConfigWI;
import ru.wirelesstools.general.CTabWI;
import ru.wirelesstools.general.OverlayWI;
import ru.wirelesstools.general.RecipesWI;
import ru.wirelesstools.items.WI_Items;
import ru.wirelesstools.proxy.CommonPlatform;
import ru.wirelesstools.tileentities.CommonTEs;
import ru.wirelesstools.tileentities.EnumWITEs;
import ru.wirelesstools.tileentities.othertes.transferhandler.EnergyTransferCommon;
import ru.wirelesstools.tileentities.othertes.transferhandler.EnergyTransferIU;
import ru.wirelesstools.wirelesshandler.WirelessHandler;
import ru.wirelesstools.wnet.TesseractRegistry;

@EventBusSubscriber
@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME,
        dependencies = "required-after:ic2;after:gravisuite",
        version = Reference.MOD_VERSION,
        acceptedMinecraftVersions = "[1.12,1.12.2]")
public class MainWI {
    private static boolean isGSModLoaded;
    private static boolean isDEModLoaded;
    private static boolean isThExpModLoaded;
    
    public static final CTabWI tab = new CTabWI();
    
    public static BlockTileEntity commonTiles;
    public static BlockTileEntity wiTiles;
    
    public static final String category_WIRELESS = "wireless_settings";
    public static final String category_SOLAR_PANELS = "solars";
    public static final String category_ARMOR_AND_TOOLS = "armor_and_tools";
    public static final String category_RECEIVER = "Receiver";
    public static final String category_OTHER = "Other";
    
    public static final EnumRarity Rarity_Multi = EnumHelper.addRarity("Rar_Multi", TextFormatting.GOLD, "Multi Rarity");
    
    @SidedProxy(clientSide = "ru.wirelesstools.proxy.ClientPlatform", serverSide = "ru.wirelesstools.proxy.CommonPlatform")
    public static CommonPlatform platform;
    
    static <E extends Enum<E> & ITeBlock> void register(Class<E> enumClass, ResourceLocation ref) {
        TeBlockRegistry.addAll(enumClass, ref);
        TeBlockRegistry.setDefaultMaterial(ref, Material.IRON);
        TeBlockRegistry.addCreativeRegisterer((list, block, itemblock, tab) -> {
            if(tab == CreativeTabs.SEARCH || tab == MainWI.tab) {
                block.getAllTypes().forEach(type -> {
                    if(type.hasItem()) {
                        list.add(block.getItemStack(type));
                    }
                });
            }
        }, ref);
    }
    
    public static boolean isIsGSModLoaded() {
        return isGSModLoaded;
    }
    
    public static boolean isIsDEModLoaded() {
        return isDEModLoaded;
    }
    
    public static boolean isIsThExpModLoaded() {
        return isThExpModLoaded;
    }
    
    public static boolean areBothThExpAndDELoaded() {
        return isThExpModLoaded && isDEModLoaded;
    }
    
    @SubscribeEvent
    public static void register(TeBlockFinalCallEvent event) {
        isGSModLoaded = Loader.isModLoaded("gravisuite");
        isDEModLoaded = Loader.isModLoaded("draconicevolution");
        isThExpModLoaded = Loader.isModLoaded("thermalexpansion");
        
        register(CommonTEs.class, CommonTEs.IDENTITY);
        register(EnumWITEs.class, EnumWITEs.IDENTITY);
    }
    
    @EventHandler
    public void load(FMLPreInitializationEvent event) {
        WirelessTransfer.handler = new WirelessHandler();
        ConfigWI.loadConfig(event.getSuggestedConfigurationFile());
        commonTiles = TeBlockRegistry.get(CommonTEs.IDENTITY);
        wiTiles = TeBlockRegistry.get(EnumWITEs.IDENTITY);
        WI_Items.buildItems(event.getSide());
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
        platform.initRecipes();
        if(event.getSide().isClient()) {
            new OverlayWI();
        }
        if(Loader.isModLoaded("industrialupgrade"))
            WirelessTransfer.transfer = new EnergyTransferIU();
        else
            WirelessTransfer.transfer = new EnergyTransferCommon();
        
        CommonTEs.buildDummies();
        EnumWITEs.buildDummies();
    }
    
    @Mod.EventHandler
    public void onServerExit(FMLServerStoppedEvent event) {
        TesseractRegistry.getInstance().getTesseractTilesMap().clear();
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    
    }
    
}
