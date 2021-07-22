package ink.echol.outofplacecraft.capabilities;

import ink.echol.outofplacecraft.OutOfPlacecraftMod;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class YingletStatus {

    public static final ResourceLocation ident = new ResourceLocation(OutOfPlacecraftMod.MODID,"yinglet_status");

    public static class Implementation implements IYingletStatus {
        public Implementation() {}
        public Implementation(boolean value) {
            ying = value;
        }

        private boolean ying = false;
        @Override
        public boolean isYinglet() {
            return ying;
        }

        @Override
        public void setIsYinglet(boolean value) {
            ying = value; //Dare you enter my magical realm?
        }

    }

    public static class YingletStatusStorage implements Capability.IStorage<IYingletStatus> {
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

    public static class Provider implements ICapabilitySerializable<INBT>
    {
        //private IYingletStatus instance = CapabilityRegistry.YINGLET_CAPABILITY.getDefaultInstance();
        //;
        private IYingletStatus yingletStatus;
        private Capability.IStorage<IYingletStatus> storage;

        public Provider()
        {
            this.yingletStatus = new Implementation(false);
            this.storage = CapabilityRegistry.YINGLET_CAPABILITY.getStorage();
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if(CapabilityRegistry.YINGLET_CAPABILITY.equals(cap)) {
                return LazyOptional.of(() -> yingletStatus).cast();
            }
            return LazyOptional.empty();
        }

        @Override
        public INBT serializeNBT() {
            return this.storage.writeNBT(CapabilityRegistry.YINGLET_CAPABILITY, this.yingletStatus, null);
        }

        @Override
        public void deserializeNBT(INBT nbt) {
            this.storage.readNBT(CapabilityRegistry.YINGLET_CAPABILITY, this.yingletStatus, null, nbt);
        }
    }
    public static class Storage implements Capability.IStorage<IYingletStatus> {
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
}
