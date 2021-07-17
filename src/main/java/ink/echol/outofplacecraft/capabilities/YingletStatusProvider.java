package ink.echol.outofplacecraft.capabilities;

import ink.echol.outofplacecraft.OutOfPlacecraftMod;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class YingletStatusProvider implements ICapabilitySerializable<INBT> {

    public static final ResourceLocation ident = new ResourceLocation(OutOfPlacecraftMod.MODID,"yinglet_status");

    @CapabilityInject(IYingletStatus.class)
    public static Capability<IYingletStatus> yingletStatus = null;

    private IYingletStatus instance = yingletStatus.getDefaultInstance();
    private final LazyOptional<IYingletStatus> holder = LazyOptional.of(() -> instance);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == instance) {
            return holder.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public INBT serializeNBT() {
        return yingletStatus.getStorage().writeNBT(yingletStatus, instance, null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        yingletStatus.getStorage().readNBT(yingletStatus, instance, null, nbt);
    }
}
