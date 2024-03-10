package ru.wirelesstools.wirelesshandler;

import ru.wirelesstools.api.IWirelessPanel;
import ru.wirelesstools.api.IWirelessStorage;
import ru.wirelesstools.api.IWirelessHandler;

public class WirelessHandler implements IWirelessHandler {
    
    public boolean isFreeEnergyInStorage(IWirelessStorage tile) {
        return this.getFreeEnergyInStorage(tile) > 0.0;
    }
    
    public double getFreeEnergyInStorage(IWirelessStorage tile) {
        return (tile.getMaxCapacityOfStorage() - tile.getCurrentEnergyInStorage());
    }
    
    @Override
    public void transferEnergyWirelessly(IWirelessPanel sender, IWirelessStorage receiver) {
        if(this.isFreeEnergyInStorage(receiver)) {
            sender.extractEnergy(receiver.addEnergy(this.getMinimumExtractedEnergy(sender)));
        }
    }
    
    public double getMinimumExtractedEnergy(IWirelessPanel sender) {
        return Math.min(sender.getWirelessTransferLimit(), sender.getCurrentEnergyInPanel());
    }
    
}
