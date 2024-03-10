package ru.wirelesstools.gui;

import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.gui.CustomButton;
import ic2.core.gui.EnergyGauge;
import ic2.core.gui.GuiElement;
import ic2.core.init.Localization;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import ru.wirelesstools.Reference;
import ru.wirelesstools.container.ContainerWirelessChargepad;
import ru.wirelesstools.gui.elements.AreaCustomWI;
import ru.wirelesstools.gui.elements.CustomWIButtonBT;

import java.util.ArrayList;
import java.util.List;

public class GuiWirelessChargepad extends GuiIC2<ContainerWirelessChargepad> {
    
    public GuiWirelessChargepad(ContainerWirelessChargepad container) {
        super(container, 196);
        this.addElement(EnergyGauge.asBar(this, 57, 23, container.base));
        
        this.addElement(new AreaCustomWI(this, 4, 4, 10, 10).withListTooltip(
                () -> {
                    List<String> list = new ArrayList<>();
                    list.add(Localization.translate("gui.chargepad.info1"));
                    list.add(Localization.translate("gui.chargepad.info2"));
                    list.add(Localization.translate("gui.chargepad.info3"));
                    return list;
                }
        ));
        
        this.addElement(new CustomWIButtonBT(this, 57, 71, 60, 15, mouseButton -> {
            switch(mouseButton) {
                case left:
                    IC2.network.get(false).initiateClientTileEntityEvent(container.base, 0);
                    break;
                case right:
                    IC2.network.get(false).initiateClientTileEntityEvent(container.base, 1);
                    break;
            }
        }).withText(() -> Localization.translate("gui.WChM.radius") + ": "
                + container.base.getRadius()));
        
        this.addElement(new CustomWIButtonBT(this, 57, 39, 60, 15, this.createEventSender(2))
                .withText(() -> {
                    switch(container.base.getMode()) {
                        case 0:
                            return Localization.translate("gui.WChPad.mode.all");
                        case 1:
                            return Localization.translate("gui.WChPad.mode.eu");
                        case 2:
                            return Localization.translate("gui.WChPad.mode.rf");
                        default:
                            return "Mode Error";
                    }
                }));
        
        this.addElement(new CustomButton(this, 106, 22, 11, 11, this.createEventSender(3))
                .withTooltip(Localization.translate("gui.dispatcher.tooltip.switch")));
    }
    
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mouseX -= this.guiLeft;
        mouseY -= this.guiTop;
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.bindTexture();
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        
        this.drawTexturedModalRect(this.guiLeft + 106, this.guiTop + 22,
                this.container.base.getIsOn() ? 177 : 191, 5, 11, 11);
        
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
        
        
        //this.showExtraTooltips(mouseX, mouseY);
    }
    
    @Override
    protected ResourceLocation getTexture() {
        return new ResourceLocation(Reference.MOD_ID, "textures/gui/gui_wchargepad.png");
    }
}
