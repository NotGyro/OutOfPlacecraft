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
package ink.echol.outofplacecraft.entities.yinglet.rendering;


import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class YingletClientEventHandler {

	@SubscribeEvent
	public static void thirdPersonPreRender(RenderPlayerEvent.Pre renderPlayerEvent) {
		// TODO test for being a yinglet
		if(true) {
			//renderPlayerEvent.setCanceled(true);
			// TODO render the yinglet

		}
	}
}
