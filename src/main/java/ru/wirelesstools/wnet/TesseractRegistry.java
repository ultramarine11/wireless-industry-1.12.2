package ru.wirelesstools.wnet;

import com.mojang.authlib.GameProfile;
import ru.wirelesstools.tileentities.wireless.TileTesseract;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TesseractRegistry {
    
    private static final TesseractRegistry instance = new TesseractRegistry();
    private static final Map<GameProfile, Set<TileTesseract>> tesseractTilesMap = new HashMap<>();
    
    private TesseractRegistry() {
    }
    
    public static TesseractRegistry getInstance() {
        return instance;
    }
    
    public Map<GameProfile, Set<TileTesseract>> getTesseractTilesMap() {
        return tesseractTilesMap;
    }
    
}
