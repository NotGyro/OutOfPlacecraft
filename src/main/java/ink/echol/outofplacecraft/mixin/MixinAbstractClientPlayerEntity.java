package ink.echol.outofplacecraft.mixin;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Pose;
import net.minecraft.util.Hand;
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

        Pose pose = thisPlayer.getPose();
        if (thisPlayer.getForcedPose() != null ) {
            pose = thisPlayer.getForcedPose();
        }
        AnimationBuilder ab = new AnimationBuilder();

        float attack_anim_min = 0.01f;

        if ( thisPlayer.attackAnim > attack_anim_min) {
            if( thisPlayer.swingingArm == Hand.MAIN_HAND ) {
                ab = ab.addAnimation("animation.yinglet.INTERACT_ATTACK_RIGHT", true);
            }
            else {
                ab = ab.addAnimation("animation.yinglet.INTERACT_ATTACK_LEFT", true);
            }
        }

        if(thisPlayer.isPassenger()) {
            ab = ab.addAnimation("animation.yinglet.SITTING", true);
        }
        else {
            if (thisPlayer.abilities.flying) {
                ab = ab.addAnimation("animation.yinglet.FLY", true);
            }
            else {
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
