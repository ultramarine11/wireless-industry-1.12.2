package ru.wirelesstools.general;

import ic2.api.item.ElectricItem;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import ru.wirelesstools.config.ConfigWI;
import ru.wirelesstools.items.tools.LuckyVajra;
import ru.wirelesstools.items.tools.PortableElectricCharger;

public class OverlayWI {
    
    static final Minecraft mc = Minecraft.getMinecraft();
    
    public OverlayWI() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    public static String getTextPercentage(double percentage) {
        return Math.round(percentage) + " %";
    }
    
    @SubscribeEvent
    public void onRender(TickEvent.RenderTickEvent event) {
        if(ConfigWI.allowDisplaying && mc.world != null && mc.inGameHasFocus && Minecraft.isGuiEnabled() && !mc.gameSettings.showDebugInfo) {
            ItemStack handheldstack = mc.player.inventory.getCurrentItem();
            if(handheldstack.getItem() instanceof LuckyVajra || handheldstack.getItem() instanceof PortableElectricCharger) {
                double chargePercentage = ElectricItem.manager.getCharge(handheldstack) / ElectricItem.manager.getMaxCharge(handheldstack) * 100.0;
                if(chargePercentage >= 75.0)
                    mc.ingameGUI.drawString(mc.fontRenderer, OverlayWI.getTextPercentage(chargePercentage), ConfigWI.xPos, ConfigWI.yPos, 5635925); // green
                else if(chargePercentage < 75.0 && chargePercentage >= 50.0)
                    mc.ingameGUI.drawString(mc.fontRenderer, OverlayWI.getTextPercentage(chargePercentage), ConfigWI.xPos, ConfigWI.yPos, 16777045); // yellow
                else if(chargePercentage < 50.0 && chargePercentage >= 25.0)
                    mc.ingameGUI.drawString(mc.fontRenderer, OverlayWI.getTextPercentage(chargePercentage), ConfigWI.xPos, ConfigWI.yPos, 15041024); // orange
                else if(chargePercentage < 25.0)
                    mc.ingameGUI.drawString(mc.fontRenderer, OverlayWI.getTextPercentage(chargePercentage), ConfigWI.xPos, ConfigWI.yPos, 16733525); // red
                else
                    mc.ingameGUI.drawString(mc.fontRenderer, OverlayWI.getTextPercentage(chargePercentage), ConfigWI.xPos, ConfigWI.yPos, 16777215); // white
            }
            if(handheldstack.getItem() instanceof LuckyVajra) {
                mc.ingameGUI.drawString(mc.fontRenderer, "Y: " + Math.round(mc.player.posY),
                        ConfigWI.xPos, ConfigWI.yPos + mc.fontRenderer.FONT_HEIGHT + 1, 16777215);
            }
        }
    }
    
}
