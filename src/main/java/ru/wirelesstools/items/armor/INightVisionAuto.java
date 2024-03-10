package ru.wirelesstools.items.armor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

public interface INightVisionAuto {
    
    default int nightVisionAuto(EntityPlayer player, boolean showParticles) {
        int lightValue = player.world.getLightFromNeighbors(player.getPosition());
        if(lightValue > 8)
            player.removePotionEffect(MobEffects.NIGHT_VISION);
        else
            player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 302, 0, true, showParticles));
        return lightValue;
    }
    
}
