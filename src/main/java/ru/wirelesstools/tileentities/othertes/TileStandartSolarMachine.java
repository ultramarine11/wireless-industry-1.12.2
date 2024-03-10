package ru.wirelesstools.tileentities.othertes;

import com.google.common.collect.Lists;
import ic2.api.network.INetworkTileEntityEventListener;
import ic2.api.recipe.MachineRecipeResult;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.audio.AudioSource;
import ic2.core.audio.FutureSound;
import ic2.core.audio.PositionSpec;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.invslot.InvSlotProcessable;
import ic2.core.block.machine.tileentity.TileEntityElectricMachine;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import ru.wirelesstools.tileentities.GenerationState;

import java.util.List;

public abstract class TileStandartSolarMachine<RI, RO, I> extends TileEntityElectricMachine implements IHasGui, INetworkTileEntityEventListener {
    
    protected short progress;
    public final int defaultEnergyConsume;
    public final int defaultOperationLength;
    public final int defaultTier;
    public final int defaultEnergyStorage;
    public int energyConsume;
    public int operationLength;
    public AudioSource audioSource;
    protected static final int EventStart = 0;
    protected static final int EventInterrupt = 1;
    protected static final int EventFinish = 2;
    protected static final int EventStop = 3;
    public InvSlotProcessable<RI, RO, I> inputSlot;
    public final InvSlotOutput outputSlot;
    protected double guiProgress;
    protected GenerationState activeMode = GenerationState.NONE;
    protected final double dayPower;
    protected final double nightPower;
    protected boolean canRain;
    protected boolean hasSky;
    
    protected FutureSound startingSound;
    protected String finishingSound;
    
    public TileStandartSolarMachine(int energyPerTick, int length, int outputSlots, double dayGen, double nightGen) {
        super(energyPerTick * length, 1);
        this.progress = 0;
        this.defaultEnergyConsume = this.energyConsume = energyPerTick;
        this.defaultOperationLength = this.operationLength = length;
        this.defaultTier = 1;
        this.defaultEnergyStorage = energyPerTick * length;
        this.outputSlot = new InvSlotOutput(this, "output", outputSlots);
        this.dayPower = dayGen;
        this.nightPower = nightGen;
    }
    
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.progress = nbttagcompound.getShort("progress");
    }
    
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setShort("progress", this.progress);
        return nbt;
    }
    
    protected void onLoaded() {
        super.onLoaded();
        if(!this.world.isRemote) {
            this.canRain = (this.world.getBiome(this.pos).canRain() || this.world.getBiome(this.pos).getRainfall() > 0.0F);
            this.hasSky = !this.world.provider.isNether();
        }
    }
    
    protected void onUnloaded() {
        super.onUnloaded();
        if(IC2.platform.isRendering() && this.audioSource != null) {
            IC2.audioManager.removeSources(this);
            this.audioSource = null;
        }
    }
    
    public double getProgress() {
        return this.guiProgress;
    }
    
    public double getDayPower() {
        return this.dayPower;
    }
    
    public double getNightPower() {
        return this.nightPower;
    }
    
    public double getEnergyFillRatio() {
        return this.energy.getEnergy() / this.energy.getCapacity();
    }
    
    protected void checkTheSky() {
        if(this.hasSky && this.world.canBlockSeeSky(this.pos.up())) {
            if(this.world.isDaytime() && (!this.canRain || (!this.world.isRaining() && !this.world.isThundering())))
                this.activeMode = GenerationState.DAY;
            else
                this.activeMode = GenerationState.NIGHT;
        }
        else
            this.activeMode = GenerationState.NONE;
    }
    
    public GenerationState getGenState() {
        return this.activeMode;
    }
    
    public double getEnergyStored() {
        return this.energy.getEnergy();
    }
    
    public double getMaxEnergy() {
        return this.energy.getCapacity();
    }
    
    protected void updateEntityServer() {
        super.updateEntityServer();
        if(this.world.getTotalWorldTime() % 5 == 0)
            this.checkTheSky();
        switch(this.activeMode) {
            case DAY:
                this.energy.addEnergy(this.dayPower);
                break;
            case NIGHT:
                this.energy.addEnergy(this.nightPower);
                break;
        }
        boolean needsInvUpdate = false;
        MachineRecipeResult<RI, RO, I> output = this.getOutput();
        if(output != null && this.energy.useEnergy(this.energyConsume)) {
            this.setActive(true);
            if(this.progress == 0)
                IC2.network.get(true).initiateTileEntityEvent(this, 0, true);
            this.progress++;
            if(this.progress >= this.operationLength) {
                this.operateOnce(output);
                this.progress = 0;
                needsInvUpdate = true;
                IC2.network.get(true).initiateTileEntityEvent(this, 2, true);
            }
        }
        else {
            if(this.getActive())
                IC2.network.get(true).initiateTileEntityEvent(this, this.progress != 0 ? 1 : 3, true);
            if(output == null)
                this.progress = 0;
            this.setActive(false);
        }
        
        this.guiProgress = (float)this.progress / (float)this.operationLength;
        if(needsInvUpdate)
            this.markDirty();
    }
    
    private void operateOnce(MachineRecipeResult<RI, RO, I> result) {
        //List<ItemStack> processResult = this.getOutput(result.getOutput());
        
        List<ItemStack> processResult = (List<ItemStack>)Lists.newArrayList(result.getOutput());
        this.inputSlot.consume(result);
        this.outputSlot.add(processResult);
    }
    
    protected List<ItemStack> getOutput(RO output) {
        //Lists.newArrayList(output);
        return (List<ItemStack>)Lists.newArrayList(output);
    }
    
    protected MachineRecipeResult<RI, RO, I> getOutput() {
        if(this.inputSlot.isEmpty())
            return null;
        else {
            MachineRecipeResult<RI, RO, I> result = this.inputSlot.process();
            if(result == null)
                return null;
            else
                return this.outputSlot.canAdd(this.getOutput(result.getOutput())) ? result : null;
        }
    }
    
    public abstract String getStartSoundFile();
    
    public abstract String getLoopSoundFile();
    
    public abstract String getInterruptSoundFile();
    
    public void onNetworkEvent(int event) {
        if(this.audioSource == null && this.getLoopSoundFile() != null)
            this.audioSource = IC2.audioManager.createSource(this, PositionSpec.Center, this.getLoopSoundFile(), true, false, IC2.audioManager.getDefaultVolume());
        
        switch(event) {
            case 0:
                if(this.startingSound == null) {
                    if(this.finishingSound != null) {
                        IC2.audioManager.removeSource(this.finishingSound);
                        this.finishingSound = null;
                    }
                    
                    if(this.audioSource != null)
                        IC2.audioManager.chainSource(
                                IC2.audioManager.playOnce(this, PositionSpec.Center, this.getStartSoundFile(), false, IC2.audioManager.getDefaultVolume() - 0.2F),
                                this.startingSound = new FutureSound(this.audioSource::play));
                }
                break;
            case 1:
            case 3:
                if(this.audioSource != null) {
                    this.audioSource.stop();
                    if(this.startingSound != null) {
                        if(!this.startingSound.isComplete()) {
                            this.startingSound.cancel();
                        }
                        
                        this.startingSound = null;
                    }
                    
                    this.finishingSound = IC2.audioManager.playOnce(this, PositionSpec.Center, this.getInterruptSoundFile(), false, IC2.audioManager.getDefaultVolume() - 0.2F);
                }
            case 2:
        }
    }
    
}
