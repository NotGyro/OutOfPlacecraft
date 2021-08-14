package ink.echol.outofplacecraft.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.ObjectUtils;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoCube;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.util.RenderUtils;

@OnlyIn(Dist.CLIENT)
public class YingletPlayerRenderer<T extends LivingEntity & IAnimatable> extends GeoEntityRenderer<T> {

    protected static YingletPlayerRenderer INSTANCE = null;

    private ItemStack rightHandItem = null;
    private ItemStack leftHandItem = null;

    private ResourceLocation hackyTextureFix;
    private IRenderTypeBuffer rtb;

    public YingletPlayerRenderer(EntityRendererManager renderManager) {
        super(renderManager, new YingletPlayerModel());
        //this.addLayer(new YingletHeldItemLayer(this));
    }


    @Override
    public void renderEarly(LivingEntity animatable, MatrixStack stackIn, float ticks, IRenderTypeBuffer renderTypeBuffer, IVertexBuilder vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks) {
        //stackIn.scale(5.0f, 5.0f, 5.0f);
        this.rtb = renderTypeBuffer;
        this.hackyTextureFix = this.getTextureLocation((T) animatable);

        if(animatable.getItemInHand(Hand.MAIN_HAND) != null) {
            if( !animatable.getItemInHand(Hand.MAIN_HAND).isEmpty() ) {
                this.rightHandItem = animatable.getItemInHand(Hand.MAIN_HAND);
            }
        }
        if(animatable.getItemInHand(Hand.OFF_HAND) != null) {
            if( !animatable.getItemInHand(Hand.OFF_HAND).isEmpty() ) {
                this.leftHandItem = animatable.getItemInHand(Hand.OFF_HAND);
            }
        }

        super.renderEarly((T) animatable, stackIn, ticks, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, partialTicks);
    }

    @Override
    public RenderType getRenderType(LivingEntity animatable, float partialTicks, MatrixStack stack,
                                    IRenderTypeBuffer renderTypeBuffer, IVertexBuilder vertexBuilder, int packedLightIn,
                                    ResourceLocation textureLocation) {
        return RenderType.entityCutout(getTextureLocation((T) animatable));
    }

    @Override
    public void renderRecursively(GeoBone bone, MatrixStack stack, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        //The shoulder / arm / hand bones were misnamed. Fantastic.
        if ( bone.getName().equals( "left_item_holder" ) ) {
            if(this.rightHandItem != null) {
                if(! this.rightHandItem.isEmpty()) {
                    renderItemInHand(HandSide.RIGHT, this.rightHandItem,
                            bone, stack, bufferIn, packedLightIn,
                            packedOverlayIn, red, green, blue, alpha);
                    this.rightHandItem = null;
                    bufferIn = rtb.getBuffer( RenderType.entitySmoothCutout( this.whTexture ) );
                }
            }
        }
        else if( bone.getName().equals("right_item_holder")) {
            if(this.leftHandItem != null) {
                if(! this.leftHandItem.isEmpty()) {
                    renderItemInHand(HandSide.LEFT, this.leftHandItem,
                                    bone, stack, bufferIn, packedLightIn,
                                    packedOverlayIn, red, green, blue, alpha);
                    this.leftHandItem = null;
                    bufferIn = rtb.getBuffer( RenderType.entitySmoothCutout( this.whTexture ) );
                }
            }
        }
        super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    public void renderItemInHand(HandSide h, ItemStack item, GeoBone bone, MatrixStack stack, IVertexBuilder bufferIn,
                           int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        ItemCameraTransforms.TransformType tt =
                (h == HandSide.RIGHT) ? ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND
                                      : ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND;

        stack.pushPose();
        if(h == HandSide.RIGHT) {
            stack.mulPose(Vector3f.XP.rotationDegrees(-90.0f));
        }

        RenderUtils.translate(bone, stack);
        RenderUtils.moveToPivot(bone, stack);
        RenderUtils.rotate(bone, stack);
        //RenderUtils.scale(next, matrixStackIn);
        RenderUtils.moveBackFromPivot(bone, stack);

        stack.pushPose();
        if(h == HandSide.RIGHT) {
            //--------------------------A                B                C
            stack.translate(0.13, 0.23, 0.75);
        }
        else {
            //------------------------  -A               C                 -B ?
            stack.translate(-0.13, 0.88, -0.43);
        }
        stack.pushPose();
        stack.scale(0.5f, 0.5f, 0.5f);

        if(h == HandSide.LEFT) {
            stack.mulPose(Vector3f.ZP.rotationDegrees(180.0f));
            stack.mulPose(Vector3f.XP.rotationDegrees(20.0f));
        }

        Minecraft.getInstance().getItemRenderer()
                .renderStatic( item, tt, packedLightIn, packedOverlayIn, stack, this.rtb );
        stack.popPose();
        stack.popPose();
        stack.popPose();
    }
}
