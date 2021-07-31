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

public class SpeciesCapability {

    public static final ResourceLocation ident = new ResourceLocation(OutOfPlacecraftMod.MODID,"species");

    public static final int HUMAN_ID = ("human").hashCode();
    public static final int YINGLET_ID = ("outofplacecraft:yinglet").hashCode();

    public static class Implementation implements ISpecies {
        public Implementation() {}
        public Implementation(int value) {
            species = value;
        }

        private int species = HUMAN_ID;

        @Override
        public int getSpecies() {
            return this.species;
        }

        @Override
        public void setSpecies(int value) {
            this.species = value;
        }

        @Override
        public boolean isYinglet() {
            return this.species == YINGLET_ID;
        }
    }

    public static class Provider implements ICapabilitySerializable<INBT>
    {
        private ISpecies species;
        private Capability.IStorage<ISpecies> storage;

        public Provider()
        {
            this.species = new Implementation(SpeciesCapability.HUMAN_ID);
            this.storage = CapabilityRegistry.SPECIES_CAPABILITY.getStorage();
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if(CapabilityRegistry.SPECIES_CAPABILITY.equals(cap)) {
                return LazyOptional.of(() -> species).cast();
            }
            return LazyOptional.empty();
        }

        @Override
        public INBT serializeNBT() {
            return this.storage.writeNBT(CapabilityRegistry.SPECIES_CAPABILITY, this.species, null);
        }

        @Override
        public void deserializeNBT(INBT nbt) {
            this.storage.readNBT(CapabilityRegistry.SPECIES_CAPABILITY, this.species, null, nbt);
        }
    }
    public static class Storage implements Capability.IStorage<ISpecies> {
        @Nullable
        @Override
        public INBT writeNBT(Capability<ISpecies> capability, ISpecies instance, Direction side) {
            CompoundNBT NBT = new CompoundNBT();
            NBT.putInt("Species",instance.getSpecies());
            return NBT;
        }

        @Override
        public void readNBT(Capability<ISpecies> capability, ISpecies instance, Direction side, INBT nbt) {
            CompoundNBT compound = (CompoundNBT) nbt;
            instance.setSpecies(compound.getInt("Species"));
        }
    }
}