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
package ink.echol.outofplacecraft;

import ink.echol.outofplacecraft.blocks.BlockRegistry;
import ink.echol.outofplacecraft.items.ItemRegistry;
import ink.echol.outofplacecraft.config.ConfigHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.GeckoLib;


@Mod("outofplacecraft")
public class OutOfPlacecraftMod
{
    public static final String MODID = "outofplacecraft";

    private static final Logger LOGGER = LogManager.getLogger();

    public OutOfPlacecraftMod() {
        GeckoLib.initialize();

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigHandler.CLIENT_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHandler.COMMON_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ConfigHandler.SERVER_SPEC);

        // Register blocks and items. (MUST BE IN THAT ORDER, for BlockItems)
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BlockRegistry.BLOCKS.register(bus);
        ItemRegistry.ITEMS.register(bus);
        //FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        //MinecraftForge.EVENT_BUS.register(this);
    }
}
