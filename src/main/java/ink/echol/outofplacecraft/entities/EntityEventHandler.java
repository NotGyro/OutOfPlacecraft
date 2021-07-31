package ink.echol.outofplacecraft.entities;

import ink.echol.outofplacecraft.OutOfPlacecraftMod;
import ink.echol.outofplacecraft.capabilities.CapabilityRegistry;
import ink.echol.outofplacecraft.capabilities.ISpecies;
import ink.echol.outofplacecraft.capabilities.SpeciesCapability;
import ink.echol.outofplacecraft.capabilities.SpeciesHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = OutOfPlacecraftMod.MODID)
public class EntityEventHandler {
    private static final long YING_STATUS_UPDATE_TICK_DELAY = 1024; // 51.2 seconds, just a pinch less than a minute.
    private static final long PLAYER_DIMENSIONS_REFRESH_DELAY = 64;

    public static final float YINGLET_EYE_HEIGHT_SCALE_STANDING = 0.71f;
    public static final float YINGLET_EYE_HEIGHT_SCALE_CROUCHING = 0.6f;
    public static final EntitySize YINGLET_SIZE_STANDING = new EntitySize(0.5f, 1.25f, false);
    public static final EntitySize YINGLET_SIZE_CROUCH = new EntitySize(0.5f, 0.8f, false);


    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if(event.getObject() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getObject();
            event.addCapability(SpeciesCapability.ident, new SpeciesCapability.Provider());
        }
    }

    @SubscribeEvent
    public static void resizeYinglet(EntityEvent.Size event) {
        if (!(event.getEntity() instanceof PlayerEntity)) {
            return;
        }
        PlayerEntity player = (PlayerEntity)event.getEntity();

        if(SpeciesHelper.getPlayerSpecies(player) == SpeciesCapability.YINGLET_ID) {
            if(player.isCrouching()) {
                event.setNewSize(YINGLET_SIZE_CROUCH, false);
                event.setNewEyeHeight(event.getNewEyeHeight()*YINGLET_EYE_HEIGHT_SCALE_CROUCHING);
            } else {
                event.setNewSize(YINGLET_SIZE_STANDING, false);
                event.setNewEyeHeight(event.getNewEyeHeight()*YINGLET_EYE_HEIGHT_SCALE_STANDING);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        // Persist across player deaths & returning from the End.
        int oldSpecies = SpeciesHelper.getPlayerSpecies(event.getOriginal());
        ISpecies newCap = event.getPlayer().getCapability(CapabilityRegistry.SPECIES_CAPABILITY, null)
                .orElse(new SpeciesCapability.Implementation(SpeciesCapability.HUMAN_ID));
        newCap.setSpecies(oldSpecies);
        if (event.getPlayer() instanceof ServerPlayerEntity) {
            // If we're on server-side, notify the client.
            SpeciesHelper.syncSpeciesToClient(event.getPlayer(), event.getPlayer());
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

                SpeciesHelper.syncSpeciesToClient(sendTo, target);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLogIn(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();
        if (player instanceof ServerPlayerEntity) {
            SpeciesHelper.syncSpeciesToClient(player, player);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        PlayerEntity player = event.getPlayer();
        if (player instanceof ServerPlayerEntity) {
            SpeciesHelper.syncSpeciesToClient(player, player);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {

        // "just-in-case" syncing of yinglet status doesn't need to be super frequent.
        PlayerEntity player = event.player;
        if (player instanceof ServerPlayerEntity) {
            if( (((ServerPlayerEntity) player).getLevel().getGameTime() % YING_STATUS_UPDATE_TICK_DELAY ) == 0) {
                //It is the appointed time! Let's do this.
                SpeciesHelper.syncSpeciesToClient(player, player);
            }
        }

        if( (player.level.getGameTime() % PLAYER_DIMENSIONS_REFRESH_DELAY ) == 0) {
            player.refreshDimensions();
        }
    }
}