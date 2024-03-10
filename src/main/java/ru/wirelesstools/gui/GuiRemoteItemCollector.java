package ru.wirelesstools.gui;

import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.gui.EnergyGauge;
import ic2.core.init.Localization;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.wirelesstools.Reference;
import ru.wirelesstools.container.ContainerRemoteCollector;
import ru.wirelesstools.gui.elements.CustomWIButtonBT;

@SideOnly(Side.CLIENT)
public class GuiRemoteItemCollector extends GuiIC2<ContainerRemoteCollector> {
    
    public GuiRemoteItemCollector(ContainerRemoteCollector container) {
        super(container, 196);
        this.addElement(EnergyGauge.asBolt(this, 10, 90, container.base));
        this.addElement(new CustomWIButtonBT(this, ((this.xSize - 94) / 2), 78, 94, 15, mouseButton -> {
            switch(mouseButton) {
                case left:
                    IC2.network.get(false).initiateClientTileEntityEvent(container.base, 0);
                    break;
                case right:
                    IC2.network.get(false).initiateClientTileEntityEvent(container.base, 1);
                    break;
            }
        }).withText(() -> Localization.translate("gui.RemC.radius") + ": "
                + container.base.getRadius()));
        
        this.addElement(new CustomWIButtonBT(this, ((this.xSize - 48) / 2), 95, 48, 15,
                mouseButton -> IC2.network.get(false).initiateClientTileEntityEvent(container.base, 2))
                .withText(() -> this.container.base.getIsWorking()
                        ? Localization.translate("gui.RemC.turned.on")
                        : Localization.translate("gui.RemC.turned.off"))
                .withTooltip(Localization.translate("gui.wind.switch")));
    }
    
    @Override
    protected ResourceLocation getTexture() {
        return new ResourceLocation(Reference.MOD_ID, "textures/gui/gui_itemcollector.png");
    }
    
}
