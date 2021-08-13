package ink.echol.outofplacecraft.items;

import ink.echol.outofplacecraft.OutOfPlacecraftMod;
import ink.echol.outofplacecraft.capabilities.CapabilityRegistry;
import ink.echol.outofplacecraft.capabilities.ISpecies;
import ink.echol.outofplacecraft.capabilities.SpeciesCapability;
import ink.echol.outofplacecraft.capabilities.SpeciesHelper;
import ink.echol.outofplacecraft.net.OOPCPacketHandler;
import ink.echol.outofplacecraft.net.SpeciesPacket;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.item.*;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.logging.log4j.Level;

import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class ZatZhingItem extends Item {
    public ZatZhingItem(Properties p_i48476_1_) {
        super(p_i48476_1_);
    }
    public int getUseDuration(ItemStack p_77626_1_) {
        return 32;
    }
    public UseAction getUseAnimation(ItemStack p_77661_1_) {
        return UseAction.EAT;
    }

    // Is this player just eating the thing, or throwing it?
    protected boolean onSelf(World world, PlayerEntity player, Hand hand) {
        if( player.isCrouching() ) {
            return false;
        }
        else {
            return true;
        }
    }

    protected ActionResult<ItemStack> actionThrow( World world, PlayerEntity player, Hand hand ) {
        //Zhrow sound.
        world.playSound((PlayerEntity)null, player.getX(), player.getY(), player.getZ(), SoundEvents.SPLASH_POTION_THROW, SoundCategory.PLAYERS, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));

        ItemStack itemstack = player.getItemInHand(hand);
        if (!world.isClientSide) {
            PotionEntity potionentity = new PotionEntity(world, player);
            potionentity.setItem(itemstack);
            potionentity.shootFromRotation(player, player.xRot, player.yRot, -20.0F, 0.5F, 1.0F);
            world.addFreshEntity(potionentity);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.abilities.instabuild) {
            itemstack.shrink(1);
        }

        return ActionResult.consume(itemstack);
    }

    protected ActionResult<ItemStack> actionEatStart(World world, PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return ActionResult.consume(itemstack);
    }

    protected boolean canApplyOnSelf(World world, PlayerEntity player, Hand hand) {
        ISpecies cap = player.getCapability(CapabilityRegistry.SPECIES_CAPABILITY).orElseThrow(NullPointerException::new);
        //If we're already yinged this does nothing.
        return !cap.isYinglet();
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if( this.onSelf(world, player, hand) && this.canApplyOnSelf(world, player, hand) ) {
            return this.actionEatStart(world, player, hand);
        }
        //Commenting this out until we have actual splash potion behavior going.
        /*
        else {
            return this.actionThrow(world, player, hand);
        }*/

        return super.use(world, player, hand);
    }

   //What happens at the end of the eating/drinking anim.
    @Override
    public ItemStack finishUsingItem(ItemStack stack, World world, LivingEntity entity) {
        //
        if(entity instanceof PlayerEntity) {
            PlayerEntity playerentity = (PlayerEntity) entity;
            if(playerentity instanceof ServerPlayerEntity) {
                CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayerEntity) playerentity, stack);
            }

            if(!world.isClientSide) {
                this.applyZhingEffect(stack, world, playerentity);
            }
            if (!playerentity.abilities.instabuild) {
                stack.shrink(1);
            }
            return stack;
        }
        else {
            // Figure out how to log a warning if a non-player is trying to use this.
            return stack;
        }
    }

    public static final float SCALE_HEALTH = -0.4f;
    public static final AttributeModifier YINGLET_HEALTH_MODIFIER = new AttributeModifier("yinglet_health_reduce", SCALE_HEALTH, AttributeModifier.Operation.MULTIPLY_BASE);

    public static void applyMaxHealthEffect(PlayerEntity player) {
        if (!player.getAttribute(Attributes.MAX_HEALTH).hasModifier(YINGLET_HEALTH_MODIFIER)) {
            if (player.getAttribute(Attributes.MAX_HEALTH).getValue() == player.getAttribute(Attributes.MAX_HEALTH).getBaseValue()) {
                if (SpeciesHelper.getPlayerSpecies(player) == SpeciesCapability.YINGLET_ID) {
                    player.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(YINGLET_HEALTH_MODIFIER);
                }
            }
        } else {
            if (SpeciesHelper.getPlayerSpecies(player) != SpeciesCapability.YINGLET_ID) {
                player.getAttribute(Attributes.MAX_HEALTH).removePermanentModifier(YINGLET_HEALTH_MODIFIER.getId());
            }
        }
    }

    public static void fixMaxHealth(PlayerEntity player) {
        Set<AttributeModifier> modifiers = player.getAttribute(Attributes.MAX_HEALTH).getModifiers();
        Iterator<AttributeModifier> iter = modifiers.iterator();
        while(iter.hasNext()) {
            AttributeModifier mod = iter.next();
            if( mod.getName().equalsIgnoreCase("yinglet_health_reduce") ) {
                player.getAttribute(Attributes.MAX_HEALTH).removePermanentModifier(mod.getId());
            }
        }
        applyMaxHealthEffect(player);
    }

    // Method for entering my magical realm. >:}
    // Returns true if we should consume the item - false if it didn't get used and won't be consumed.
    public static void applyZhingEffect(ItemStack stack, World world, PlayerEntity player) {
        player.sendMessage((new TranslationTextComponent("chat.outofplacecraft.yingletTransformation")).withStyle(TextFormatting.GREEN), Util.NIL_UUID);
        // This will probably never be called client-side, but... better safe than sorry.
        if(!world.isClientSide) {
            // Oops.
            OutOfPlacecraftMod.LOGGER.log(Level.INFO, "A player has been \"zat zhing\"'d: " + player.getScoreboardName());

            SpeciesHelper.setPlayerSpecies(player, SpeciesCapability.YINGLET_ID);
            fixMaxHealth(player);
        }
    }
}