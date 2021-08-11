package ink.echol.outofplacecraft.mixin;

import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.matrix.MatrixStack;
import ink.echol.outofplacecraft.capabilities.CapabilityRegistry;
import ink.echol.outofplacecraft.capabilities.ISpecies;
import ink.echol.outofplacecraft.client.YingletPlayerModel;
import ink.echol.outofplacecraft.entities.yinglet.YingletModel;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FirstPersonRenderer.class)
public class MixinFirstPersonRenderer {
    @Inject(method = "Lnet/minecraft/client/renderer/FirstPersonRenderer;renderHandsWithItems(FLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer$Impl;Lnet/minecraft/client/entity/player/ClientPlayerEntity;I)V", at = @At("HEAD"), cancellable = true)
    public void renderHandsWithItems(float p_228396_1_, MatrixStack p_228396_2_, IRenderTypeBuffer.Impl p_228396_3_, ClientPlayerEntity p_228396_4_, int p_228396_5_, CallbackInfo callback) {
        LazyOptional<ISpecies> capOpt = p_228396_4_.getCapability(CapabilityRegistry.SPECIES_CAPABILITY);
        if(capOpt.isPresent()) {
            ISpecies cap = capOpt.orElseThrow(NullPointerException::new);
            if(cap.isYinglet()) {
                Object obj = this;
                FirstPersonRenderer renderObj = (FirstPersonRenderer) obj;
                callback.cancel();
            }
        }
    }
}
