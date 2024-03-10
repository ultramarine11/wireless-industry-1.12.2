package ru.wirelesstools.gui;

import com.mojang.authlib.GameProfile;
import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.gui.CustomGauge;
import ic2.core.gui.EnergyGauge;
import ic2.core.gui.Gauge;
import ic2.core.gui.GuiElement;
import ic2.core.init.Localization;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.wirelesstools.Reference;
import ru.wirelesstools.container.ContainerWChPlayer;
import ru.wirelesstools.gui.elements.CustomWIButtonBT;
import ru.wirelesstools.gui.elements.CustomWIButtonCommon;
import ru.wirelesstools.utils.GUIUtility;

import java.util.Collections;

@SideOnly(Side.CLIENT)
public class GuiWChPlayer extends GuiIC2<ContainerWChPlayer> {
    
    private final int xPosGauge1;
    
    public GuiWChPlayer(ContainerWChPlayer container) {
        super(container, 196);
        this.xPosGauge1 = (this.xSize - (32 * 2 + 12 + 3 * 2)) / 2;
        this.addElement(EnergyGauge.asBar(this, this.xPosGauge1 + 3, 27, container.base));
        this.addElement(new CustomGauge(this, this.xPosGauge1 + 32 + 12 + 3 * 2 + 5, 27,
                () -> (double)container.base.getEnergyRF() / container.base.getMaxEnergyRF(),
                new Gauge.GaugePropertyBuilder(181, 52, 24, 9,
                        Gauge.GaugePropertyBuilder.GaugeOrientation.Right)
                        .withTexture(this.getTexture())
                        .withBackground(-4, -4, 32, 17, 177, 33)
                        .build())
                .withTooltip(() -> GUIUtility.formatNumber(container.base.getEnergyRF()) + " / "
                        + GUIUtility.formatNumber(container.base.getMaxEnergyRF()) + " RF"));
    
        this.addElement(new CustomWIButtonCommon(this, 7, 60, 20, 20, 8)
                .withTooltip(() -> container.base.getIsModeGP() ? "gui.WChP.modeGP" : "gui.WChP.modeRadius"));
        
        this.addElement(new CustomWIButtonBT(this, (this.xSize - 56) / 2, 78, 56, 15, mouseButton -> {
            switch(mouseButton) {
                case left:
                    IC2.network.get(false).initiateClientTileEntityEvent(container.base, 0);
                    break;
                case right:
                    IC2.network.get(false).initiateClientTileEntityEvent(container.base, 1);
                    break;
            }
        }).withText(() -> Localization.translate("gui.WChP.radius") + ": "
                + container.base.getRadius()).withEnableHandler(() -> !container.base.getIsModeGP()));
        
        this.addElement(new CustomWIButtonBT(this, ((this.xSize - 48) / 2), 44, 48, 12, mouseButton -> {
            switch(mouseButton) {
                case left:
                    IC2.network.get(false).initiateClientTileEntityEvent(container.base, 4);
                    break;
                case right:
                    IC2.network.get(false).initiateClientTileEntityEvent(container.base, 5);
                    break;
            }
        }).withText(() -> String.valueOf(container.base.getAutoConversionValueEuToRf())).withTooltip("EU -> RF")
                .withEnableHandler(() -> container.base.getConversionMode() == 1));
        
        this.addElement(new CustomWIButtonBT(this, ((this.xSize - 48) / 2), 44, 48, 12, mouseButton -> {
            switch(mouseButton) {
                case left:
                    IC2.network.get(false).initiateClientTileEntityEvent(container.base, 6);
                    break;
                case right:
                    IC2.network.get(false).initiateClientTileEntityEvent(container.base, 7);
                    break;
            }
        }).withText(() -> String.valueOf(container.base.getAutoConversionValueRfToEu())).withTooltip("RF -> EU")
                .withEnableHandler(() -> container.base.getConversionMode() == 2));
        
        this.addElement(new CustomWIButtonBT(this, ((this.xSize - 68) / 2), 60, 68, 14,
                mouseButton -> IC2.network.get(false).initiateClientTileEntityEvent(container.base, 2))
                .withText(() -> this.container.base.getIsChargerPrivate()
                        ? Localization.translate("gui.WChP.private")
                        : Localization.translate("gui.WChP.public"))
                .withTooltip(Localization.translate("gui.wind.switch.privacy")).withEnableHandler(() -> !container.base.getIsModeGP()));
        
        this.addElement(new CustomWIButtonCommon(this, this.xPosGauge1 + 32 + 3, 27, 12, 12, 3)
                .withTooltip(() -> {
                    switch(container.base.getConversionMode()) {
                        case 1:
                            return "gui.WChP.autoconversion.EU";
                        case 2:
                            return "gui.WChP.autoconversion.RF";
                        case 0:
                        default:
                            return Localization.translate("gui.WChP.autoconversion.off");
                        
                    }
                }));
    }
    
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mouseX -= this.guiLeft;
        mouseY -= this.guiTop;
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.bindTexture();
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        
        switch(this.container.base.getConversionMode()) {
            case 1: // EU to RF
                this.drawTexturedModalRect(this.guiLeft + this.xPosGauge1 + 32 + 3, this.guiTop + 27, 181, 64, 12, 12);
                this.drawTexturedModalRect(this.guiLeft + 64, this.guiTop + 44, 181, 79, 48, 12);
                break;
            case 2: // RF to EU
                this.drawTexturedModalRect(this.guiLeft + this.xPosGauge1 + 32 + 3, this.guiTop + 27, 194, 64, 12, 12);
                this.drawTexturedModalRect(this.guiLeft + 64, this.guiTop + 44, 181, 79, 48, 12);
                break;
            case 0: // off
            default:
                this.drawTexturedModalRect(this.guiLeft + this.xPosGauge1 + 32 + 3, this.guiTop + 27, 207, 64, 12, 12);
                break;
        }
        
        if(!this.container.base.getIsModeGP()) {
            this.drawTexturedModalRect(this.guiLeft + 54, this.guiTop + 60, 181, 94, 68, 14);
            this.drawTexturedModalRect(this.guiLeft + 60, this.guiTop + 78, 187, 112, 56, 15);
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
        GameProfile chargerOwner = this.container.base.getOwner();
        String ownerAll = chargerOwner != null
                ? Localization.translate("gui.WChP.owner") + ": " + chargerOwner.getName()
                : Localization.translate("gui.wi.err.noowner");
        
        int nmPos4 = (this.xSize - this.fontRenderer.getStringWidth(ownerAll)) / 2;
        
        this.fontRenderer.drawString(ownerAll, nmPos4, 98, 4210752);
    
        if(mouseX >= 151 && mouseX <= 168 && mouseY >= 55 && mouseY <= 108)
            this.drawTooltip(mouseX, mouseY, Collections.singletonList(Localization.translate("gui.VChP.slot.trusted.player")));
    }
    
    @Override
    protected ResourceLocation getTexture() {
        return new ResourceLocation(Reference.MOD_ID, "textures/gui/gui_wchargerplayer.png");
    }
    
}
