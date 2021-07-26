package ink.echol.outofplacecraft.mixin;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
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
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.yinglet.stand", true));
        return PlayState.CONTINUE;
    }
    protected AnimationFactory animationFactory = new AnimationFactory((IAnimatable)  this);

    public AnimationFactory oopc$getFactory() {
        return animationFactory;
    }
}
