package ru.wirelesstools.gui;

import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.gui.EnergyGauge;
import ic2.core.gui.GuiElement;
import ic2.core.init.Localization;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.wirelesstools.Reference;
import ru.wirelesstools.container.ContainerEnergyDispatcher;
import ru.wirelesstools.gui.elements.CustomWIButtonBT;
import ru.wirelesstools.gui.elements.CustomWIButtonCommon;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiEnergyDispatcher extends GuiIC2<ContainerEnergyDispatcher> {
    
    public GuiEnergyDispatcher(ContainerEnergyDispatcher container) {
        super(container, 196);
        this.addElement(EnergyGauge.asBar(this, 75, 60, container.base));
        this.addElement(new CustomWIButtonCommon(this, 97, 34, 20, 20, 3).withTooltip("gui.dispatcher.tooltip.switch"));
        this.addElement(new CustomWIButtonCommon(this, 57, 34, 20, 20, 4).withTooltip("gui.dispatcher.tooltip.transmission"));
        this.addElement(new CustomWIButtonBT(this, 57, 16, 60, 15, mouseButton -> {
            switch(mouseButton) {
                case left:
                    IC2.network.get(false).initiateClientTileEntityEvent(container.base, 1);
                    break;
                case right:
                    IC2.network.get(false).initiateClientTileEntityEvent(container.base, 2);
                    break;
            }
        }).withText(() -> String.format("%.0f", this.container.base.getTransmitValue()) + " EU/t"));
    }
    
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mouseX -= this.guiLeft;
        mouseY -= this.guiTop;
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.bindTexture();
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        
        if(this.container.base.getIsOn())
            this.drawTexturedModalRect(this.guiLeft + 97, this.guiTop + 34, 199, 3, 20, 20);
        else
            this.drawTexturedModalRect(this.guiLeft + 97, this.guiTop + 34, 177, 3, 20, 20);
        
        for(GuiElement<?> element : this.elements) {
            if(element.isEnabled()) {
                element.drawBackground(mouseX, mouseY);
            }
        }
    }
    
    protected void drawForegroundLayer(int mouseX, int mouseY) {
        super.drawForegroundLayer(mouseX, mouseY);
        String name = Localization.translate(this.container.base.getName());
        this.fontRenderer.drawString(name, (this.xSize - this.fontRenderer.getStringWidth(name)) / 2, 6, 4210752);
        
        String evenlyDistribution = Localization.translate("gui.dispatcher.evenly.distribution");
        String commonDistribution = Localization.translate("gui.dispatcher.common.distribution");
        String sentSingleEnergyPacket = Localization.translate("gui.dispatcher.singleenergypacket") + ": "
                + String.format("%.2f", this.container.base.getSinglePacket()) + " EU/t";
        String tilesQuantity = Localization.translate("gui.dispatcher.tiles.quantity.chunk") + ": "
                + this.container.base.getEnergyTilesQuantity();
        
        this.fontRenderer.drawString(this.container.base.getIsEvenlyDistribution() ? evenlyDistribution : commonDistribution,
                this.container.base.getIsEvenlyDistribution() ? (this.xSize - this.fontRenderer.getStringWidth(evenlyDistribution)) / 2
                        : (this.xSize - this.fontRenderer.getStringWidth(commonDistribution)) / 2, 77, 4210752);
        this.fontRenderer.drawString(tilesQuantity,
                (this.xSize - this.fontRenderer.getStringWidth(tilesQuantity)) / 2, 87, 4210752);
        if(this.container.base.getIsEvenlyDistribution()) {
            this.fontRenderer.drawString(sentSingleEnergyPacket,
                    (this.xSize - this.fontRenderer.getStringWidth(sentSingleEnergyPacket)) / 2, 97, 4210752);
        }
        this.handleDispatcherTooltips(mouseX, mouseY);
    }
    
    private void handleDispatcherTooltips(int x, int y) {
        if(x >= 4 && x <= 13 && y >= 4 && y <= 13) {
            List<String> list = new ArrayList<>();
            list.add(Localization.translate("gui.dispatcher.info"));
            list.add(Localization.translate("gui.dispatcher.about.evenly1"));
            list.add(Localization.translate("gui.dispatcher.about.evenly2"));
            list.add(Localization.translate("gui.dispatcher.evenly.formula"));
            list.add(Localization.translate("gui.dispatcher.evenly.if.not.found"));
            this.drawTooltip(x, y, list);
        }
    }
    
    @Override
    protected ResourceLocation getTexture() {
        return new ResourceLocation(Reference.MOD_ID, "textures/gui/gui_energydispatcher.png");
    }
}
