package ru.wirelesstools.tileentities.othertes;

import ic2.core.ContainerBase;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ru.wirelesstools.config.ConfigWI;
import ru.wirelesstools.container.ContainerSolarWirelessMachinesCharger;
import ru.wirelesstools.gui.GUISolarWirelessMachinesCharger;
import ru.wirelesstools.tileentities.GenerationState;

public class TileEntitySolarMachinesCharger extends TileEntityMachinesChargerBase {
    
    protected boolean canRain;
    protected boolean hasSky;
    protected GenerationState activeState = GenerationState.NONE;
    protected final double dayPower;
    protected final double nightPower;
    
    public TileEntitySolarMachinesCharger() {
        super(ConfigWI.maxStorageMachinesCharger, ConfigWI.machinesChargerTier);
        this.dayPower = ConfigWI.dayPowerSolarMachinesCharger;
        this.nightPower = ConfigWI.nightPowerSolarMachinesCharger;
    }
    
    @SideOnly(Side.CLIENT)
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GUISolarWirelessMachinesCharger(new ContainerSolarWirelessMachinesCharger(player, this));
    }
    
    @Override
    public ContainerBase<TileEntityMachinesChargerBase> getGuiContainer(EntityPlayer player) {
        return new ContainerSolarWirelessMachinesCharger(player, this);
    }
    
    public GenerationState getGenState() {
        return this.activeState;
    }
    
    public double getDayPower() {
        return this.dayPower;
    }
    
    public double getNightPower() {
        return this.nightPower;
    }
    
    protected void onLoaded() {
        super.onLoaded();
        if(!this.world.isRemote) {
            this.canRain = (this.world.getBiome(this.pos).canRain() || this.world.getBiome(this.pos).getRainfall() > 0.0F);
            this.hasSky = !this.world.provider.isNether();
        }
    }
    
    protected void checkTheSky() {
        if(this.hasSky && this.world.canBlockSeeSky(this.pos.up())) {
            if(this.world.isDaytime() && (!this.canRain || (!this.world.isRaining() && !this.world.isThundering())))
                this.activeState = GenerationState.DAY;
            else
                this.activeState = GenerationState.NIGHT;
        }
        else
            this.activeState = GenerationState.NONE;
    }
    
    protected void updateEntityServer() {
        if(this.world.getTotalWorldTime() % 5 == 0)
            this.checkTheSky();
        switch(this.activeState) {
            case DAY:
                this.energy.addEnergy(this.dayPower);
                break;
            case NIGHT:
                this.energy.addEnergy(this.nightPower);
                break;
        }
        if(this.isOn)
            super.updateEntityServer();
    }
    
}
