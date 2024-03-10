package ru.wirelesstools.items;

import ic2.core.block.state.IIdProvider;
import ic2.core.init.BlocksItems;
import ic2.core.item.ItemMulti;
import ic2.core.ref.ItemName;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.wirelesstools.MainWI;
import ru.wirelesstools.Reference;

import java.util.Locale;

public class ItemsForCraft extends ItemMulti<ItemsForCraft.Craftings> {
    
    public ItemsForCraft() {
        super(null, Craftings.class);
        this.setCreativeTab(MainWI.tab);
        BlocksItems.registerItem(this, new ResourceLocation(Reference.MOD_ID, "crafting"));
    }
    
    public enum Craftings implements IIdProvider {
        
        wirelessmodule(0);
        
        private final String name;
        private final int ID;
        
        Craftings(int id) {
            this.ID = id;
            this.name = name().toLowerCase(Locale.ENGLISH);
        }
        
        @Override
        public int getId() {
            return this.ID;
        }
        
        @Override
        public String getName() {
            return this.name;
        }
        
        public static Craftings getFromID(int ID) {
            return VALUES[ID % VALUES.length];
        }
        
        private static final Craftings[] VALUES = values();
    }
    
    @SideOnly(Side.CLIENT)
    protected void registerModel(int meta, ItemName name, String extraName) {
        ModelLoader.setCustomModelResourceLocation(this, meta, new ModelResourceLocation(
                Reference.MOD_ID + ":crafting/" + Craftings.getFromID(meta).getName(), null));
    }
    
}
