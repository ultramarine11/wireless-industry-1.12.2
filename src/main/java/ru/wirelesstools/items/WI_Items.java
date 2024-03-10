package ru.wirelesstools.items;

import ic2.core.block.state.IIdProvider;
import ic2.core.ref.IItemModelProvider;
import ic2.core.ref.IMultiItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.wirelesstools.items.armor.MultiVisor;
import ru.wirelesstools.items.armor.QuantumBootsStatic;
import ru.wirelesstools.items.armor.QuantumMultiChestplate;
import ru.wirelesstools.items.tools.*;
import ru.wirelesstools.items.weapon.QuantumEnergyAbsorptionSword;

public enum WI_Items {
    luckyvajra,
    CRAFTING,
    playermodule,
    multichestplate,
    channel_switcher,
    multivisor,
    electric_descaler,
    absorbing_saber,
    portablecharger,
    golden_wrench,
    ender_quantum_boots;
    
    private Item instance;
    
    public <T extends Item & IItemModelProvider> T getInstance() {
        return (T)this.instance;
    }
    
    public ItemStack getItemStack() {
        return this.getItemStack((String)null);
    }
    
    public <T extends Enum<T>> ItemStack getItemStack(String variant) {
        if(this.instance == null) {
            return null;
        }
        if(this.instance instanceof IMultiItem) {
            IMultiItem multiItem = (IMultiItem)this.instance;
            return multiItem.getItemStack(variant);
        }
        if(variant == null) {
            return new ItemStack(this.instance);
        }
        throw new IllegalArgumentException("not applicable");
    }
    
    public <T extends Enum<T> & IIdProvider> ItemStack getItemStack(T variant) {
        if(this.instance == null)
            return null;
        
        if(this.instance instanceof IMultiItem) {
            
            IMultiItem<T> multiItem = (IMultiItem<T>)this.instance;
            
            return multiItem.getItemStack(variant);
        }
        if(variant == null) {
            return new ItemStack(this.instance);
        }
        throw new IllegalArgumentException("Not applicable");
        
    }
    
    public <T extends Item & IItemModelProvider> void setInstance(T instance) {
        if(this.instance != null)
            throw new IllegalStateException("Duplicate instances! " + this.instance.getUnlocalizedName() + " already exists");
        
        this.instance = instance;
    }
    
    public static void buildItems(Side side) {
        CRAFTING.setInstance(new ItemsForCraft());
        luckyvajra.setInstance(new LuckyVajra("luckyvajra"));
        playermodule.setInstance(new ItemPlayerModule("playermodule"));
        multichestplate.setInstance(new QuantumMultiChestplate("quantumchestmulti"));
        channel_switcher.setInstance(new ItemChannelSwitcher("channelswitcher"));
        multivisor.setInstance(new MultiVisor("multivisor"));
        electric_descaler.setInstance(new ElectricDescaler("electricdescaler"));
        absorbing_saber.setInstance(new QuantumEnergyAbsorptionSword("absorbingsword"));
        portablecharger.setInstance(new PortableElectricCharger("portablecharger"));
        golden_wrench.setInstance(new ItemGoldenWrench("goldenwrench"));
        ender_quantum_boots.setInstance(new QuantumBootsStatic("ender_quantum_static"));
        
        if(side == Side.CLIENT)
            doModelGuf();
    }
    
    @SideOnly(Side.CLIENT)
    private static void doModelGuf() {
        for(WI_Items item : values()) {
            item.getInstance().registerModels(null);
        }
        
    }
}
