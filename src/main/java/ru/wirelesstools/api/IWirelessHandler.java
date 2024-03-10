package ru.wirelesstools.api;

public interface IWirelessHandler {

	/**
	 * This method is used to transmit EU wirelessly from solar panel to storage.
	 *
	 */
	void transferEnergyWirelessly(IWirelessPanel sender, IWirelessStorage receiver);

	boolean isFreeEnergyInStorage(IWirelessStorage tile);

	double getFreeEnergyInStorage(IWirelessStorage tile);

	double getMinimumExtractedEnergy(IWirelessPanel sender);

}
