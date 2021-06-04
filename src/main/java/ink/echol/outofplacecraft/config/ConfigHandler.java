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
package ink.echol.outofplacecraft.config;

import ink.echol.outofplacecraft.OutOfPlacecraftMod;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber(modid = OutOfPlacecraftMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConfigHandler {
	public static final ClientConfig CLIENT;
	public static final ForgeConfigSpec CLIENT_SPEC;
	public static final CommonConfig COMMON;
	public static final ForgeConfigSpec COMMON_SPEC;
	public static final ServerConfig SERVER;
	public static final ForgeConfigSpec SERVER_SPEC;

	static {
		final Pair<ClientConfig, ForgeConfigSpec> client = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
		CLIENT = client.getLeft();
		CLIENT_SPEC = client.getRight();

		final Pair<CommonConfig, ForgeConfigSpec> common = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
		COMMON = common.getLeft();
		COMMON_SPEC = common.getRight();

		final Pair<ServerConfig, ForgeConfigSpec> server = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
		SERVER = server.getLeft();
		SERVER_SPEC = server.getRight();
	}
}
