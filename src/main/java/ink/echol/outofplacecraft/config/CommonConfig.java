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

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import ink.echol.outofplacecraft.OutOfPlacecraftMod;
import ink.echol.outofplacecraft.items.ItemRegistry;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class CommonConfig {

	protected static final String FOOD_SECTION = "food";

	// Foods that are uniquely beneficial to a yinglet.
	protected static final List<String> CLAMLIKE_PATH = Arrays.asList(FOOD_SECTION, "foodsClamlike");
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> CLAMLIKE;

	// Foods that make a yinglet sick.
	protected static final List<String> BREADLIKE_PATH = Arrays.asList(FOOD_SECTION, "foodsBreadlike");
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> BREADLIKE;

	// Rotten foods to make safe for yinglets
	protected static final List<String> ROTTEN_PATH = Arrays.asList(FOOD_SECTION, "safeRotten");
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> ROTTEN;

	CommonConfig(ForgeConfigSpec.Builder builder) {

		builder.comment("Yinglet dietary settings").push("food");
		CLAMLIKE = builder.comment(" A list of food items (as fully-qualified modname:unlocalized_item_name pairs)\n" +
				" that are uniquely beneficial for yinglets.\n" +
				" Per Out Of Placers lore, this should be raw meat of mollusks (any shellfish, slug, or snail.")
				.defineListAllowEmpty(this.CLAMLIKE_PATH,
					() -> Arrays.asList(OutOfPlacecraftMod.MODID + ":clam"),
					o -> o instanceof String);

		BREADLIKE = builder.comment(" A list of food items (as fully-qualified modname:unlocalized_item_name pairs)\n" +
						" that cannot be digested by yinglets, and make them sick.\n" +
						" Per Out Of Placers lore, this should be anything that contains grass glutin.\n" +
						" Anything that would be made with flour should be on this list.")
				.defineListAllowEmpty(this.BREADLIKE_PATH,
						() -> Arrays.asList("minecraft:bread", "minecraft:cake"),
						o -> o instanceof String);

		ROTTEN = builder.comment(" A list of food items (as fully-qualified modname:unlocalized_item_name pairs)\n" +
						" that would normally give you the Hunger effect, but which are safe for yinglets.\n" +
						" Yinglets are sometimes derogatorily called \"scavs\" - scavengers.\n" +
						" This is because they scavenge. Like hyenas or vultures, they have evolved to eat dead things.")
				.defineListAllowEmpty(this.ROTTEN_PATH,
						() -> Arrays.asList("minecraft:rotten_flesh"),
						o -> o instanceof String);
		builder.pop();
	}
}
