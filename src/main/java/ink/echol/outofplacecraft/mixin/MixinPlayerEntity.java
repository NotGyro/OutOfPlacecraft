package ink.echol.outofplacecraft.mixin;

import ink.echol.outofplacecraft.capabilities.SpeciesCapability;
import ink.echol.outofplacecraft.capabilities.SpeciesHelper;
import ink.echol.outofplacecraft.entities.EntityEventHandler;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity {

    @Inject(method = "Lnet/minecraft/entity/player/PlayerEntity;getDimensions(Lnet/minecraft/entity/Pose;)Lnet/minecraft/entity/EntitySize;", at=@At("HEAD"), cancellable = true)
    public void getDimensionsForPose(Pose pose, CallbackInfoReturnable<EntitySize> cir) {
        if(pose == Pose.CROUCHING) {
            Object obj = (Object) this;
            if(obj instanceof PlayerEntity) {
                PlayerEntity thisPlayer = ((PlayerEntity) obj);
                if (SpeciesHelper.getPlayerSpecies(thisPlayer) == SpeciesCapability.YINGLET_ID) {
                    cir.setReturnValue(thisPlayer.getDimensions(Pose.SWIMMING));
                    cir.cancel();
                }
            }
        }
    }
}