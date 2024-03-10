package ru.wirelesstools.gui.elements;

import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.gui.CustomButton;
import ic2.core.gui.MouseButton;
import net.minecraft.tileentity.TileEntity;

public class CustomWIButtonCommon extends CustomButton {
    
    public CustomWIButtonCommon(GuiIC2<?> gui, int x, int y, int width, int height, final int event) {
        super(gui, x, y, width, height, (button) -> {
            if(gui.getContainer().base instanceof TileEntity && button == MouseButton.left) {
                IC2.network.get(false).initiateClientTileEntityEvent((TileEntity)gui.getContainer().base, event);
            }
        });
    }
}
