package ru.wirelesstools.gui.elements;

import ic2.core.GuiIC2;
import ic2.core.gui.CustomButton;
import ic2.core.gui.IClickHandler;

public class CustomWIButtonBT extends CustomButton {
    
    public CustomWIButtonBT(GuiIC2<?> gui, int x, int y, int width, int height, IClickHandler handler) {
        super(gui, x, y, width, height, handler);
    }
    
    protected int getTextColor(int mouseX, int mouseY) {
        return 4210752;
    }
}
