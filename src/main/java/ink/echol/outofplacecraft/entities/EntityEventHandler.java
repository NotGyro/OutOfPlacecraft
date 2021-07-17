package ink.echol.outofplacecraft.entities;

import ink.echol.outofplacecraft.OutOfPlacecraftMod;
import ink.echol.outofplacecraft.capabilities.IYingletStatus;
import ink.echol.outofplacecraft.capabilities.YingletStatus;
import ink.echol.outofplacecraft.capabilities.YingletStatusProvider;
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
            event.addCapability(YingletStatusProvider.ident, new YingletStatusProvider());
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        // Persist across player deaths & returning from the End.
        IYingletStatus cap = event.getOriginal().getCapability(YingletStatusProvider.yingletStatus).orElse(new YingletStatus(false));
        IYingletStatus newCap = event.getPlayer().getCapability(YingletStatusProvider.yingletStatus).orElseThrow(NullPointerException::new);
        newCap.setIsYinglet(cap.isYinglet());
    }
}