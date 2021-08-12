package ink.echol.outofplacecraft.mixin;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Pose;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

@Mixin(AbstractClientPlayerEntity.class)
@Implements(@Interface(iface = IAnimatable.class, prefix = "oopc$", remap= Interface.Remap.NONE))
public abstract class MixinAbstractClientPlayerEntity {
    public void oopc$registerControllers(AnimationData data) {
        //TODO: This, properly.
        data.addAnimationController(new AnimationController((IAnimatable) this, "controller", 0, this::predicate));
    }

    protected <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {

        Object obj = (Object) this;
        AbstractClientPlayerEntity thisPlayer = ((AbstractClientPlayerEntity) obj);

        AnimationBuilder ab = new AnimationBuilder();

        boolean idle = true;

        //None of the above animation is playing. Default to stand.
        Pose pose = thisPlayer.getPose();
        if (thisPlayer.getForcedPose() != null ) {
            pose = thisPlayer.getForcedPose();
        }

        boolean isCrouching = pose == Pose.CROUCHING;


        //Vector3d  = thisPlayer.getDeltaMovement();
        Vector3d playerDelta = new Vector3d(thisPlayer.getX() - thisPlayer.xo, thisPlayer.getY() - thisPlayer.yo, thisPlayer.getZ() - thisPlayer.zo);

        boolean isWalkingOrRunning = Math.sqrt(Math.pow(playerDelta.x, 2) + Math.pow(playerDelta.z, 2)) > 0.005;

        float fallAmount = (float) playerDelta.y();
        boolean fallingFlag = (fallAmount + 0.5f) < 0.0f; //thisPlayer.getFallFlyingTicks() > 4;

        // Sit
        if(thisPlayer.isPassenger()) {
            ab = ab.addAnimation("animation.yinglet.SITTING", true);
            idle = false;
        }
        else {
            if (thisPlayer.abilities.flying && !thisPlayer.isOnGround() && !thisPlayer.isInWater() && !thisPlayer.isInLava() ) {
                ab = ab.addAnimation("animation.yinglet.FLY", true);
                idle = false;
            }
            else if(!thisPlayer.isOnGround() && thisPlayer.isInWater()) {
                ab = ab.addAnimation("animation.yinglet.SWIMMING", true);
                idle = false;
            }
            else if(fallingFlag) {
                ab = ab.addAnimation("animation.yinglet.FALLING", true);
                idle = false;
            }
            // Walking or running
            else if(isWalkingOrRunning) {
                if(isCrouching && thisPlayer.animationSpeed != 0.0f) {
                    // No crouch running yet.
                    ab = ab.addAnimation("animation.yinglet.CROUCH_WALK", true);
                    idle = false;
                }
                else {
                    //Not crouching
                    //But we ARE sprinting.
                    if(thisPlayer.isSprinting()) {
                        //Running
                        ab = ab.addAnimation("animation.yinglet.RUNNING", true);
                        idle = false;

                    }
                    else if (thisPlayer.animationSpeed != 0.0f) {
                        //Walking
                        ab = ab.addAnimation("animation.yinglet.WALK", true);
                        idle = false;
                    }
                }
            }
        }

        if(pose == Pose.SLEEPING) {
            ab = ab.addAnimation("animation.yinglet.SLEEPING", true);
            idle = false;
        }

        float attack_anim_min = 0.01f;

        if ( thisPlayer.attackAnim > attack_anim_min) {
            if( thisPlayer.swingingArm == Hand.MAIN_HAND ) {
                ab = ab.addAnimation("animation.yinglet.INTERACT_ATTACK_RIGHT", true);
                idle = false;
            }
            else {
                ab = ab.addAnimation("animation.yinglet.INTERACT_ATTACK_LEFT", true);
                idle = false;
            }
        }

        //None of the above animations was selected, do the regular standing animation.
        if(idle) {
            if(isCrouching) {
                ab = ab.addAnimation("animation.yinglet.CROUCH_IDLE", true);
            } else {
                ab = ab.addAnimation("animation.yinglet.STAND", true);
            }
        }

        event.getController().setAnimation(ab);
        return PlayState.CONTINUE;
    }
    protected AnimationFactory animationFactory = new AnimationFactory((IAnimatable)  this);

    public AnimationFactory oopc$getFactory() {
        return animationFactory;
    }

}
