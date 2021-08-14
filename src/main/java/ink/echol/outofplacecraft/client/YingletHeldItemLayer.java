package ink.echol.outofplacecraft.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import ink.echol.outofplacecraft.capabilities.SpeciesCapability;
import ink.echol.outofplacecraft.capabilities.SpeciesHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoCube;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;
import software.bernie.geckolib3.util.RenderUtils;

import java.util.Optional;
import java.util.Stack;

public class YingletHeldItemLayer extends GeoLayerRenderer {

    public YingletHeldItemLayer(IGeoRenderer entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, Entity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if(entitylivingbaseIn instanceof PlayerEntity) {
            PlayerEntity thisPlayer = (PlayerEntity) entitylivingbaseIn;
            if(SpeciesHelper.getPlayerSpecies(thisPlayer) == SpeciesCapability.YINGLET_ID) {

                ItemStack item_right = thisPlayer.getItemInHand(Hand.MAIN_HAND);
                ItemStack item_left = thisPlayer.getItemInHand(Hand.OFF_HAND);

                if(item_right != null) {
                    if (!item_right.isEmpty()) {
                        this.renderHand(thisPlayer, item_right, HandSide.RIGHT,
                                matrixStackIn, bufferIn, packedLightIn,
                                limbSwing, limbSwingAmount, partialTicks,
                                ageInTicks, netHeadYaw, headPitch);
                    }
                }
                if(item_left != null) {
                    if (!item_left.isEmpty()) {
                        this.renderHand(thisPlayer, item_left, HandSide.LEFT,
                                matrixStackIn, bufferIn, packedLightIn,
                                limbSwing, limbSwingAmount, partialTicks,
                                ageInTicks, netHeadYaw, headPitch);
                    }
                }
            }
        }
    }
    protected void renderHand(PlayerEntity player, ItemStack item, HandSide handSide,
                              MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {


        boolean flag = handSide == HandSide.RIGHT;

        YingletPlayerModel model = ((YingletPlayerModel)getEntityModel());
        GeoBone hand_bone = (GeoBone) ( flag ? ((AnimatedGeoModel)this.getEntityModel()).getAnimationProcessor().getBone("hand_right_bone")
                                : ((AnimatedGeoModel)this.getEntityModel()).getAnimationProcessor().getBone("hand_left_bone"));

        int posesPushed = 0;

        //Let's go for a walk.
        Stack<GeoBone> boneStack = new Stack<>();
        boneStack.push(hand_bone);
        GeoBone parent = hand_bone.parent;
        GeoBone current = null;
        //Construct a stack of all involved bones up until the root node.
        while(parent != null) {
            boneStack.push(parent);
            current = parent;
            parent = current.parent;
        }

        //Make sure our stack includes every transform.
        GeoBone next = boneStack.pop();
        while(!boneStack.isEmpty()) {
            matrixStackIn.pushPose();
            posesPushed = posesPushed+1;

            RenderUtils.translate(next, matrixStackIn);
            RenderUtils.moveToPivot(next, matrixStackIn);
            RenderUtils.rotate(next, matrixStackIn);
            //RenderUtils.scale(next, matrixStackIn);
            RenderUtils.moveBackFromPivot(next, matrixStackIn);

            next = boneStack.pop();
        }

        Optional<GeoCube> maybeCube = hand_bone.childCubes.stream().findFirst();
        if(maybeCube.isPresent()) {
            GeoCube cube = maybeCube.get();

            matrixStackIn.pushPose();
            posesPushed = posesPushed+1;

            RenderUtils.moveToPivot(cube, matrixStackIn);
            RenderUtils.rotate(cube, matrixStackIn);
            RenderUtils.moveBackFromPivot(cube, matrixStackIn);

        }
        //Turn it so it's standing up, not laying flat with the hand.

        matrixStackIn.pushPose();

        //Fix things which cannot be fixed intelligently - we have to do it the stupid way.
        if(flag) {
            //matrixStackIn.translate(0.0d, 1.0d, 0.0d);
            ///matrixStackIn.scale(0.5f, 0.5f, 0.5f);
        }
        else {

        }
        //

        ItemCameraTransforms.TransformType tt = flag ? ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND
                                                     : ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND;
        //ItemCameraTransforms.TransformType tt = ItemCameraTransforms.TransformType.NONE;

        Minecraft.getInstance().getItemInHandRenderer()
                .renderItem(player, item, tt,
                            !flag, matrixStackIn, bufferIn, packedLightIn);

        matrixStackIn.popPose();

        while( posesPushed > 0) {
            matrixStackIn.popPose();
            posesPushed -= 1;
        }
    }
}
