package ink.echol.outofplacecraft.entities.yinglet;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class YingletRenderer extends GeoEntityRenderer<YingletEntity> {
	protected YingletRenderer(final EntityRendererManager renderManager, final AnimatedGeoModel<YingletEntity> modelProvider) {
		super(renderManager, modelProvider);
	}
}
