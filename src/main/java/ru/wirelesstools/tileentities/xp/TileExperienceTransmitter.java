package ru.wirelesstools.tileentities.xp;

import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.core.ContainerBase;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.comp.Energy;
import ic2.core.init.Localization;
import ic2.core.ref.TeBlock;
import ic2.core.util.EntityIC2FX;
import ic2.core.util.StackUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.wirelesstools.config.ConfigWI;
import ru.wirelesstools.container.ContainerXPTransmitter;
import ru.wirelesstools.gui.GuiXPTransmitter;
import ru.wirelesstools.tileentities.GenerationState;
import ru.wirelesstools.utils.ExperienceUtil;

import java.util.*;

public class TileExperienceTransmitter extends TileEntityInventory implements IHasGui, INetworkClientTileEntityEventListener {
    protected final Energy energy;
    private boolean isOn;
    private boolean sendingXPMode;
    private int amountXPTransmit;
    private int storedXP;
    private final int xpLimit;
    private final List<EntityPlayer> playersStandingOnTop;
    private final Set<String> playerNamesSet;
    
    private boolean isSolar;
    protected GenerationState activeMode = GenerationState.NONE;
    protected boolean canRain;
    protected boolean hasSky;
    
    protected TileExperienceTransmitter(boolean solarMode) {
        this();
        this.isSolar = solarMode;
    }
    
    public TileExperienceTransmitter() {
        this.energy = this.addComponent(Energy.asBasicSink(this, ConfigWI.energyForXP, ConfigWI.xpTransmitterTier));
        this.sendingXPMode = true;
        this.isOn = true;
        this.amountXPTransmit = 1;
        this.storedXP = 0;
        this.xpLimit = ConfigWI.xpLimit;
        this.playersStandingOnTop = new ArrayList<>();
        this.playerNamesSet = new HashSet<>();
        this.isSolar = false;
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, List<String> info, ITooltipFlag flag) {
        info.add(Localization.translate(this.isSolar ? "wi.info.solar.xp.transmitter" : "wi.info.xp.transmitter.generation"));
        super.addInformation(stack, info, flag);
    }
    
    protected ItemStack adjustDrop(ItemStack drop, boolean wrench) {
        drop = super.adjustDrop(drop, wrench);
        if(wrench || this.teBlock.getDefaultDrop() == TeBlock.DefaultDrop.Self) {
            NBTTagCompound nbt = StackUtil.getOrCreateNbtData(drop);
            nbt.setInteger("storedXP", this.storedXP);
            nbt.setInteger("amountXPTransmit", this.amountXPTransmit);
            nbt.setBoolean("sendingXPMode", this.sendingXPMode);
            nbt.setBoolean("isOn", this.isOn);
            nbt.setDouble("energy", this.energy.getEnergy());
        }
        return drop;
    }
    
    public void onPlaced(ItemStack stack, EntityLivingBase placer, EnumFacing facing) {
        super.onPlaced(stack, placer, facing);
        if(!this.world.isRemote) {
            NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
            if(nbt.hasKey("storedXP"))
                this.storedXP = nbt.getInteger("storedXP");
            if(nbt.hasKey("amountXPTransmit"))
                this.amountXPTransmit = nbt.getInteger("amountXPTransmit");
            if(nbt.hasKey("sendingXPMode"))
                this.sendingXPMode = nbt.getBoolean("sendingXPMode");
            if(nbt.hasKey("isOn"))
                this.isOn = nbt.getBoolean("isOn");
            if(nbt.hasKey("energy"))
                this.energy.addEnergy(nbt.getDouble("energy"));
        }
    }
    
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.isOn = nbt.getBoolean("isOnMode");
        this.sendingXPMode = nbt.getBoolean("sendingXPMode");
        this.amountXPTransmit = nbt.getInteger("amountXPSentToPlayer");
        this.storedXP = nbt.getInteger("storedXP");
    }
    
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("amountXPSentToPlayer", this.amountXPTransmit);
        nbt.setInteger("storedXP", this.storedXP);
        nbt.setBoolean("isOnMode", this.isOn);
        nbt.setBoolean("sendingXPMode", this.sendingXPMode);
        return nbt;
    }
    
    protected void updateEntityServer() {
        super.updateEntityServer();
        if(this.isSolar) {
            if(this.world.getTotalWorldTime() % 5 == 0)
                this.checkTheSkyExpTransmitter();
            this.attemptGenerationFromSolar();
            
            if(this.isOn) {
                this.getPlayersStandingOn();
                
                if(this.sendingXPMode)
                    this.transferXPToPlayers(this.playersStandingOnTop);
                else
                    this.consumeXPFromPlayers(this.playersStandingOnTop);
            }
            else {
                if(this.getActive())
                    this.setActive(false);
            }
        }
        else {
            if(this.isOn) {
                this.attemptGeneration();
                this.getPlayersStandingOn();
                
                if(this.sendingXPMode)
                    this.transferXPToPlayers(this.playersStandingOnTop);
                else
                    this.consumeXPFromPlayers(this.playersStandingOnTop);
            }
            else {
                if(this.getActive())
                    this.setActive(false);
            }
        }
    }
    
    protected void invertMode() {
        this.sendingXPMode = !this.sendingXPMode;
    }
    
    protected void toggleWork() {
        this.isOn = !this.isOn;
    }
    
    public boolean getIsSendingMode() {
        return this.sendingXPMode;
    }
    
    public boolean getIsOn() {
        return this.isOn;
    }
    
    public boolean isSolar() {
        return this.isSolar;
    }
    
    protected void changeAmountSentToPlayer(int amount) {
        this.amountXPTransmit = Math.max(this.amountXPTransmit + amount, 1);
    }
    
    public int getStoredXP() {
        return this.storedXP;
    }
    
    public GenerationState getGenState() {
        return this.activeMode;
    }
    
    public int getAmountXPTransmit() {
        return this.amountXPTransmit;
    }
    
    public int getXpLimit() {
        return this.xpLimit;
    }
    
    public Set<String> getPlayerNamesSet() {
        return this.playerNamesSet;
    }
    
    protected void transferXPToPlayers(List<EntityPlayer> playersStandingOn) {
        if(!playersStandingOn.isEmpty()) {
            this.setActive(true);
            if(this.world.getTotalWorldTime() % 2 == 0)
                for(EntityPlayer player : playersStandingOn) {
                    if(this.storedXP > 0) {
                        int realTransfer = Math.min(this.storedXP, this.amountXPTransmit);
                        this.storedXP -= realTransfer;
                        ExperienceUtil.addXPToPlayer(player, realTransfer);
                    }
                }
        }
        else
            this.setActive(false);
    }
    
    protected void consumeXPFromPlayers(List<EntityPlayer> playersStandingOn) {
        if(!playersStandingOn.isEmpty()) {
            this.setActive(true);
            for(EntityPlayer player : playersStandingOn) {
                int playerXP = ExperienceUtil.getPlayerXP(player);
                if(playerXP > 0) {
                    int maxAcceptedXP = Math.min(Math.min(playerXP, this.amountXPTransmit), (this.xpLimit - this.storedXP));
                    if(maxAcceptedXP > 0) {
                        ExperienceUtil.consumeXPFromPlayer(player, maxAcceptedXP);
                        this.storedXP += maxAcceptedXP;
                    }
                }
            }
        }
        else
            this.setActive(false);
    }
    
    @SideOnly(Side.CLIENT)
    protected void updateEntityClient() {
        super.updateEntityClient();
        World world = this.getWorld();
        Random rnd = world.rand;
        if(rnd.nextInt(8) != 0)
            return;
        if(this.getActive()) {
            ParticleManager effect = FMLClientHandler.instance().getClient().effectRenderer;
            float[] orangeParticles = {1.0F, 0.55F, 0F};
            float[] greenParticles = {0.5F, 0.72F, 0F};
            for(int particles = 20; particles > 0; particles--) {
                double x = (this.pos.getX() + 0.0F + rnd.nextFloat());
                double y = (this.pos.getY() + 0.9F + rnd.nextFloat());
                double z = (this.pos.getZ() + 0.0F + rnd.nextFloat());
                effect.addEffect(new EntityIC2FX(world, x, y, z, 45, new double[] {0.0D, 0.1D, 0.0D},
                        this.sendingXPMode ? greenParticles : orangeParticles));
            }
        }
    }
    
    protected void getPlayersStandingOn() {
        this.playerNamesSet.clear();
        this.playersStandingOnTop.clear();
        List<EntityPlayer> playersList = this.world.getEntitiesWithinAABB(EntityPlayer.class,
                new AxisAlignedBB(this.pos.up()).expand(0, 1, 0));
        if(!playersList.isEmpty()) {
            this.playersStandingOnTop.addAll(playersList);
            for(EntityPlayer player : playersList)
                this.playerNamesSet.add(player.getName());
        }
    }
    
    public int getPercentageGeneration() {
        return (int)(this.energy.getEnergy() / this.energy.getCapacity() * 100);
    }
    
    protected void attemptGeneration() {
        if(this.energy.getEnergy() >= this.energy.getCapacity()) {
            if(this.storedXP < this.xpLimit) {
                this.storedXP++;
                this.energy.useEnergy(this.energy.getCapacity());
            }
        }
    }
    
    protected void attemptGenerationFromSolar() {
        switch(this.activeMode) {
            case DAY:
                this.energy.addEnergy(ConfigWI.dayPowerSolarXPTransmitter);
                break;
            case NIGHT:
                this.energy.addEnergy(ConfigWI.nightPowerSolarXPTransmitter);
                break;
        }
        if(this.energy.getEnergy() >= this.energy.getCapacity()) {
            if(this.storedXP < this.xpLimit) {
                this.storedXP++;
                this.energy.useEnergy(this.energy.getCapacity());
            }
        }
    }
    
    protected void onLoaded() {
        super.onLoaded();
        if(!this.world.isRemote) {
            this.canRain = (this.world.getBiome(this.pos).canRain() || this.world.getBiome(this.pos).getRainfall() > 0.0F);
            this.hasSky = !this.world.provider.isNether();
        }
    }
    
    protected void checkTheSkyExpTransmitter() {
        if(this.hasSky && this.world.canBlockSeeSky(this.pos.up())) {
            if(this.world.isDaytime() && (!this.canRain || (!this.world.isRaining() && !this.world.isThundering())))
                this.activeMode = GenerationState.DAY;
            else
                this.activeMode = GenerationState.NIGHT;
        }
        else
            this.activeMode = GenerationState.NONE;
    }
    
    @Override
    public void onNetworkEvent(EntityPlayer player, int id) {
        switch(id) {
            case 1:
                this.changeAmountSentToPlayer(1);
                break;
            case 2:
                this.changeAmountSentToPlayer(-1);
                break;
            case 3:
                this.toggleWork();
                break;
            case 4:
                this.invertMode();
                break;
            case 5:
                this.changeAmountSentToPlayer(10);
                break;
            case 6:
                this.changeAmountSentToPlayer(-10);
                break;
            case 7:
                this.changeAmountSentToPlayer(100);
                break;
            case 8:
                this.changeAmountSentToPlayer(-100);
                break;
        }
    }
    
    @Override
    public ContainerBase<TileExperienceTransmitter> getGuiContainer(EntityPlayer player) {
        return new ContainerXPTransmitter(player, this);
    }
    
    @SideOnly(Side.CLIENT)
    public GuiScreen getGui(EntityPlayer entityPlayer, boolean isAdmin) {
        return new GuiXPTransmitter(new ContainerXPTransmitter(entityPlayer, this));
    }
    
    @Override
    public void onGuiClosed(EntityPlayer player) {
    }
}
