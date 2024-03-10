package ru.wirelesstools.api;

public interface IWirelessPanel {

	double getCurrentEnergyInPanel();

	int getWirelessTransferLimit();

	void extractEnergy(double amount);

}
