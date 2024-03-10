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
import ru.wirelesstools.MainWI;
import ru.wirelesstools.Reference;
import ru.wirelesstools.tileentities.othertes.TileSolarFurnace;
import ru.wirelesstools.tileentities.othertes.TileVacuumPlayerChest;

import java.util.Set;

public enum CommonTEs implements ITeBlock {
    vacuum_player_chest(TileVacuumPlayerChest.class, 3, EnumRarity.UNCOMMON, Util.horizontalFacings, false),
    solar_furnace(TileSolarFurnace.class, 9, MainWI.Rarity_Multi, Util.horizontalFacings, true);
    
    private final Class<? extends TileEntityBlock> teClass;
    private final int itemMeta;
    private final EnumRarity rarity;
    private TileEntityBlock dummyTe;
    private static final CommonTEs[] VALUES;
    public static final ResourceLocation IDENTITY;
    private final Set<EnumFacing> supportedFacings;
    private final boolean hasActiveState;
    
    static {
        VALUES = values();
        IDENTITY = new ResourceLocation(Reference.MOD_ID, "commontes");
    }
    
    CommonTEs(Class<? extends TileEntityBlock> teClass, int itemMeta, EnumRarity rarity, Set<EnumFacing> supportedFacings, boolean hasActive) {
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
        for(CommonTEs block : VALUES) {
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
