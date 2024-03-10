package ru.wirelesstools.gui.elements;

import com.google.common.base.Supplier;
import ic2.core.GuiIC2;
import ic2.core.gui.Area;

import java.util.List;

public class AreaCustomWI extends Area {
    private Supplier<List<String>> tooltipProvider;
    
    public AreaCustomWI(GuiIC2<?> gui, int x, int y, int width, int height) {
        super(gui, x, y, width, height);
    }
    
    public AreaCustomWI withListTooltip(Supplier<List<String>> tooltipProvider) {
        this.tooltipProvider = tooltipProvider;
        return this;
    }
    
    protected List<String> getToolTip() {
        List<String> ret = super.getToolTip();
        if(this.tooltipProvider != null) {
            ret.addAll(this.tooltipProvider.get());
        }
        return ret;
    }
    
}
