package ru.wirelesstools.utils;

import net.minecraft.entity.player.EntityPlayer;

public class ExperienceUtil {
    
    public static int getPlayerXP(EntityPlayer player) {
        return (int)((float)ExperienceUtil.getExperienceForLevel(player.experienceLevel) + player.experience * (float)player.xpBarCap());
    }

    /*@Deprecated
    public static void addPlayerXP(EntityPlayer player, int amount) {
        int experience;
        player.experienceTotal = experience = ExperienceUtil.getPlayerXP(player) + amount;
        player.experienceLevel = ExperienceUtil.getLevelForExperience(experience);
        int expForLevel = ExperienceUtil.getExperienceForLevel2(player.experienceLevel);
        player.experience = (float) (experience - expForLevel) / (float) player.xpBarCap();
    }*/
    
    public static void consumeXPFromPlayer(EntityPlayer player, int amount) {
        ExperienceUtil.addXPToPlayer(player, -amount);
    }
    
    public static void addXPToPlayer(EntityPlayer player, int amount) {
        int experience = ExperienceUtil.getPlayerXP(player) + amount;
        player.experienceTotal = experience;
        player.experienceLevel = ExperienceUtil.getLevelForExperience(experience);
        player.experience = (float)(experience - ExperienceUtil.getExperienceForLevel(player.experienceLevel))
                / (float)player.xpBarCap();
    }
    
    public static int getExperienceForLevel(int level) {
        if(level == 0)
            return 0;
        else if(level <= 15)
            return ExperienceUtil.sum(level, 7, 2);
        else
            return level <= 30 ? 315 + sum(level - 15, 37, 5) : 1395 + sum(level - 30, 112, 9);
    }
    
    public static int getXpToNextLevel(int level) {
        int levelXP = ExperienceUtil.getLevelForExperience(level);
        int nextXP = ExperienceUtil.getExperienceForLevel(level + 1);
        return nextXP - levelXP;
    }
    
    public static int getLevelForExperience(int targetXp) {
        int level = 0;
        while(true) {
            int xpToNextLevel = xpBarCap(level);
            if(targetXp < xpToNextLevel) {
                return level;
            }
            
            ++level;
            targetXp -= xpToNextLevel;
        }
    }
    
    public static int xpBarCap(int level) {
        if(level >= 30)
            return 112 + (level - 30) * 9;
        else
            return level >= 15 ? 37 + (level - 15) * 5 : 7 + level * 2;
    }
    
    private static int sum(int level, int a0, int d) {
        return level * (2 * a0 + (level - 1) * d) / 2;
    }
    
}
