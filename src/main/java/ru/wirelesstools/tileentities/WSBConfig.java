package ru.wirelesstools.tileentities;

public class WSBConfig {
    
    public final int tier;
    public final double maxstorage;
    
    public WSBConfig(double maxstorage, int tier) {
        this.tier = tier;
        this.maxstorage = maxstorage;
    }
}
