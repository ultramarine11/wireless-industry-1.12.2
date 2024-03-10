package ru.wirelesstools.api;

import com.mojang.authlib.GameProfile;

public interface IWirelessStorage {

	double getMaxCapacityOfStorage();

	double getCurrentEnergyInStorage();

	double addEnergy(double amount);

	int getChannel();

	GameProfile getOwner();
}
