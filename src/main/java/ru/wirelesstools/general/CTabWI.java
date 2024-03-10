package ru.wirelesstools.general;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import ru.wirelesstools.MainWI;
import ru.wirelesstools.tileentities.EnumWITEs;

public class CTabWI extends CreativeTabs {
    
    public CTabWI() {
        super("WirelessIndustry");
    }
    
    @Override
    public ItemStack getTabIconItem() {
        //if(MainWI.isIUModLoaded())
           // return MainWI.tilesIU.getItemStack(EnumIUTiles.tesseract);
        //else
            return MainWI.wiTiles.getItemStack(EnumWITEs.tesseract);
    }
}
