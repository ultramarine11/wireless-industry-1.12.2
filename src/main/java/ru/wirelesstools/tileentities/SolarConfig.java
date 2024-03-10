package ru.wirelesstools.tileentities;

public final class SolarConfig {
    public final int dayPower;
    public final int nightPower;
    public final int tier;
    public final int wirelesstransferlimit;
    public final double maxStorage;
    
    public SolarConfig(int dayPower, int nightPower, double maxstorage, int tier, int limit) {
        this.dayPower = dayPower;
        this.nightPower = nightPower;
        this.tier = tier;
        this.wirelesstransferlimit = limit;
        this.maxStorage = maxstorage;
    }
}
