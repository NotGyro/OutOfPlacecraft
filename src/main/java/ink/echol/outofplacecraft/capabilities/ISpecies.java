package ink.echol.outofplacecraft.capabilities;

// Species value is implemented as *the hash code of* a species name, to save space and CPU-time.
public interface ISpecies {
    int getSpecies();
    void setSpecies(int value);
    @Deprecated
    boolean isYinglet();
}
