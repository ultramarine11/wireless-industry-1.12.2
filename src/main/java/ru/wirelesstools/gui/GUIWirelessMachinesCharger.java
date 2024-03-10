package ru.wirelesstools.gui;

import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.gui.CustomButton;
import ic2.core.gui.EnergyGauge;
import ic2.core.gui.GuiElement;
import ic2.core.init.Localization;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.wirelesstools.Reference;
import ru.wirelesstools.container.ContainerWirelessMachinesCharger;
import ru.wirelesstools.gui.elements.CustomWIButtonBT;

@SideOnly(Side.CLIENT)
public class GUIWirelessMachinesCharger extends GuiIC2<ContainerWirelessMachinesCharger> {
    
    public GUIWirelessMachinesCharger(ContainerWirelessMachinesCharger container) {
        super(container, 196);
        this.addElement(EnergyGauge.asBar(this, 75, 40, container.base));
        
        //horizontal +1
        this.addElement(new CustomButton(this, 14, 26, 31, 21, this.createEventSender(6)).withTooltip(
                () -> Localization.translate("gui.wchm.offset.xz") + " +1"
        ).withEnableHandler(container.base::getIsAsymmetricCharging));
        //horizontal -1
        this.addElement(new CustomButton(this, 14, 50, 31, 21, this.createEventSender(7)).withTooltip(
                () -> Localization.translate("gui.wchm.offset.xz") + " -1"
        ).withEnableHandler(container.base::getIsAsymmetricCharging));
        
        //vertical +1
        this.addElement(new CustomButton(this, 118, 40, 21, 31, this.createEventSender(8)).withTooltip(
                () -> Localization.translate("gui.wchm.offset.y") + " +1"
        ).withEnableHandler(container.base::getIsAsymmetricCharging));
        //vertical -1
        this.addElement(new CustomButton(this, 142, 40, 21, 31, this.createEventSender(9)).withTooltip(
                () -> Localization.translate("gui.wchm.offset.y") + " -1"
        ).withEnableHandler(container.base::getIsAsymmetricCharging));
        
        //symmetric
        this.addElement(new CustomButton(this, 74, 74, 11, 11, this.createEventSender(1))
                .withTooltip("+1").withEnableHandler(() -> !container.base.getIsAsymmetricCharging()));
        //symmetric
        this.addElement(new CustomButton(this, 89, 74, 11, 11, this.createEventSender(2))
                .withTooltip("-1").withEnableHandler(() -> !container.base.getIsAsymmetricCharging()));
        
        //button that changes mode symmetric <-> asymmetric
        this.addElement(new CustomButton(this, 76, 59, 22, 11, this.createEventSender(10))
                .withTooltip(Localization.translate("gui.wchm.tooltip.switch.chargingmode")));
        
        this.addElement(new CustomWIButtonBT(this, 57, 17, 60, 15, mouseButton -> {
            switch(mouseButton) {
                case left:
                    IC2.network.get(false).initiateClientTileEntityEvent(container.base, 3);
                    break;
                case right:
                    IC2.network.get(false).initiateClientTileEntityEvent(container.base, 4);
                    break;
            }
        }).withText(() -> container.base.getMode() == 8
                ? Localization.translate("gui.wchm.mode.automatic")
                : (int)container.base.getChargeRate() + " EU/t").withTooltip("gui.wchm.to.change.mode"));
        
        this.addElement(new CustomButton(this, 56, 39, 11, 11, this.createEventSender(5))
                .withTooltip(Localization.translate("gui.dispatcher.tooltip.switch")));
    }
    
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mouseX -= this.guiLeft;
        mouseY -= this.guiTop;
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.bindTexture();
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        
        this.drawTexturedModalRect(this.guiLeft + 56, this.guiTop + 39,
                this.container.base.getIsOn() ? 221 : 235, 8, 11, 11);
        
        if(this.container.base.getIsAsymmetricCharging()) {
            //horizontal +1
            this.drawTexturedModalRect(this.guiLeft + 14, this.guiTop + 26, 180, 54, 31, 21);
            //horizontal -1
            this.drawTexturedModalRect(this.guiLeft + 14, this.guiTop + 50, 180, 76, 31, 21);
            
            //vertical +1
            this.drawTexturedModalRect(this.guiLeft + 118, this.guiTop + 40, 214, 54, 21, 31);
            //vertical -1
            this.drawTexturedModalRect(this.guiLeft + 142, this.guiTop + 40, 214, 86, 21, 31);
        }
        else {
            this.drawTexturedModalRect(this.guiLeft + 74, this.guiTop + 74, 191, 41, 11, 11);
            this.drawTexturedModalRect(this.guiLeft + 89, this.guiTop + 74, 177, 41, 11, 11);
        }
        
        
        for(GuiElement<?> element : this.elements) {
            if(element.isEnabled()) {
                element.drawBackground(mouseX, mouseY);
            }
        }
    }
    
    protected void drawForegroundLayer(int mouseX, int mouseY) {
        super.drawForegroundLayer(mouseX, mouseY);
        String name = Localization.translate(this.container.base.getName());
        this.fontRenderer.drawString(name, (this.xSize - this.fontRenderer.getStringWidth(name)) / 2, 5, 4210752);
        
        String radiusstring = Localization.translate("gui.WChM.radius") + ": "
                + this.container.base.getRadius() + " " + Localization.translate("gui.blocks");
        String offsetHorizontal = Localization.translate("gui.wchm.offset.xz") + ": "
                + this.container.base.getOffsetXZ() + " " + Localization.translate("gui.blocks");
        String offsetVertical = Localization.translate("gui.wchm.offset.y") + ": "
                + this.container.base.getOffsetY() + " " + Localization.translate("gui.blocks");
        if(this.container.base.getIsAsymmetricCharging()) {
            this.fontRenderer.drawString(offsetHorizontal, (this.xSize - this.fontRenderer.getStringWidth(offsetHorizontal)) / 2, 77, 4210752);
            this.fontRenderer.drawString(offsetVertical, (this.xSize - this.fontRenderer.getStringWidth(offsetVertical)) / 2, 87, 4210752);
        }
        else
            this.fontRenderer.drawString(radiusstring, (this.xSize - this.fontRenderer.getStringWidth(radiusstring)) / 2, 87, 4210752);
        
        String tilesQuantity = Localization.translate("gui.dispatcher.tiles.quantity.radius") + ": "
                + this.container.base.getEnergyTilesQuantity();
        
        if(this.container.base.getIsOn())
            this.fontRenderer.drawString(tilesQuantity,
                    (this.xSize - this.fontRenderer.getStringWidth(tilesQuantity)) / 2, 97, 4210752);
    }
    
    @Override
    protected ResourceLocation getTexture() {
        return new ResourceLocation(Reference.MOD_ID, "textures/gui/gui_wmachinescharger.png");
    }
    
}
