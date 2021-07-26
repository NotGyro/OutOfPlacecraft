package ink.echol.outofplacecraft.mixin;

import com.google.common.collect.Maps;
import ink.echol.outofplacecraft.capabilities.CapabilityRegistry;
import ink.echol.outofplacecraft.capabilities.IYingletStatus;
import ink.echol.outofplacecraft.capabilities.YingletStatus;
import ink.echol.outofplacecraft.client.YingletPlayerRenderer;
import ink.echol.outofplacecraft.entities.yinglet.YingletModel;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.bernie.geckolib3.core.IAnimatable;

@Mixin(EntityRendererManager.class)
public abstract class MixinEntityRendererManager {
    @Inject(method = "Lnet/minecraft/client/renderer/entity/EntityRendererManager;getRenderer(Lnet/minecraft/entity/Entity;)Lnet/minecraft/client/renderer/entity/EntityRenderer;", at = @At("HEAD"), cancellable = true)
    public <T extends Entity> void mixRenderer(T p_78713_1_, CallbackInfoReturnable callback) {
        if (p_78713_1_ instanceof AbstractClientPlayerEntity) {
            AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) p_78713_1_;
            LazyOptional<IYingletStatus> capOpt = player.getCapability(CapabilityRegistry.YINGLET_CAPABILITY);
            if(capOpt.isPresent()) {
                IYingletStatus cap = capOpt.orElseThrow(NullPointerException::new);
                if(cap.isYinglet()) {
                    //Class<?>[] interfaces = AbstractClientPlayerEntity.class.getInterfaces();
                    //System.out.println(interfaces);
                    //We're rendering a little guy!
                    //
                    if(player instanceof IAnimatable) {
                        //System.out.println("It's alive, it's ALIIIIVE!");
                        //I was able to get here.
                        //Ugly hax.
                        Object obj = (Object) this;
                        EntityRendererManager manager = ((EntityRendererManager) obj);
                        YingletPlayerRenderer rend = new YingletPlayerRenderer(manager);
                        callback.setReturnValue(rend);
                        callback.cancel();
                    }
                }
            }
            else {
                //Fail gracefully. (?)
            }
        }
    }
}