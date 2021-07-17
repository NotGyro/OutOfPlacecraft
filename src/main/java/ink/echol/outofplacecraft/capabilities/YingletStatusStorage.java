package ink.echol.outofplacecraft.capabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class YingletStatusStorage implements Capability.IStorage<IYingletStatus> {
    @Nullable
    @Override
    public INBT writeNBT(Capability<IYingletStatus> capability, IYingletStatus instance, Direction side) {
        CompoundNBT NBT = new CompoundNBT();
        NBT.putBoolean("IsYinglet",instance.isYinglet());
        return NBT;
    }

    @Override
    public void readNBT(Capability<IYingletStatus> capability, IYingletStatus instance, Direction side, INBT nbt) {
        CompoundNBT compound = (CompoundNBT) nbt;
        instance.setIsYinglet(compound.getBoolean("IsYinglet"));
    }
}
