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
package ink.echol.outofplacecraft.entities.yinglet;

import ink.echol.outofplacecraft.OutOfPlacecraftMod;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class YingletModel extends AnimatedGeoModel<YingletEntity> {
	@Override
	public ResourceLocation getModelLocation(final YingletEntity object) {
		return new ResourceLocation(OutOfPlacecraftMod.MODID, "geo/yinglet.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(final YingletEntity object) {
		return new ResourceLocation(OutOfPlacecraftMod.MODID, "textures/yinglet/default.png");
	}

	@Override
	public ResourceLocation getAnimationFileLocation(final YingletEntity animatable) {
		return new ResourceLocation(OutOfPlacecraftMod.MODID, "animations/yinglet.animation.json");
	}
}
