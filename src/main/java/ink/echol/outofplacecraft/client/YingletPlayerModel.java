/**
 * OutOfPlaceCraft
 * Copyright (c) 2021.
 *
 * This file is part of OutOfPlaceCraft.
 *
 * OutOfPlaceCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OutOfPlaceCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OutOfPlaceCraft.  If not, see <https://www.gnu.org/licenses/>.
 */
package ink.echol.outofplacecraft.client;

import ink.echol.outofplacecraft.OutOfPlacecraftMod;
import ink.echol.outofplacecraft.net.YingletSkinManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import java.util.UUID;

import static ink.echol.outofplacecraft.client.SkinTextureLoader.SKIN_RESOURCE_PACK;

public class YingletPlayerModel extends AnimatedGeoModel {
	@Override
	public ResourceLocation getModelLocation(Object entity) {
		return new ResourceLocation(OutOfPlacecraftMod.MODID, "geo/yinglet.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(Object entity) {
		if( entity instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) entity;
			UUID id = player.getUUID();
			if(YingletSkinManager.getClient().skinIndex.containsKey(id)) {
				return SkinTextureLoader.ensureLoaded(id);
			}
		}
		return new ResourceLocation(OutOfPlacecraftMod.MODID, "textures/yinglet/default.png");
	}

	@Override
	public ResourceLocation getAnimationFileLocation(Object entity) {
		return new ResourceLocation(OutOfPlacecraftMod.MODID, "animations/yinglet.animation.json");
	}

	@Override
	public void setLivingAnimations(IAnimatable entity, Integer uniqueID, AnimationEvent customPredicate) {
		super.setLivingAnimations(entity, uniqueID, customPredicate);

		// Handle head rotation.
		if(entity instanceof PlayerEntity) {
			IBone head = this.getAnimationProcessor().getBone("head_bone");
			PlayerEntity player = (PlayerEntity) entity;

			float tickTime = customPredicate.getPartialTick();

			float f = MathHelper.rotLerp(tickTime, player.yBodyRotO, player.yBodyRot);
			float f1 = MathHelper.rotLerp(tickTime, player.yHeadRotO, player.yHeadRot);
			float angle = f1 - f;
			float rad_angle = (float) -((angle) * (Math.PI/180));
			head.setRotationY(rad_angle + head.getRotationY());

			float angleX = MathHelper.lerp(tickTime, player.xRotO, player.xRot);
			float rad_angleX = (float) -((angleX) * (Math.PI/180));
			head.setRotationX(rad_angleX + head.getRotationX());
		}
	}
}
