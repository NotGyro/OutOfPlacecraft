package ink.echol.outofplacecraft.entities;

import com.google.common.eventbus.Subscribe;
import ink.echol.outofplacecraft.OutOfPlacecraftMod;
import ink.echol.outofplacecraft.capabilities.CapabilityRegistry;
import ink.echol.outofplacecraft.capabilities.IYingletStatus;
import ink.echol.outofplacecraft.capabilities.YingletStatus;
import ink.echol.outofplacecraft.entities.yinglet.Yinglet;
import ink.echol.outofplacecraft.net.OOPCPacketHandler;
import ink.echol.outofplacecraft.net.YingletStatusPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.UUID;


@Mod.EventBusSubscriber(modid = OutOfPlacecraftMod.MODID)
public class EntityEventHandler {
    private static final long YING_STATUS_UPDATE_TICK_DELAY = 1024; // 51.2 seconds, just a pinch less than a minute.
    private static final long PLAYER_DIMENSIONS_REFRESH_DELAY = 64;

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
        if (event.getPlayer() instanceof ServerPlayerEntity) {
            // If we're on server-side, notify the client.
            syncYingletStatusToClient(event.getPlayer(), event.getPlayer());
        }
    }

    /**
     * Fired whenever the server recognizes player A should start receiving updates about entity B. (i.e. B is in range of A, close enough to be relevant.)
     *
     */
    @SubscribeEvent
    public static void onPlayerStartTracking(PlayerEvent.StartTracking event) {
        if (event.getPlayer() instanceof ServerPlayerEntity) {
            // We only care if it's another player.
            if (event.getTarget() instanceof PlayerEntity) {
                PlayerEntity sendTo = event.getPlayer();
                PlayerEntity target = (PlayerEntity) event.getTarget();

                syncYingletStatusToClient(sendTo, target);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLogIn(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();
        if (player instanceof ServerPlayerEntity) {
            syncYingletStatusToClient(player, player);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        PlayerEntity player = event.getPlayer();
        if (player instanceof ServerPlayerEntity) {
            syncYingletStatusToClient(player, player);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {

        // "just-in-case" syncing of yinglet status doesn't need to be super frequent.
        PlayerEntity player = event.player;
        if (player instanceof ServerPlayerEntity) {
            if( (((ServerPlayerEntity) player).getLevel().getGameTime() % YING_STATUS_UPDATE_TICK_DELAY ) == 0) {
                //It is the appointed time! Let's do this.
                syncYingletStatusToClient(player, player);
            }
        }

        if( (player.level.getGameTime() % PLAYER_DIMENSIONS_REFRESH_DELAY ) == 0) {
            player.refreshDimensions();
        }
    }

    private static void syncYingletStatusToClient(PlayerEntity player, PlayerEntity target) {
        // It shouldn't be possible to get here without being serversided, but we might as well sanity check it, just in case.
        if (player instanceof ServerPlayerEntity) {
            IYingletStatus targetCap = target.getCapability(CapabilityRegistry.YINGLET_CAPABILITY, null).orElseThrow(NullPointerException::new);
            UUID targetUUID = target.getGameProfile().getId();
            YingletStatusPacket pkt = new YingletStatusPacket(targetUUID, targetCap.isYinglet());
            OOPCPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) target), pkt);
        }
    }
}