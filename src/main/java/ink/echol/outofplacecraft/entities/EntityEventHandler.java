package ink.echol.outofplacecraft.entities;

import ink.echol.outofplacecraft.OutOfPlacecraftMod;
import ink.echol.outofplacecraft.capabilities.CapabilityRegistry;
import ink.echol.outofplacecraft.capabilities.ISpecies;
import ink.echol.outofplacecraft.capabilities.SpeciesCapability;
import ink.echol.outofplacecraft.capabilities.SpeciesHelper;
import ink.echol.outofplacecraft.config.CommonConfig;
import ink.echol.outofplacecraft.items.ZatZhingItem;
import ink.echol.outofplacecraft.net.OOPCPacketHandler;
import ink.echol.outofplacecraft.net.SpeciesPacket;
import ink.echol.outofplacecraft.net.SyncSkinPkt;
import ink.echol.outofplacecraft.net.YingletSkinManager;
import ink.echol.outofplacecraft.potion.PotionRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Util;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;


@Mod.EventBusSubscriber(modid = OutOfPlacecraftMod.MODID)
public class EntityEventHandler {
    private static final long YING_STATUS_UPDATE_TICK_DELAY = 1024; // 51.2 seconds, just a pinch less than a minute.
    private static final long PLAYER_DIMENSIONS_REFRESH_DELAY = 64;

    public static final float YINGLET_EYE_HEIGHT_SCALE_STANDING = 0.85f;
    public static final float YINGLET_EYE_HEIGHT_SCALE_CROUCHING = 0.70f;
    public static final EntitySize YINGLET_SIZE_STANDING = EntitySize.scalable(0.5f, 1.25f);
    public static final EntitySize YINGLET_SIZE_CROUCH = EntitySize.scalable(0.4f, 0.9f);

    public static final float YINGLET_FALL_DAMAGE_MUL = 0.5f;
    public static final float YINGLET_VISIBILITY_MODIFIER = 0.75f;

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if(event.getObject() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getObject();
            event.addCapability(SpeciesCapability.ident, new SpeciesCapability.Provider());
        }
    }

    @SubscribeEvent
    public static void modifyVisibility(LivingEvent.LivingVisibilityEvent event) {
        if (!(event.getEntity() instanceof PlayerEntity)) {
            return;
        }
        PlayerEntity player = (PlayerEntity)event.getEntity();

        if(SpeciesHelper.getPlayerSpecies(player) == SpeciesCapability.YINGLET_ID) {
            event.modifyVisibility(YINGLET_VISIBILITY_MODIFIER);
        }
    }

    @SubscribeEvent
    public static void resizeYinglet(EntityEvent.Size event) {
        if (!(event.getEntity() instanceof PlayerEntity)) {
            return;
        }
        PlayerEntity player = (PlayerEntity)event.getEntity();

        //player.setForcedPose(Pose.SWIMMING);
        if(SpeciesHelper.getPlayerSpecies(player) == SpeciesCapability.YINGLET_ID) {
            if(player.isCrouching()) {
                event.setNewSize(YINGLET_SIZE_CROUCH, false);
                event.setNewEyeHeight((float) YINGLET_SIZE_CROUCH.height * YINGLET_EYE_HEIGHT_SCALE_CROUCHING);
            } else {
                event.setNewSize(YINGLET_SIZE_STANDING, false);
                event.setNewEyeHeight((float) YINGLET_SIZE_STANDING.height * YINGLET_EYE_HEIGHT_SCALE_STANDING);
            }
        }
    }

    @SubscribeEvent
    public static void potionAdded(PotionEvent.PotionAddedEvent event) {
        if( event.getEntityLiving() instanceof PlayerEntity) {

            PlayerEntity player = (PlayerEntity) event.getEntityLiving();

            Effect effect = event.getPotionEffect().getEffect();

            if( effect != null ) {
                if (SpeciesHelper.getPlayerSpecies(player) == SpeciesCapability.YINGLET_ID) {
                    // Cancel out the verious things yinglets are immune to.
                    if(effect == Effects.POISON) {
                        event.setResult(Event.Result.DENY);
                        player.removeEffect(effect);
                    }
                    //We used to disable hunger here but instead I'm just going to explicitly add an effect for rotten flesh.
                    // Beneficial effects of eating clams.
                    // TODO - move into the "on eat" entity events? This has no reason to be a potion effect now.
                    if(PotionRegistry.CLOMMED != null) {
                        if(PotionRegistry.CLOMMED.get() != null) {
                            if(effect == PotionRegistry.CLOMMED.get()) {
                                if(!player.level.isClientSide()) {
                                    player.addEffect(new EffectInstance(Effects.REGENERATION, 130, 1) );
                                    //player.addEffect(new EffectInstance(Effects.LUCK, 1800) );

                                    player.heal(2.0f);

                                    ArrayList<Effect> toRemove = new ArrayList<>();

                                    Collection<EffectInstance> oldEffects = player.getActiveEffects();
                                    for(EffectInstance eff : oldEffects) {
                                        if(!eff.getEffect().isBeneficial()) {
                                            toRemove.add(eff.getEffect());
                                        }
                                    }
                                    for(Effect e : toRemove) {
                                        player.removeEffect(e);
                                    }
                                }

                                //Clommed doesn't actually exist. It's just glue.
                                event.setResult(Event.Result.DENY);
                                player.removeEffect(effect);
                            }
                        }
                    }
                }
                else {
                    //Remove "Clommed" from human players
                    if(effect == PotionRegistry.CLOMMED.get()) {
                        event.setResult(Event.Result.DENY);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void potionApplicable(PotionEvent.PotionApplicableEvent event) {
        if( event.getEntityLiving() instanceof PlayerEntity) {

            PlayerEntity player = (PlayerEntity) event.getEntityLiving();

            Effect effect = event.getPotionEffect().getEffect();

            if (SpeciesHelper.getPlayerSpecies(player) == SpeciesCapability.YINGLET_ID) {
                // Cancel out the verious things yinglets are immune to.
                if(effect == Effects.POISON) {
                    event.setResult(Event.Result.DENY);
                }
            }
            else {
                //Remove "Clommed" from human players
                if(effect == PotionRegistry.CLOMMED.get()) {
                    event.setResult(Event.Result.DENY);
                }
            }
        }
    }

    @SubscribeEvent
    public static void breadBehavior(LivingEntityUseItemEvent.Finish event) {
        if( event.getEntityLiving() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            if(SpeciesHelper.getPlayerSpecies(player) == SpeciesCapability.YINGLET_ID) {
                if(event.getItem().getItem() != null) {

                    String itemName = event.getItem().getItem().getRegistryName().toString();

                    if( CommonConfig.BREADLIKE.get().contains(itemName) ) {
                        //Bread is not healthy for yings D:
                        if(player.level.isClientSide()) {
                            player.sendMessage((new TranslationTextComponent("chat.outofplacecraft.yingletVomitBread")).withStyle(TextFormatting.DARK_GREEN), Util.NIL_UUID);
                        }
                        int foodLevel = (player.getFoodData().getFoodLevel()/3)-1;
                        foodLevel = Math.min(foodLevel, 4);
                        if(foodLevel < 0) {
                            foodLevel = 0;
                        }
                        float satLevel = (player.getFoodData().getSaturationLevel()/3.0f)-1.0f;
                        satLevel = Math.min(satLevel, 4.0f);
                        if(satLevel < 0.0f) {
                            satLevel = 0.0f;
                        }
                        player.getFoodData().setFoodLevel(foodLevel);
                        player.getFoodData().setSaturation(satLevel);
                        player.addEffect(new EffectInstance(Effects.HUNGER, 90, 0));
                        player.addEffect(new EffectInstance(Effects.WEAKNESS, 300, 1));
                    }
                    else if(CommonConfig.CLAMLIKE.get().contains(itemName)) {
                        if(!player.hasEffect(PotionRegistry.CLOMMED.get())) {
                            player.addEffect(new EffectInstance(PotionRegistry.CLOMMED.get()));
                        }
                    }
                    else if( CommonConfig.ROTTEN.get().contains(itemName) ) {
                        //Yinglets are sometimes derogatorily called "scavs" - scavengers.
                        //This is because they scavenge. Like hyenas or vultures, they have evolved to eat dead things.

                        if(player.hasEffect(Effects.HUNGER)) {
                            OutOfPlacecraftMod.LOGGER.log(Level.INFO, "Removing a yinglet player's Hunger effect after eating rotten flesh.");
                            player.removeEffect(Effects.HUNGER);
                        }
                    }
                }
            }
        }
    }
    @SubscribeEvent
    public static void modifyFall(LivingFallEvent event) {
        if( event.getEntityLiving() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();

            if (SpeciesHelper.getPlayerSpecies(player) == SpeciesCapability.YINGLET_ID) {
                if(event.getDistance() < 8.0f) {
                    event.setDamageMultiplier(0.0f);
                    event.setCanceled(true);
                }
                else if((event.getDistance() > 64.0f) && (event.getDistance() < 64.0f)) {
                    event.setDamageMultiplier(YINGLET_FALL_DAMAGE_MUL);
                }
                else if((event.getDistance() > 64.0f) && (event.getDistance() < 80.0f)) {
                    //Being small will not save you if you fall a very great distance.
                    event.setDamageMultiplier(0.3f + YINGLET_FALL_DAMAGE_MUL);
                }
                //If fall distance is greater than 80, do not reduce damage at all.
            }
        }
    }

    @SubscribeEvent
    public static void preventFallDeath(LivingHurtEvent event) {
        if(event.getSource() == DamageSource.FALL) {
            if (event.getEntityLiving() instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) event.getEntityLiving();
                if (SpeciesHelper.getPlayerSpecies(player) == SpeciesCapability.YINGLET_ID) {
                    float playerHealth = player.getHealth();
                    if(event.getAmount() >= playerHealth) {
                        //Yinglets cannot die by falling.
                        float amount = playerHealth - 0.5f;
                        if( amount < 0.0f ) {
                            amount = 0.0f;
                        }
                        event.setAmount(amount);
                    }
                }
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
            SpeciesPacket pkt = new SpeciesPacket(event.getPlayer().getUUID(), oldSpecies, false);
            OOPCPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), pkt);
            ZatZhingItem.fixMaxHealth(event.getPlayer());
        }
        event.getPlayer().refreshDimensions();
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

                //Handle skin stuff.
                UUID targetUUID = target.getUUID();
                if(YingletSkinManager.getServer().skinIndex.containsKey(targetUUID)){
                    YingletSkinManager.SkinEntry entry = YingletSkinManager.getServer().skinIndex.get(targetUUID);
                    if(entry != null) {
                        SyncSkinPkt pkt = new SyncSkinPkt(targetUUID, entry.url, entry.hash);
                        OOPCPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) sendTo), pkt);
                    }
                }

                //Handle this stuff.
                SpeciesHelper.syncSpeciesToClient(sendTo, target);
            }
        }
        event.getPlayer().refreshDimensions();
    }

    @SubscribeEvent
    public static void onPlayerLogIn(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();
        if (player instanceof ServerPlayerEntity) {
            int speciesId = SpeciesHelper.getPlayerSpecies(player);
            SpeciesPacket pkt = new SpeciesPacket(event.getPlayer().getUUID(), speciesId, false);
            OOPCPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), pkt);
            ZatZhingItem.fixMaxHealth(event.getPlayer());

            //Handle skin stuff.
            YingletSkinManager.getServer().syncAllSkinsTo(player);
        }
        event.getPlayer().refreshDimensions();
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        PlayerEntity player = event.getPlayer();
        if (player instanceof ServerPlayerEntity) {
            int speciesId = SpeciesHelper.getPlayerSpecies(player);
            SpeciesPacket pkt = new SpeciesPacket(event.getPlayer().getUUID(), speciesId, false);
            OOPCPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), pkt);
            ZatZhingItem.fixMaxHealth(event.getPlayer());
        }
        event.getPlayer().refreshDimensions();
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {

        // "just-in-case" syncing of yinglet status doesn't need to be super frequent.
        PlayerEntity player = event.player;
        if (player instanceof ServerPlayerEntity) {
            if( (((ServerPlayerEntity) player).getLevel().getGameTime() % YING_STATUS_UPDATE_TICK_DELAY ) == 0) {
                //It is the appointed time! Let's do this.
                int speciesId = SpeciesHelper.getPlayerSpecies(player);
                SpeciesPacket pkt = new SpeciesPacket(player.getUUID(), speciesId, false);
                OOPCPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), pkt);
                ZatZhingItem.fixMaxHealth(player);
            }
            if( (((ServerPlayerEntity) player).getLevel().getGameTime() % 512 ) == 0) {
                //Handle skin stuff.
                YingletSkinManager.getServer().syncAllSkinsTo(player);
            }
        }

        if( (player.level.getGameTime() % PLAYER_DIMENSIONS_REFRESH_DELAY ) == 0) {
            player.refreshDimensions();
        }
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        PlayerEntity player = event.getPlayer();
        if (player instanceof ServerPlayerEntity) {
            int speciesId = SpeciesHelper.getPlayerSpecies(player);
            SpeciesPacket pkt = new SpeciesPacket(event.getPlayer().getUUID(), speciesId, false);
            OOPCPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), pkt);
            ZatZhingItem.fixMaxHealth(event.getPlayer());

            //Handle skin stuff.
            YingletSkinManager.getServer().syncAllSkinsTo(player);
        }
        event.getPlayer().refreshDimensions();
    }
}