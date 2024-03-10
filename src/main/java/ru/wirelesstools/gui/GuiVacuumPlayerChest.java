package ru.wirelesstools.gui;

import com.mojang.authlib.GameProfile;
import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.init.Localization;
import ic2.core.util.StackUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.wirelesstools.Reference;
import ru.wirelesstools.container.ContainerVacuumPlayerChest;
import ru.wirelesstools.gui.elements.CustomWIButtonBT;
import ru.wirelesstools.items.tools.ItemPlayerModule;

import java.util.Collections;

@SideOnly(Side.CLIENT)
public class GuiVacuumPlayerChest extends GuiIC2<ContainerVacuumPlayerChest> {
    
    public GuiVacuumPlayerChest(ContainerVacuumPlayerChest container) {
        super(container, 228);
        this.addElement(new CustomWIButtonBT(this, 108, 78, 56, 15, mouseButton -> {
            switch(mouseButton) {
                case left:
                    IC2.network.get(false).initiateClientTileEntityEvent(container.base, 0);
                    break;
                case right:
                    IC2.network.get(false).initiateClientTileEntityEvent(container.base, 1);
                    break;
            }
        }).withText(() -> Localization.translate("gui.VChP.radius") + ": "
                + container.base.getRadius()));
        
        this.addElement(new CustomWIButtonBT(this, ((this.xSize - 48) / 2), 123, 48, 15,
                mouseButton -> IC2.network.get(false).initiateClientTileEntityEvent(container.base, 2))
                .withText(() -> container.base.getIsWorking()
                        ? Localization.translate("gui.VChP.turned.on")
                        : Localization.translate("gui.VChP.turned.off"))
                .withTooltip(Localization.translate("gui.wind.switch")));
    }
    
    protected void drawForegroundLayer(int mouseX, int mouseY) {
        super.drawForegroundLayer(mouseX, mouseY);
        String playerString = "<" + Localization.translate("gui.VChP.empty") + ">";
        ItemStack modulestack = this.container.base.modulePlayerSlot.get();
        if(modulestack != null && modulestack.getItem() instanceof ItemPlayerModule) {
            GameProfile moduleOwner = NBTUtil.readGameProfileFromNBT(StackUtil.getOrCreateNbtData(modulestack)
                    .getCompoundTag("playerModulegameprofile"));
            if(moduleOwner != null)
                playerString = moduleOwner.getName();
        }
        String trustedPlayerString = Localization.translate("gui.VChP.trusted.player") + ": " + playerString;
        GameProfile vacuumChestOwner = this.container.base.getOwner();
        String ownerAll = vacuumChestOwner != null
                ? Localization.translate("gui.VChP.owner") + ": " + vacuumChestOwner.getName()
                : Localization.translate("gui.wi.err.noowner");
        
        int nmPos4 = (this.xSize - this.fontRenderer.getStringWidth(ownerAll)) / 2;
        int nmPos5 = (this.xSize - this.fontRenderer.getStringWidth(trustedPlayerString)) / 2;
        
        this.fontRenderer.drawString(ownerAll, nmPos4, 110, 4210752);
        this.fontRenderer.drawString(trustedPlayerString, nmPos5, 98, 4210752);
        this.handleTooltips_1(mouseX, mouseY);
    }
    
    private void handleTooltips_1(int x, int y) {
        if(x >= 79 && x <= 96 && y >= 77 && y <= 94)
            this.drawTooltip(x, y, Collections.singletonList(Localization.translate("gui.VChP.slot.trusted.player")));
    }
    
    @Override
    protected ResourceLocation getTexture() {
        return new ResourceLocation(Reference.MOD_ID, "textures/gui/gui_vacuumplayerchest.png");
    }
    
}
