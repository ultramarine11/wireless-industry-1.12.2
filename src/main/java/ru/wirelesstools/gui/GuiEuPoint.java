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
import ru.wirelesstools.container.ContainerEuPoint;
import ru.wirelesstools.gui.elements.CustomWIButtonCommon;
import ru.wirelesstools.utils.GUIUtility;

@SideOnly(Side.CLIENT)
public class GuiEuPoint extends GuiIC2<ContainerEuPoint> {
    
    public GuiEuPoint(ContainerEuPoint container) {
        super(container, 175, 166);
        this.addElement(new CustomWIButtonCommon(this, 66, 58, 44, 13, 0).withTooltip("gui.eupoint.switch.mode"));
        this.addElement(new CustomWIButtonCommon(this, 10, 60, 11, 11, 1).withTooltip("+1"));
        this.addElement(new CustomWIButtonCommon(this, 155, 60, 11, 11, 2).withTooltip("-1"));
        this.addElement(new CustomWIButtonCommon(this, 118, 17, 10, 11, 3)
                .withTooltip("gui.eupoint.switch.privacy"));
        this.addElement(new CustomWIButtonCommon(this, 25, 60, 19, 11, 4).withTooltip("+10"));
        this.addElement(new CustomWIButtonCommon(this, 132, 60, 19, 11, 5).withTooltip("-10"));
        this.addElement(new Area(this, 61, 15, 55, 16).withTooltip(() ->
                GUIUtility.formatNumber(this.container.base.getEnergyRef().getEnergy()) + " / "
                        + GUIUtility.formatNumber(this.container.base.getEnergyRef().getCapacity()) + " EU"));
    }
    
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mouseX -= this.guiLeft;
        mouseY -= this.guiTop;
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.bindTexture();
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        
        if(this.container.base.getEnergyRef().getEnergy() > 0.0) {
            int l = this.container.base.gaugeEnergy(47);
            this.drawTexturedModalRect(this.guiLeft + 65, this.guiTop + 19, 177, 2, l + 1, 9);
        }
        
        if(this.container.base.getIsPublic()) {
            this.drawTexturedModalRect(this.guiLeft + 118, this.guiTop + 17, 191, 14, 10, 11);
        }
        else {
            this.drawTexturedModalRect(this.guiLeft + 118, this.guiTop + 17, 179, 14, 10, 11);
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
        this.fontRenderer.drawString(name, (this.xSize - this.fontRenderer.getStringWidth(name)) / 2, 6, 16777215);
        
        String owner = Localization.translate("gui.WP.owner") + ": ";
        String noOwner = Localization.translate("gui.wi.err.noowner");
        String channel = Localization.translate("gui.WP.channel") + ": ";
        String wirelessTransmit = Localization.translate("gui.eupoint.wirelesstransmit") + ": ";
        String sendMode = Localization.translate("gui.eupoint.mode.send");
        String receiveMode = Localization.translate("gui.eupoint.mode.receive");
        
        String channelAll = channel + this.container.base.getChannel();
        String wtAll = wirelessTransmit + Math.round(this.container.base.getWirelessTransferLimit()) + " EU/t";
        
        GameProfile pointOwner = this.container.base.getOwner();
        String ownerAll = pointOwner != null ? owner + pointOwner.getName() : noOwner;
        
        int nmChPos = (this.xSize - this.fontRenderer.getStringWidth(channelAll)) / 2;
        int nmOwnerPos = (this.xSize - this.fontRenderer.getStringWidth(ownerAll)) / 2;
        int nmWtPos = (this.xSize - this.fontRenderer.getStringWidth(wtAll)) / 2;
        int nmSendPos = (this.xSize - this.fontRenderer.getStringWidth(sendMode)) / 2;
        int nmReceivePos = (this.xSize - this.fontRenderer.getStringWidth(receiveMode)) / 2;
        
        this.fontRenderer.drawString(wtAll, nmWtPos, 33, 16777215);
        this.fontRenderer.drawString(ownerAll, nmOwnerPos, 43, 16777215);
        this.fontRenderer.drawString(channelAll, nmChPos, 72, 16777215);
        this.fontRenderer.drawString(this.container.base.isSending() ? sendMode : receiveMode,
                this.container.base.isSending() ? nmSendPos + 1 : nmReceivePos + 1, 61, 16777215);
    }
    
    @Override
    protected ResourceLocation getTexture() {
        switch(this.container.base.getGuiType()) {
            case 1:
                return new ResourceLocation(Reference.MOD_ID, "textures/gui/gui_eupoint_1.png");
            case 2:
                return new ResourceLocation(Reference.MOD_ID, "textures/gui/gui_eupoint_2.png");
        }
        return new ResourceLocation(Reference.MOD_ID, "textures/gui/gui_eupoint_1.png");
    }
}
