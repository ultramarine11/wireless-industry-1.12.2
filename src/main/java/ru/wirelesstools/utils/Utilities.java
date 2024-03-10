package ru.wirelesstools.utils;

import cofh.redstoneflux.api.IEnergyContainerItem;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.core.init.Localization;
import ic2.core.item.DamageHandler;
import ic2.core.util.StackUtil;
import ic2.core.util.Util;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.Range;
import ru.wirelesstools.tileentities.othertes.TileWirelessChargerPlayer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Utilities {
    
    static Range<Long> dayTime1 = Range.between(0L, 12541L); // 12541 is the last tick of day, the next one (12542) is night tick
    static Range<Long> dayTime2 = Range.between(23460L, 23999L); // 23460 is the first day tick after night
    static Range<Long> nightTime = Range.between(12542L, 23459L); // 23459 is the last tick of night, the next one (23460) is day tick
    
    public static final GameProfile DEFAULT_UNKNOWN_OWNER = new GameProfile(UUID.fromString("1ef1a6f0-87bc-4e78-0a0b-c6824eb787ea"), "[None]");
    
    public static void chargePlayers(boolean isPrivate,
                                     TileWirelessChargerPlayer tile, AxisAlignedBB aabb) {
        for(EntityPlayer player : tile.getWorld().getEntitiesWithinAABB(EntityPlayer.class, aabb)) {
            if(player != null) {
                if(isPrivate) {
                    if(!player.getGameProfile().equals(tile.getOwner()))
                        continue;
                }
                Utilities.chargeInventory(player, tile);
            }
        }
    }
    
    public static int getInteger(ItemStack stack, String tag) {
        NBTTagCompound compound = stack.getTagCompound();
        return (compound != null && compound.hasKey(tag)) ? compound.getInteger(tag) : 0;
    }
    
    public static List<ItemStack> getAllPlayerInventory(EntityPlayer player) {
        LinkedList<ItemStack> fullInv = new LinkedList<>();
        fullInv.addAll(player.inventory.mainInventory);
        fullInv.addAll(player.inventory.armorInventory);
        fullInv.addAll(player.inventory.offHandInventory);
        return fullInv;
    }
    
    public static void chargeInventory(EntityPlayer player, TileWirelessChargerPlayer tile) {
        for(ItemStack stack : Utilities.getAllPlayerInventory(player)) {
            Item item = stack.getItem();
            if(item instanceof IElectricItem) {
                double tileEnergyEU = tile.getStorage();
                if(tileEnergyEU > 0.0) {
                    if(Utilities.isNotFullyCharged(stack))
                        tile.useEnergyAmount(chargeElectricItem(stack, tileEnergyEU,
                                Integer.MAX_VALUE, false, false));
                }
            }
            else if(item instanceof IEnergyContainerItem) {
                int tileEnergyRF = tile.getEnergyRF();
                if(tileEnergyRF > 0) {
                    IEnergyContainerItem rfItem = (IEnergyContainerItem)item;
                    int requiredEnergy = rfItem.receiveEnergy(stack, Integer.MAX_VALUE, true);
                    if(requiredEnergy > 0)
                        tile.useEnergyRF(rfItem.receiveEnergy(stack, Math.min(tileEnergyRF, requiredEnergy), false));
                }
            }
        }
    }
    
    public static String formatNumber(long value) {
        if(value < 1000L) {
            return String.valueOf(value);
        }
        else if(value < 1000000L) {
            return (double)Math.round((float)value) / 1000.0D + "K";
        }
        else if(value < 1000000000L) {
            return (double)Math.round((float)(value / 1000L)) / 1000.0D + "M";
        }
        else if(value < 1000000000000L) {
            return (double)Math.round((float)(value / 1000000L)) / 1000.0D + "G";
        }
        else if(value < 1000000000000000L) {
            return (double)Math.round((float)(value / 1000000000L)) / 1000.0D + "T";
        }
        else if(value < 1000000000000000000L) {
            return (double)Math.round((float)(value / 1000000000000L)) / 1000.0D + "P";
        }
        else {
            return (double)Math.round((float)(value / 1000000000000000L)) / 1000.0D + "E";
        }
    }
    
    public static NBTTagCompound checkRFItemNBT(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if(nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
            nbt.setInteger("Energy", 0);
        }
        return nbt;
    }
    
    public static String tooltipChargeRF(ItemStack stack) {
        return GUIUtility.formatNumber(getCharge(stack) * 4)
                + " / " + GUIUtility.formatNumber(ElectricItem.manager.getMaxCharge(stack) * 4) + " RF";
    }
    
    public static double needsEnergyElectricItem(ItemStack stack, double amount, boolean ignoreTransferLimit) {
        Item item = stack.getItem();
        if(item instanceof IElectricItem) {
            IElectricItem eI = (IElectricItem)item;
            if(amount > 0.0 && StackUtil.getSize(stack) <= 1) {
                if(!ignoreTransferLimit && amount > eI.getTransferLimit(stack))
                    amount = eI.getTransferLimit(stack);
                return Math.min(amount, eI.getMaxCharge(stack) - StackUtil.getOrCreateNbtData(stack).getDouble("charge"));
            }
            else
                return 0.0;
        }
        else
            return 0.0;
    }
    
    public static boolean isNotFullyCharged(ItemStack stack) {
        Item item = stack.getItem();
        if(item instanceof IElectricItem) {
            if(StackUtil.getSize(stack) <= 1)
                return (((IElectricItem)item).getMaxCharge(stack) - StackUtil.getOrCreateNbtData(stack).getDouble("charge")) > 0.0;
        }
        return false;
    }
    
    public static double simpleChargeElectricItemITL(ItemStack stack, double amount) {
        return chargeElectricItem(stack, amount, Integer.MAX_VALUE, true, false);
    }
    
    public static double simpleChargeElectricItem(ItemStack stack, double amount) {
        return chargeElectricItem(stack, amount, Integer.MAX_VALUE, false, false);
    }
    
    public static double chargeElectricItem(ItemStack stack, double amount, int tier, boolean ignoreTransferLimit, boolean simulate) {
        Item item = stack.getItem();
        if(item instanceof IElectricItem) {
            IElectricItem eI = (IElectricItem)item;
            if(amount > 0.0 && StackUtil.getSize(stack) <= 1 && eI.getTier(stack) <= tier) {
                if(!ignoreTransferLimit && amount > eI.getTransferLimit(stack))
                    amount = eI.getTransferLimit(stack);
                NBTTagCompound tNBT = StackUtil.getOrCreateNbtData(stack);
                double newCharge = tNBT.getDouble("charge");
                amount = Math.min(amount, eI.getMaxCharge(stack) - newCharge);
                if(!simulate) {
                    newCharge += amount;
                    if(newCharge > 0.0)
                        tNBT.setDouble("charge", newCharge);
                    else {
                        tNBT.removeTag("charge");
                        if(tNBT.hasNoTags()) {
                            stack.setTagCompound(null);
                        }
                    }
                    DamageHandler.setDamage(stack, mapChargeLevelToDamage(newCharge, eI.getMaxCharge(stack),
                            DamageHandler.getMaxDamage(stack)), true);
                }
                return amount;
            }
            else
                return 0.0;
        }
        else
            return 0.0;
    }
    
    public static double getCharge(ItemStack stack) {
        if(stack.getItem() instanceof IElectricItem) {
            if(StackUtil.getSize(stack) <= 1)
                return StackUtil.getOrCreateNbtData(stack).getDouble("charge");
            else
                return 0.0;
        }
        else
            return 0.0;
    }
    
    private static int mapChargeLevelToDamage(double charge, double maxCharge, int maxDamage) {
        if(maxDamage < 2)
            return 0;
        else {
            --maxDamage;
            return maxDamage - (int)Util.map(charge, maxCharge, maxDamage);
        }
    }
    
    public static List<GameProfile> readListGameProfilesFromNBT(NBTTagCompound compound) {
        List<GameProfile> listProfiles = new ArrayList<>();
        NBTTagList nbttaglist1 = compound.getTagList("GameProfilesList", 10);
        for(int k = 0, c = nbttaglist1.tagCount(); k < c; k++) {
            NBTTagCompound tagcompound1 = nbttaglist1.getCompoundTagAt(k);
            String s = null;
            String s1 = null;
            if(tagcompound1.hasKey("Name", 8)) {
                s = tagcompound1.getString("Name");
            }
            if(tagcompound1.hasKey("Id", 8)) {
                s1 = tagcompound1.getString("Id");
            }
            try {
                UUID uuid;
                try {
                    uuid = UUID.fromString(s1);
                }
                catch(Throwable th) {
                    uuid = null;
                }
                GameProfile gameprofile = new GameProfile(uuid, s);
                if(tagcompound1.hasKey("Properties", 10)) {
                    NBTTagCompound nbttagcompound = tagcompound1.getCompoundTag("Properties");
                    for(String s2 : nbttagcompound.getKeySet()) {
                        NBTTagList nbttaglist = nbttagcompound.getTagList(s2, 10);
                        for(int i = 0; i < nbttaglist.tagCount(); ++i) {
                            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
                            String s3 = nbttagcompound1.getString("Value");
                            if(nbttagcompound1.hasKey("Signature", 8)) {
                                gameprofile.getProperties().put(s2, new Property(s2, s3, nbttagcompound1.getString("Signature")));
                            }
                            else {
                                gameprofile.getProperties().put(s2, new Property(s2, s3));
                            }
                        }
                    }
                }
                listProfiles.add(gameprofile);
            }
            catch(Throwable throwable) {
                return new ArrayList<>();
            }
        }
        return listProfiles;
    }
    
    public static NBTTagCompound writeListGameProfilesToNBT(NBTTagCompound tagCompound, List<GameProfile> listProfiles) {
        NBTTagList tagList = new NBTTagList();
        for(GameProfile profile : listProfiles) {
            NBTTagCompound tagCompound2 = new NBTTagCompound();
            if(!StringUtils.isNullOrEmpty(profile.getName())) {
                tagCompound2.setString("Name", profile.getName());
            }
            if(profile.getId() != null) {
                tagCompound2.setString("Id", profile.getId().toString());
            }
            if(!profile.getProperties().isEmpty()) {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                for(String s : profile.getProperties().keySet()) {
                    NBTTagList nbttaglist = new NBTTagList();
                    for(Property property : profile.getProperties().get(s)) {
                        NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                        nbttagcompound1.setString("Value", property.getValue());
                        if(property.hasSignature()) {
                            nbttagcompound1.setString("Signature", property.getSignature());
                        }
                        nbttaglist.appendTag(nbttagcompound1);
                    }
                    nbttagcompound.setTag(s, nbttaglist);
                }
                tagCompound2.setTag("Properties", nbttagcompound);
            }
            tagList.appendTag(tagCompound2);
        }
        tagCompound.setTag("GameProfilesList", tagList);
        return tagCompound;
    }
    
    public static Iterable<BlockPos.MutableBlockPos> getMutablePosesInRadius(BlockPos center, int radius) {
        return BlockPos.getAllInBoxMutable(center.getX() - radius, center.getY() - radius, center.getZ() - radius,
                center.getX() + radius, center.getY() + radius, center.getZ() + radius);
    }
    
    public static NBTTagCompound getNBTOfItemInHand(EntityPlayer player, EnumHand hand) {
        return StackUtil.getOrCreateNbtData(player.getHeldItem(hand));
    }
    
    public static boolean canSendEnergy(int channel_1, int channel_2, GameProfile id1, GameProfile id2) {
        return (channel_1 == channel_2) && ((id1 != null) && (id1.equals(id2)));
    }
    
    public static double percentageNumber(int number, int percent) {
        return number * (1.0 + percent / 100.0);
    }
    
    public static <T extends TileEntity> String calculateRemainTime(T tile) {
        long time = tile.getWorld().getWorldTime() % 24000L;
        long remainTicks = 0;
        boolean night = false;
        // 1) rT = 12541 - s
        // 2) rT = 23999 - s + 12541
        // 3) rT = 23459 - s
        if(dayTime1.contains(time)) {
            remainTicks = dayTime1.getMaximum() - time;
        }
        else if(dayTime2.contains(time)) {
            remainTicks = dayTime2.getMaximum() - time + dayTime1.getMaximum();
        }
        else if(nightTime.contains(time)) {
            remainTicks = nightTime.getMaximum() - time;
            night = true;
        }
        long secs = Math.floorDiv(remainTicks, 20L);
        long remMinutes = TimeUnit.SECONDS.toMinutes(secs);
        long remSeconds = secs - remMinutes * 60L;
        if(night) {
            return remMinutes + " " + Localization.translate("gui.WP.mins.remain") + " " + remSeconds + " "
                    + Localization.translate("gui.WP.secs.remain")
                    + " " + Localization.translate("gui.WP.time.remain") + "\n" + Localization.translate("gui.WP.until.day");
        }
        else {
            return remMinutes + " " + Localization.translate("gui.WP.mins.remain") + " " + remSeconds + " "
                    + Localization.translate("gui.WP.secs.remain")
                    + " " + Localization.translate("gui.WP.time.remain") + "\n" + Localization.translate("gui.WP.until.night");
        }
    }
    
}
