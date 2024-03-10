package ru.wirelesstools.gui;

import com.mojang.authlib.GameProfile;
import ic2.core.GuiIC2;
import ic2.core.gui.Area;
import ic2.core.gui.GuiElement;
import ic2.core.init.Localization;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.wirelesstools.Reference;
import ru.wirelesstools.container.ContainerWPP;
import ru.wirelesstools.gui.elements.CustomWIButtonCommon;
import ru.wirelesstools.tileentities.GenerationState;
import ru.wirelesstools.utils.GUIUtility;
import ru.wirelesstools.utils.Utilities;

@SideOnly(Side.CLIENT)
public class GUIWP extends GuiIC2<ContainerWPP> {
    
    public GUIWP(ContainerWPP container) {
        super(container, 194, 168);
        this.addElement(new CustomWIButtonCommon(this, 18, 63, 11, 11, 1).withTooltip("+1"));
        this.addElement(new CustomWIButtonCommon(this, 34, 63, 11, 11, 2).withTooltip("-1"));
        this.addElement(new CustomWIButtonCommon(this, 53, 60, 15, 15, 3).withTooltip("gui.WP.tooltip.switcher"));
        this.addElement(new Area(this, 24, 41, 14, 14).withTooltip(() -> {
            if(container.base.getGenState() == GenerationState.NONE) {
                return Localization.translate("gui.WP.no.generation");
            }
            return Utilities.calculateRemainTime(container.base);
        }));
    }
    
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mouseX -= this.guiLeft;
        mouseY -= this.guiTop;
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.bindTexture();
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        
        if(this.container.base.getEnergyRef().getEnergy() > 0) {
            int l = this.container.base.gaugeEnergy(47);
            this.drawTexturedModalRect(this.guiLeft + 19, this.guiTop + 24, 195, 0, l + 1, 14);
        }
        
        if(this.container.base.getIsActivePanel())
            this.drawTexturedModalRect(this.guiLeft + 52, this.guiTop + 59, 214, 31, 17, 17);
        else
            this.drawTexturedModalRect(this.guiLeft + 52, this.guiTop + 59, 195, 31, 17, 17);
        
        switch(this.container.base.getGenState()) {
            case DAY:
                this.drawTexturedModalRect(this.guiLeft + 24, this.guiTop + 41, 195, 15, 14, 14);
                break;
            case NIGHT:
                this.drawTexturedModalRect(this.guiLeft + 24, this.guiTop + 41, 210, 15, 14, 14);
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
        int nmPosName = (this.xSize - this.fontRenderer.getStringWidth(name)) / 2;
        this.fontRenderer.drawString(name, nmPosName, 6, 16777215);
        
        String generation = Localization.translate("gui.WP.generating") + ": ";
        String energyAll = GUIUtility.formatNumber(this.container.base.getEnergyRef().getEnergy()) + " / "
                + GUIUtility.formatNumber(this.container.base.getEnergyRef().getCapacity()) + " EU";
        String channelAll = Localization.translate("gui.WP.channel") + ": " + this.container.base.getChannel();
        String wtAll = Localization.translate("gui.WP.wirelesstransmit") + ": "
                + this.container.base.getWirelessTransferLimit() + " EU/t";
        String genAll;
        switch(this.container.base.getGenState()) {
            case DAY:
                genAll = generation + this.container.base.getDayPower() + " EU/t";
                break;
            case NIGHT:
                genAll = generation + this.container.base.getNightPower() + " EU/t";
                break;
            default:
                genAll = generation + "0 EU/t";
                break;
        }
        
        GameProfile panelOwner = this.container.base.getOwner();
        String ownerAll = panelOwner != null
                ? Localization.translate("gui.WP.owner") + ": " + panelOwner.getName()
                : Localization.translate("gui.wi.err.noowner");
        
        this.fontRenderer.drawString(energyAll, 75, 21, 16777215);
        this.fontRenderer.drawString(genAll, 75, 31, 16777215);
        this.fontRenderer.drawString(wtAll, 75, 41, 16777215);
        this.fontRenderer.drawString(ownerAll, 75, 51, 43520); // green formatting color
        this.fontRenderer.drawString(channelAll, 75, 61, 16777215);
    }
    
    @Override
    protected ResourceLocation getTexture() {
        return new ResourceLocation(Reference.MOD_ID, "textures/gui/wirelesssolarpanel_2.png");
    }
    
}
