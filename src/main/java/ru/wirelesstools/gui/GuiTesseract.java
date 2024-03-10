package ru.wirelesstools.gui;

import com.mojang.authlib.GameProfile;
import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.gui.GuiElement;
import ic2.core.init.Localization;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.wirelesstools.Reference;
import ru.wirelesstools.container.ContainerTesseract;
import ru.wirelesstools.gui.elements.CustomWIButtonBT;
import ru.wirelesstools.gui.elements.CustomWIButtonCommon;
import ru.wirelesstools.utils.GUIUtility;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiTesseract extends GuiIC2<ContainerTesseract> {
    
    private int tickerGui = 40;
    private byte colorRef = 0;
    private int guiScreenID;
    
    public GuiTesseract(ContainerTesseract container) {
        super(container, 191, 208);
        this.guiScreenID = 1;
        this.addElement(new CustomWIButtonBT(this, 141, 18, 11, 11, mouseButton -> {
            switch(mouseButton) {
                case left:
                    if(GuiScreen.isShiftKeyDown())
                        IC2.network.get(false).initiateClientTileEntityEvent(container.base, 2);
                    else if(GuiScreen.isCtrlKeyDown())
                        IC2.network.get(false).initiateClientTileEntityEvent(container.base, 4);
                    else if(GuiScreen.isAltKeyDown())
                        IC2.network.get(false).initiateClientTileEntityEvent(container.base, 6);
                    else
                        IC2.network.get(false).initiateClientTileEntityEvent(container.base, 0);
                    break;
                case right:
                    if(GuiScreen.isShiftKeyDown())
                        IC2.network.get(false).initiateClientTileEntityEvent(container.base, 3);
                    else if(GuiScreen.isCtrlKeyDown())
                        IC2.network.get(false).initiateClientTileEntityEvent(container.base, 5);
                    else if(GuiScreen.isAltKeyDown())
                        IC2.network.get(false).initiateClientTileEntityEvent(container.base, 7);
                    else
                        IC2.network.get(false).initiateClientTileEntityEvent(container.base, 1);
                    break;
            }
        }).withEnableHandler(() -> GuiTesseract.this.guiScreenID == 1));
        this.addElement(new CustomWIButtonBT(this, 154, 18, 11, 11, mouseButton -> {
            switch(mouseButton) {
                case left:
                    if(GuiScreen.isShiftKeyDown())
                        IC2.network.get(false).initiateClientTileEntityEvent(container.base, 10);
                    else if(GuiScreen.isCtrlKeyDown())
                        IC2.network.get(false).initiateClientTileEntityEvent(container.base, 12);
                    else if(GuiScreen.isAltKeyDown())
                        IC2.network.get(false).initiateClientTileEntityEvent(container.base, 14);
                    else
                        IC2.network.get(false).initiateClientTileEntityEvent(container.base, 8);
                    break;
                case right:
                    if(GuiScreen.isShiftKeyDown())
                        IC2.network.get(false).initiateClientTileEntityEvent(container.base, 11);
                    else if(GuiScreen.isCtrlKeyDown())
                        IC2.network.get(false).initiateClientTileEntityEvent(container.base, 13);
                    else if(GuiScreen.isAltKeyDown())
                        IC2.network.get(false).initiateClientTileEntityEvent(container.base, 15);
                    else
                        IC2.network.get(false).initiateClientTileEntityEvent(container.base, 9);
                    break;
            }
        }).withEnableHandler(() -> GuiTesseract.this.guiScreenID == 1));
        this.addElement(new CustomWIButtonBT(this, 56, 18, 11, 11, mouseButton -> {
            switch(mouseButton) {
                case left:
                    IC2.network.get(false).initiateClientTileEntityEvent(container.base, 17);
                    break;
                case right:
                    IC2.network.get(false).initiateClientTileEntityEvent(container.base, 18);
                    break;
            }
        }));
        this.addElement(new CustomWIButtonBT(this, 69, 18, 11, 11, mouseButton -> {
            switch(mouseButton) {
                case left:
                    IC2.network.get(false).initiateClientTileEntityEvent(container.base, 19);
                    break;
                case right:
                    IC2.network.get(false).initiateClientTileEntityEvent(container.base, 20);
                    break;
            }
        }).withEnableHandler(() -> GuiTesseract.this.guiScreenID == 1));
        this.addElement(new CustomWIButtonCommon(this, 103, 18, 9, 12, 16).withTooltip(() -> {
            if(container.base.isSendingEnergy())
                return Localization.translate("gui.tesseract.sending.energy");
            else
                return Localization.translate("gui.tesseract.receiving.energy");
        }).withEnableHandler(() -> GuiTesseract.this.guiScreenID == 1));
        
        this.addElement(new CustomWIButtonBT(this, 149, 31, 11, 11, mouseButton -> {
            switch(mouseButton) {
                case left:
                    IC2.network.get(false).initiateClientTileEntityEvent(container.base, 21);
                    break;
                case right:
                    IC2.network.get(false).initiateClientTileEntityEvent(container.base, 22);
                    break;
            }
        }).withEnableHandler(() -> GuiTesseract.this.guiScreenID == 2));
        this.addElement(new CustomWIButtonBT(this, 149, 59, 11, 11, mouseButton -> {
            switch(mouseButton) {
                case left:
                    IC2.network.get(false).initiateClientTileEntityEvent(container.base, 23);
                    break;
                case right:
                    IC2.network.get(false).initiateClientTileEntityEvent(container.base, 24);
                    break;
            }
        }).withEnableHandler(() -> GuiTesseract.this.guiScreenID == 2));
        this.addElement(new CustomWIButtonBT(this, 149, 87, 11, 11, mouseButton -> {
            switch(mouseButton) {
                case left:
                    IC2.network.get(false).initiateClientTileEntityEvent(container.base, 25);
                    break;
                case right:
                    IC2.network.get(false).initiateClientTileEntityEvent(container.base, 26);
                    break;
            }
        }).withEnableHandler(() -> GuiTesseract.this.guiScreenID == 2));
        this.addElement(new CustomWIButtonBT(this, 162, 31, 11, 11, mouseButton -> {
            switch(mouseButton) {
                case left:
                    IC2.network.get(false).initiateClientTileEntityEvent(container.base, 27);
                    break;
                case right:
                    IC2.network.get(false).initiateClientTileEntityEvent(container.base, 28);
                    break;
            }
        }).withEnableHandler(() -> GuiTesseract.this.guiScreenID == 2));
        this.addElement(new CustomWIButtonBT(this, 162, 59, 11, 11, mouseButton -> {
            switch(mouseButton) {
                case left:
                    IC2.network.get(false).initiateClientTileEntityEvent(container.base, 29);
                    break;
                case right:
                    IC2.network.get(false).initiateClientTileEntityEvent(container.base, 30);
                    break;
            }
        }).withEnableHandler(() -> GuiTesseract.this.guiScreenID == 2));
        this.addElement(new CustomWIButtonBT(this, 162, 87, 11, 11, mouseButton -> {
            switch(mouseButton) {
                case left:
                    IC2.network.get(false).initiateClientTileEntityEvent(container.base, 31);
                    break;
                case right:
                    IC2.network.get(false).initiateClientTileEntityEvent(container.base, 32);
                    break;
            }
        }).withEnableHandler(() -> GuiTesseract.this.guiScreenID == 2));
        
        this.addElement(new CustomWIButtonCommon(this, 111, 31, 9, 12, 33).withTooltip(() -> {
            if(container.base.isSendingFluid_1())
                return Localization.translate("gui.tesseract.sending.fluid");
            else
                return Localization.translate("gui.tesseract.receiving.fluid");
        }).withEnableHandler(() -> GuiTesseract.this.guiScreenID == 2));
        this.addElement(new CustomWIButtonCommon(this, 111, 59, 9, 12, 34).withTooltip(() -> {
            if(container.base.isSendingFluid_2())
                return Localization.translate("gui.tesseract.sending.fluid");
            else
                return Localization.translate("gui.tesseract.receiving.fluid");
        }).withEnableHandler(() -> GuiTesseract.this.guiScreenID == 2));
        this.addElement(new CustomWIButtonCommon(this, 111, 87, 9, 12, 35).withTooltip(() -> {
            if(container.base.isSendingFluid_3())
                return Localization.translate("gui.tesseract.sending.fluid");
            else
                return Localization.translate("gui.tesseract.receiving.fluid");
        }).withEnableHandler(() -> GuiTesseract.this.guiScreenID == 2));
        
        
        this.addElement(new CustomWIButtonBT(this, 57, 61, 17, 17, mouseButton -> this.guiScreenID = 2).withEnableHandler(() -> GuiTesseract.this.guiScreenID == 1)
                .withTooltip(Localization.translate("gui.tesseract.fluid.settings")));
        this.addElement(new CustomWIButtonBT(this, 9, 7, 19, 12, mouseButton -> this.guiScreenID = 1).withEnableHandler(() -> GuiTesseract.this.guiScreenID == 2)
                .withTooltip(Localization.translate("gui.tesseract.back")));
    }
    
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mouseX -= this.guiLeft;
        mouseY -= this.guiTop;
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.bindTexture();
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        
        switch(this.guiScreenID) {
            case 1:
                if(this.container.base.isSendingEnergy())
                    this.drawTexturedModalRect(this.guiLeft + 103, this.guiTop + 18, 230, 44, 9, 12);
                else
                    this.drawTexturedModalRect(this.guiLeft + 103, this.guiTop + 18, 219, 44, 9, 12);
                if(this.container.base.getEnergyRef().getEnergy() > 0.0) {
                    int l = this.container.base.gaugeEnergy(40);
                    this.drawTexturedModalRect(this.guiLeft + 175, this.guiTop + 164 - l + 1, 192, 42 - l + 1,
                            9, l);
                }
                break;
            case 2:
                if(this.container.base.isSendingFluid_1())
                    this.drawTexturedModalRect(this.guiLeft + 111, this.guiTop + 31, 230, 44, 9, 12);
                else
                    this.drawTexturedModalRect(this.guiLeft + 111, this.guiTop + 31, 219, 44, 9, 12);
                if(this.container.base.isSendingFluid_2())
                    this.drawTexturedModalRect(this.guiLeft + 111, this.guiTop + 59, 230, 44, 9, 12);
                else
                    this.drawTexturedModalRect(this.guiLeft + 111, this.guiTop + 59, 219, 44, 9, 12);
                if(this.container.base.isSendingFluid_3())
                    this.drawTexturedModalRect(this.guiLeft + 111, this.guiTop + 87, 230, 44, 9, 12);
                else
                    this.drawTexturedModalRect(this.guiLeft + 111, this.guiTop + 87, 219, 44, 9, 12);
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
        this.tickerGui--;
        if(this.tickerGui < 0) {
            this.tickerGui = 40;
            this.colorRef++;
            if(this.colorRef > 2)
                this.colorRef = 0;
        }
        switch(this.guiScreenID) {
            case 1:
                String name = Localization.translate(this.container.base.getName());
                // 41607 - эндер мятный оттенок
                // 45152 - зелёненький оттенок
                // 43634 - более красивый зелёно-эндер цвет
                switch(this.colorRef) {
                    case 0:
                        this.fontRenderer.drawString(name, (this.xSize - this.fontRenderer.getStringWidth(name)) / 2, 4, 41607);
                        break;
                    case 1:
                        this.fontRenderer.drawString(name, (this.xSize - this.fontRenderer.getStringWidth(name)) / 2, 4, 45152);
                        break;
                    case 2:
                        this.fontRenderer.drawString(name, (this.xSize - this.fontRenderer.getStringWidth(name)) / 2, 4, 43634);
                        break;
                }
                
                if(this.container.base.isSendingEnergy())
                    this.fontRenderer.drawString(Localization.translate("gui.tesseract.wireless.transfer") + ": "
                            + String.format("%.0f", this.container.base.getWirelessTransferAmount()) + " EU/t", 107, 34, 16777215);
                
                this.fontRenderer.drawString(Localization.translate("gui.tesseract.energy.channel") + ": "
                        + this.container.base.getChannelEnergy(), 54, 34, 16777215);
                
                GameProfile owner = this.container.base.getOwner();
                if(owner != null)
                    this.fontRenderer.drawString(Localization.translate("gui.tesseract.owner") + ": "
                            + owner.getName(), 55, 100, 16777215);
                else
                    this.fontRenderer.drawString(Localization.translate("gui.wi.err.noowner"), 55, 100, 16777215);
                
                this.handleTesseractTooltips_1(mouseX, mouseY);
                break;
            case 2:
                String fluidSettings = Localization.translate("gui.tesseract.fluid.settings");
                // 41607 - эндер мятный оттенок
                // 45152 - зелёненький оттенок
                // 43634 - более красивый зелёно-эндер цвет
                switch(this.colorRef) {
                    case 0:
                        this.fontRenderer.drawString(fluidSettings, (this.xSize - this.fontRenderer.getStringWidth(fluidSettings)) / 2, 4, 41607);
                        break;
                    case 1:
                        this.fontRenderer.drawString(fluidSettings, (this.xSize - this.fontRenderer.getStringWidth(fluidSettings)) / 2, 4, 45152);
                        break;
                    case 2:
                        this.fontRenderer.drawString(fluidSettings, (this.xSize - this.fontRenderer.getStringWidth(fluidSettings)) / 2, 4, 43634);
                        break;
                }
                FluidStack fs1 = this.container.base.getFluidTank_1().getFluid();
                FluidStack fs2 = this.container.base.getFluidTank_2().getFluid();
                FluidStack fs3 = this.container.base.getFluidTank_3().getFluid();
                if(fs1 != null) {
                    this.fontRenderer.drawString(fs1.getLocalizedName(), 20, 30, 16777215);
                    this.fontRenderer.drawString(Localization.translate("gui.tesseract.fs.amount") + ": "
                            + fs1.amount + " mB", 20, 39, 16777215);
                }
                else
                    this.fontRenderer.drawString(Localization.translate("gui.tesseract.fluid.empty"), 20, 34, 16777215);
                
                if(fs2 != null) {
                    this.fontRenderer.drawString(fs2.getLocalizedName(), 20, 58, 16777215);
                    this.fontRenderer.drawString(Localization.translate("gui.tesseract.fs.amount") + ": "
                            + fs2.amount + " mB", 20, 67, 16777215);
                }
                else
                    this.fontRenderer.drawString(Localization.translate("gui.tesseract.fluid.empty"), 20, 62, 16777215);
                
                if(fs3 != null) {
                    this.fontRenderer.drawString(fs3.getLocalizedName(), 20, 86, 16777215);
                    this.fontRenderer.drawString(Localization.translate("gui.tesseract.fs.amount") + ": "
                            + fs3.amount + " mB", 20, 95, 16777215);
                }
                else
                    this.fontRenderer.drawString(Localization.translate("gui.tesseract.fluid.empty"), 20, 90, 16777215);
                
                this.fontRenderer.drawString(Localization.translate("gui.tesseract.fluid.channel") + ": "
                        + this.container.base.getChannelFluid_1(), 132, 43, 16777215);
                this.fontRenderer.drawString(Localization.translate("gui.tesseract.fluid.channel") + ": "
                        + this.container.base.getChannelFluid_2(), 132, 71, 16777215);
                this.fontRenderer.drawString(Localization.translate("gui.tesseract.fluid.channel") + ": "
                        + this.container.base.getChannelFluid_3(), 132, 99, 16777215);
                break;
        }
    }
    
    private void handleTesseractTooltips_1(int x, int y) {
        int leftWT = 500;
        int rightWT = 100;
        List<String> list = new ArrayList<>();
        if(GuiScreen.isShiftKeyDown()) {
            leftWT = 5000;
            rightWT = 1000;
        }
        else if(GuiScreen.isCtrlKeyDown()) {
            leftWT = 50;
            rightWT = 10;
        }
        else if(GuiScreen.isAltKeyDown()) {
            leftWT = 5;
            rightWT = 1;
        }
        
        if((x >= 141 && x <= 151 && y >= 18 && y <= 28) || (x >= 154 && x <= 164 && y >= 18 && y <= 28)) {
            list.add(leftWT + "/" + rightWT);
            this.drawTooltip(x, y, list);
        }
        
        if((x >= 56 && x <= 66 && y >= 18 && y <= 28) || (x >= 69 && x <= 79 && y >= 18 && y <= 28)) {
            list.add(5 + "/" + 1);
            this.drawTooltip(x, y, list);
        }
        
        if(x >= 21 && x <= 36 && y >= 25 && y <= 37) {
            list.add(Localization.translate("gui.tesseract.energy.icon"));
            this.drawTooltip(x, y, list);
        }
        
        if(x >= 23 && x <= 34 && y >= 63 && y <= 75) {
            list.add(Localization.translate("gui.tesseract.fluid.icon"));
            this.drawTooltip(x, y, list);
        }
        
        if(x >= 5 && x <= 14 && y >= 5 && y <= 14) {
            list.add(Localization.translate("gui.tesseract.info"));
            list.add(Localization.translate("gui.tesseract.keys.for.channels"));
            this.drawTooltip(x, y, list);
        }
        
        if(x >= 175 && x <= 183 && y >= 124 && y <= 165) {
            list.add(GUIUtility.formatNumber(this.container.base.getEnergyRef().getEnergy()) + " / "
                    + GUIUtility.formatNumber(this.container.base.getEnergyRef().getCapacity())
                    + " " + Localization.translate("ic2.generic.text.EU"));
            this.drawTooltip(x, y, list);
        }
    }
    
    @Override
    protected ResourceLocation getTexture() {
        if(this.guiScreenID == 2) {
            return new ResourceLocation(Reference.MOD_ID, "textures/gui/gui_tesseract_2.png");
        }
        return new ResourceLocation(Reference.MOD_ID, "textures/gui/gui_tesseract.png");
    }
}
