package ru.wirelesstools.tileentities;

import ic2.core.block.ITeBlock;
import ic2.core.block.TileEntityBlock;
import ic2.core.ref.TeBlock.DefaultDrop;
import ic2.core.ref.TeBlock.HarvestTool;
import ic2.core.util.Util;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.GameRegistry;
import ru.wirelesstools.Reference;
import ru.wirelesstools.tileentities.othertes.*;
import ru.wirelesstools.tileentities.wireless.TileEnergyAutoDispatcher;
import ru.wirelesstools.tileentities.wireless.TilePoint1;
import ru.wirelesstools.tileentities.wireless.TilePoint2;
import ru.wirelesstools.tileentities.wireless.TileTesseract;
import ru.wirelesstools.tileentities.wireless.panels.*;
import ru.wirelesstools.tileentities.wireless.receivers.TileWSPersonal;
import ru.wirelesstools.tileentities.wireless.receivers.TileWSPersonal2;
import ru.wirelesstools.tileentities.wireless.receivers.TileWSPersonal3;
import ru.wirelesstools.tileentities.wireless.receivers.TileWSPersonal4;
import ru.wirelesstools.tileentities.xp.SolarXPTransmitter;
import ru.wirelesstools.tileentities.xp.TileExperienceTransmitter;

import java.util.EnumSet;
import java.util.Set;

public enum EnumWITEs implements ITeBlock {
    wireless_machine_charger(TileEntityMachinesCharger.class, 0, EnumRarity.COMMON, Util.horizontalFacings, false),
    remote_item_collector(TileRemoteItemCollector.class, 1, EnumRarity.COMMON, Util.horizontalFacings, false),
    wireless_charger_player(TileWirelessChargerPlayer.class, 2, EnumRarity.UNCOMMON, Util.horizontalFacings, false),
    eu_point_1(TilePoint1.class, 4, EnumRarity.COMMON, Util.allFacings, true),
    eu_point_2(TilePoint2.class, 5, EnumRarity.COMMON, Util.allFacings, true),
    energy_dispatcher(TileEnergyAutoDispatcher.class, 6, EnumRarity.COMMON, Util.allFacings, false),
    tesseract(TileTesseract.class, 7, EnumRarity.EPIC, Util.allFacings, false),
    solar_wireless_charger_player(TileEntitySolarMachinesCharger.class, 8, EnumRarity.RARE, Util.horizontalFacings, false),
    
    wireless_storage_personal(TileWSPersonal.class, 9, EnumRarity.COMMON, Util.allFacings, false),
    wireless_storage_personal2(TileWSPersonal2.class, 10, EnumRarity.UNCOMMON, Util.allFacings, false),
    wireless_storage_personal3(TileWSPersonal3.class, 11, EnumRarity.RARE, Util.allFacings, false),
    wireless_storage_personal4(TileWSPersonal4.class, 12, EnumRarity.EPIC, Util.allFacings, false),
    
    advanced_solar_panel_personal(TileWAdvancedPanelPersonal.class, 13, EnumRarity.COMMON, Util.horizontalFacings, false),
    hybrid_solar_panel_personal(TileWHybridPanelPersonal.class, 14, EnumRarity.COMMON, Util.horizontalFacings, false),
    ultimate_solar_panel_personal(TileWUltimatePanelPersonal.class, 15, EnumRarity.COMMON, Util.horizontalFacings, false),
    quantum_solar_panel_personal(TileWQuantumPanelPersonal.class, 16, EnumRarity.COMMON, Util.horizontalFacings, false),
    spectral_solar_panel_personal(TileWSpectralPanelPersonal.class, 17, EnumRarity.RARE, Util.horizontalFacings, false),
    proton_solar_panel_personal(TileWProtonPanelPersonal.class, 18, EnumRarity.RARE, Util.horizontalFacings, false),
    singular_solar_panel_personal(TileWSingularPanelPersonal.class, 19, EnumRarity.RARE, Util.horizontalFacings, false),
    absorbing_solar_panel_personal(TileWAbsorbingPanelPersonal.class, 20, EnumRarity.RARE, Util.horizontalFacings, false),
    photonic_solar_panel_personal(TileWPhotonicPanelPersonal.class, 21, EnumRarity.RARE, Util.horizontalFacings, false),
    neutron_solar_panel_personal(TileWNeutronPanelPersonal.class, 22, EnumRarity.EPIC, Util.horizontalFacings, false),
    
    xpgenerator(TileExperienceTransmitter.class, 23, EnumRarity.UNCOMMON, Util.horizontalFacings, true),
    solarxpgen(SolarXPTransmitter.class, 24, EnumRarity.RARE, Util.horizontalFacings, true),
    wirelesschargepad(TileWirelessChargepad.class, 25, EnumRarity.RARE, EnumSet.of(EnumFacing.EAST, EnumFacing.SOUTH), true);
    
    private final Class<? extends TileEntityBlock> teClass;
    private final int itemMeta;
    private final EnumRarity rarity;
    private TileEntityBlock dummyTe;
    private static final EnumWITEs[] VALUES;
    public static final ResourceLocation IDENTITY;
    private final Set<EnumFacing> supportedFacings;
    private final boolean hasActiveState;
    
    static {
        VALUES = values();
        IDENTITY = new ResourceLocation(Reference.MOD_ID, "witiles");
    }
    
    EnumWITEs(Class<? extends TileEntityBlock> teClass, int itemMeta, EnumRarity rarity, Set<EnumFacing> supportedFacings, boolean hasActive) {
        this.teClass = teClass;
        this.itemMeta = itemMeta;
        this.rarity = rarity;
        this.supportedFacings = supportedFacings;
        this.hasActiveState = hasActive;
        GameRegistry.registerTileEntity(teClass, Reference.MOD_ID + ":" + getName());
    }
    
    @Override
    public int getId() {
        return this.itemMeta;
    }
    
    @Override
    public String getName() {
        return name();
    }
    
    @Override
    public boolean allowWrenchRotating() {
        return false;
    }
    
    @Override
    public DefaultDrop getDefaultDrop() {
        return DefaultDrop.Self;
    }
    
    @Override
    public TileEntityBlock getDummyTe() {
        return this.dummyTe;
    }
    
    @Override
    public float getExplosionResistance() {
        return 15.0F;
    }
    
    @Override
    public float getHardness() {
        return 3.0F;
    }
    
    @Override
    public HarvestTool getHarvestTool() {
        return HarvestTool.Pickaxe;
    }
    
    @Override
    public ResourceLocation getIdentifier() {
        return IDENTITY;
    }
    
    @Override
    public EnumRarity getRarity() {
        return this.rarity;
    }
    
    @Override
    public Set<EnumFacing> getSupportedFacings() {
        return this.supportedFacings;
    }
    
    @Override
    public Class<? extends TileEntityBlock> getTeClass() {
        return this.teClass;
    }
    
    @Override
    public boolean hasActive() {
        return this.hasActiveState;
    }
    
    @Override
    public boolean hasItem() {
        return true;
    }
    
    public static void buildDummies() {
        ModContainer mc = Loader.instance().activeModContainer();
        if(mc == null || !"wirelesstools".equals(mc.getModId())) {
            throw new IllegalAccessError("Don't mess with this please.");
        }
        for(EnumWITEs block : VALUES) {
            if(block.teClass != null) {
                try {
                    block.dummyTe = block.teClass.newInstance();
                }
                catch(Exception e) {
                    if(Util.inDev()) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
}
