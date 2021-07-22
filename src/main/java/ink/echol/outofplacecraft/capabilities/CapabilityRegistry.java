package ink.echol.outofplacecraft.capabilities;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityRegistry {
    /** A capability allowing you to check if a player is a yinglet or not. */
    @CapabilityInject(IYingletStatus.class)
    public static Capability<IYingletStatus> YINGLET_CAPABILITY = null;

    public static void initCapabilities() {
        CapabilityManager.INSTANCE.register(IYingletStatus.class, new YingletStatus.Storage(), YingletStatus.Implementation::new);
    }
}
