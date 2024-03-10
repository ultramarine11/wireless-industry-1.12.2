package ru.wirelesstools.gui;

import com.google.common.collect.Lists;
import ic2.core.GuiIC2;
import ic2.core.gui.Area;
import ic2.core.gui.CustomGauge;
import ic2.core.gui.Gauge;
import ic2.core.gui.GuiElement;
import ic2.core.init.Localization;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.wirelesstools.Reference;
import ru.wirelesstools.config.ConfigWI;
import ru.wirelesstools.container.ContainerXPTransmitter;
import ru.wirelesstools.gui.elements.CustomWIButtonCommon;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@SideOnly(Side.CLIENT)
public class GuiXPTransmitter extends GuiIC2<ContainerXPTransmitter> {
    
    public GuiXPTransmitter(ContainerXPTransmitter container) {
        super(container);
        this.addElement(new CustomWIButtonCommon(this, 9, 42, 11, 11, 1).withTooltip("+1"));
        this.addElement(new CustomWIButtonCommon(this, 9, 61, 11, 11, 2).withTooltip("-1"));
        this.addElement(new CustomWIButtonCommon(this, 22, 42, 11, 11, 5).withTooltip("+10"));
        this.addElement(new CustomWIButtonCommon(this, 22, 61, 11, 11, 6).withTooltip("-10"));
        this.addElement(new CustomWIButtonCommon(this, 35, 42, 11, 11, 7).withTooltip("+100"));
        this.addElement(new CustomWIButtonCommon(this, 35, 61, 11, 11, 8).withTooltip("-100"));
        this.addElement(new CustomWIButtonCommon(this, 95, 52, 20, 20, 3).withTooltip("gui.xp.tooltip.on_off"));
        this.addElement(new CustomWIButtonCommon(this, 130, 52, 20, 20, 4).withTooltip("gui.xp.tooltip.toggle.transmission"));
        this.addElement(new CustomGauge(this, 65, 21,
                () -> (double)container.base.getStoredXP() / container.base.getXpLimit(),
                new Gauge.GaugePropertyBuilder(181, 59, 48, 5,
                        Gauge.GaugePropertyBuilder.GaugeOrientation.Right)
                        .withTexture(new ResourceLocation(Reference.MOD_ID, "textures/gui/guiexptransmitter.png"))
                        .withBackground(-4, -4, 56, 13, 177, 44)
                        .build())
                .withTooltip(() -> container.base.getStoredXP() + " / " + container.base.getXpLimit() + " XP"));
        
        if(container.base.isSolar())
            this.addElement(new Area(this, 141, 20, 14, 14).withTooltip(() -> {
                switch(container.base.getGenState()) {
                    case DAY:
                        return Localization.translate("gui.WP.generating") + ": " + String.format("%.0f", ConfigWI.dayPowerSolarXPTransmitter) + " EU/t";
                    case NIGHT:
                        return Localization.translate("gui.WP.generating") + ": " + String.format("%.0f", ConfigWI.nightPowerSolarXPTransmitter) + " EU/t";
                    default:
                        return Localization.translate("gui.WP.generating") + ": 0 EU/t";
                }
            }));
    }
    
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mouseX -= this.guiLeft;
        mouseY -= this.guiTop;
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.bindTexture();
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        
        if(this.container.base.getIsOn())
            this.drawTexturedModalRect(this.guiLeft + 95, this.guiTop + 52, 199, 23, 20, 20);
        else
            this.drawTexturedModalRect(this.guiLeft + 95, this.guiTop + 52, 177, 23, 20, 20);
        
        if(container.base.isSolar())
            switch(this.container.base.getGenState()) {
                case DAY:
                    this.drawTexturedModalRect(this.guiLeft + 141, this.guiTop + 20, 177, 3, 14, 14);
                    break;
                case NIGHT:
                    this.drawTexturedModalRect(this.guiLeft + 141, this.guiTop + 20, 191, 3, 14, 14);
                    break;
                case NONE:
                    this.drawTexturedModalRect(this.guiLeft + 141, this.guiTop + 20, 205, 3, 14, 14);
                    break;
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
        this.fontRenderer.drawString(name, (this.xSize - this.fontRenderer.getStringWidth(name)) / 2, 6, 4210752);
        
        String amountXPTransmit = Localization.translate("gui.xp.amount.transmit") + ": ";
        String sendingMode = Localization.translate("gui.xp.mode.send");
        String consumingMode = Localization.translate("gui.xp.mode.consume");
        String progress = Localization.translate("gui.xp.gen.progress") + ": ";
        
        String progressAll = progress + this.container.base.getPercentageGeneration() + " %";
        
        this.fontRenderer.drawString(amountXPTransmit + this.container.base.getAmountXPTransmit() + " XP", 55, 41, 4210752);
        this.fontRenderer.drawString(this.container.base.getIsSendingMode() ? sendingMode : consumingMode,
                this.container.base.getIsSendingMode() ? (this.xSize - this.fontRenderer.getStringWidth(sendingMode)) / 2
                        : (this.xSize - this.fontRenderer.getStringWidth(consumingMode)) / 2, 74, 4210752);
        this.fontRenderer.drawString(progressAll, (this.xSize - this.fontRenderer.getStringWidth(progressAll)) / 2, 32, 4210752);
        
        this.handleTransmitterTooltips(mouseX, mouseY);
    }
    
    private void handleTransmitterTooltips(int x, int y) {
        if(x >= 46 && x <= 130 && y >= 74 && y <= 81) {
            Set<String> namesSet = this.container.base.getPlayerNamesSet();
            List<String> list = Lists.newArrayList();
            if(namesSet.isEmpty())
                list = Collections.singletonList(Localization.translate("gui.xp.no.players"));
            else
                list.addAll(namesSet);
            this.drawTooltip(x, y, list);
        }
    }
    
    @Override
    protected ResourceLocation getTexture() {
        return new ResourceLocation(Reference.MOD_ID, "textures/gui/guiexptransmitter.png");
    }
    
}
