package ru.wirelesstools.gui;

import com.mojang.authlib.GameProfile;
import ic2.core.GuiIC2;
import ic2.core.gui.EnergyGauge;
import ic2.core.init.Localization;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.wirelesstools.Reference;
import ru.wirelesstools.container.ContainerWSPersonal;
import ru.wirelesstools.gui.elements.CustomWIButtonCommon;

@SideOnly(Side.CLIENT)
public class GuiWSPersonal extends GuiIC2<ContainerWSPersonal> {
    
    public GuiWSPersonal(ContainerWSPersonal container) {
        super(container, 196);
        this.addElement(EnergyGauge.asBar(this, ((this.xSize - 32) / 2), 30, container.base));
        this.addElement(new CustomWIButtonCommon(this, 64, 66, 11, 11, 1).withTooltip("+1"));
        this.addElement(new CustomWIButtonCommon(this, 94, 66, 11, 11, 2).withTooltip("-1"));
    }
    
    protected void drawForegroundLayer(int mouseX, int mouseY) {
        super.drawForegroundLayer(mouseX, mouseY);
        GameProfile storageOwner = this.container.base.getOwner();
        String channelAll = Localization.translate("gui.WSBPersonal.channel") + ": " + this.container.base.getChannel();
        String ownerAll = storageOwner != null
                ? Localization.translate("gui.WSB.owner") + ": " + storageOwner.getName()
                : Localization.translate("gui.wi.err.noowner");
        
        int nmPos2 = (this.xSize - this.fontRenderer.getStringWidth(channelAll)) / 2;
        int nmPos3 = (this.xSize - this.fontRenderer.getStringWidth(ownerAll)) / 2;
        
        this.fontRenderer.drawString(channelAll, nmPos2, 52, 4210752);
        this.fontRenderer.drawString(ownerAll, nmPos3, 85, 4210752);
    }
    
    protected ResourceLocation getTexture() {
        return new ResourceLocation(Reference.MOD_ID, "textures/gui/gui_wsb_personal.png");
    }
    
}
