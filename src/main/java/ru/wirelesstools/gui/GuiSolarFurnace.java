package ru.wirelesstools.gui;

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
import ru.wirelesstools.container.ContainerSolarFurnace;
import ru.wirelesstools.utils.GUIUtility;

@SideOnly(Side.CLIENT)
public class GuiSolarFurnace extends GuiIC2<ContainerSolarFurnace> {
    
    public GuiSolarFurnace(ContainerSolarFurnace container) {
        super(container, 176, 166);
        this.addElement(new CustomGauge(this, 50, 66, container.base::getEnergyFillRatio,
                new Gauge.GaugePropertyBuilder(111, 129, 33, 9, Gauge.GaugePropertyBuilder.GaugeOrientation.Right)
                        .withTexture(new ResourceLocation(Reference.MOD_ID, "textures/gui/gauge/wi_custom_gauges.png"))
                        .withBackground(-1, -1, 35, 11, 110, 117)
                        .build())
                .withTooltip(() -> GUIUtility.formatNumber(container.base.getEnergyStored())
                        + " / " + GUIUtility.formatNumber(container.base.getMaxEnergy()) + " EU"));
        
        this.addElement(new CustomGauge(this, 80, 35, container.base::getProgress,
                new Gauge.GaugePropertyBuilder(177, 14, 22, 16, Gauge.GaugePropertyBuilder.GaugeOrientation.Right)
                        .withTexture(new ResourceLocation(Reference.MOD_ID, "textures/gui/GUISolarFurnace.png"))
                        .build()));
        this.addElement(new Area(this, 57, 42, 14, 14).withTooltip(() -> {
            switch(container.base.getGenState()) {
                case DAY:
                    return String.format("%.0f", container.base.getDayPower()) + " EU/t";
                case NIGHT:
                    return String.format("%.0f", container.base.getNightPower()) + " EU/t";
                case NONE:
                default:
                    return "0 EU/t";
            }
        }));
    }
    
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mouseX -= this.guiLeft;
        mouseY -= this.guiTop;
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.bindTexture();
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        
        switch(this.container.base.getGenState()) {
            case DAY:
                this.drawTexturedModalRect(this.guiLeft + 57, this.guiTop + 42, 176, 0, 14, 14);
                break;
            case NIGHT:
                this.drawTexturedModalRect(this.guiLeft + 57, this.guiTop + 42, 190, 0, 14, 14);
                break;
        }
        
        for(GuiElement<?> element : this.elements) {
            if(element.isEnabled())
                element.drawBackground(mouseX, mouseY);
        }
    }
    
    protected void drawForegroundLayer(int mouseX, int mouseY) {
        super.drawForegroundLayer(mouseX, mouseY);
        String name = Localization.translate(this.container.base.getName());
        this.fontRenderer.drawString(name, (this.xSize - this.fontRenderer.getStringWidth(name)) / 2, 5, 4210752);
    }
    
    @Override
    protected ResourceLocation getTexture() {
        return new ResourceLocation(Reference.MOD_ID, "textures/gui/guisolarfurnace.png");
    }
}
