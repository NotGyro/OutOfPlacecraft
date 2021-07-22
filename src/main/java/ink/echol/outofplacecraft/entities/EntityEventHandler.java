package ink.echol.outofplacecraft.entities;

import ink.echol.outofplacecraft.OutOfPlacecraftMod;
import ink.echol.outofplacecraft.capabilities.CapabilityRegistry;
import ink.echol.outofplacecraft.capabilities.IYingletStatus;
import ink.echol.outofplacecraft.capabilities.YingletStatus;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = OutOfPlacecraftMod.MODID)
public class EntityEventHandler {

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if(event.getObject() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getObject();
            event.addCapability(YingletStatus.ident, new YingletStatus.Provider());
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        // Persist across player deaths & returning from the End.
        IYingletStatus cap = event.getOriginal().getCapability(CapabilityRegistry.YINGLET_CAPABILITY, null).orElse(new YingletStatus.Implementation(false));
        IYingletStatus newCap = event.getPlayer().getCapability(CapabilityRegistry.YINGLET_CAPABILITY, null).orElseThrow(NullPointerException::new);
        newCap.setIsYinglet(cap.isYinglet());
    }
}