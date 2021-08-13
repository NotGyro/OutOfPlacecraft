package ink.echol.outofplacecraft.world;

import com.google.common.collect.ImmutableList;
import ink.echol.outofplacecraft.OutOfPlacecraftMod;
import ink.echol.outofplacecraft.blocks.BlockRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

//@Mod.EventBusSubscriber(modid = OutOfPlacecraftMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WorldEvents {

    public static ConfiguredFeature<?,?> CONFIGURED_CLAM_SAND;
    public static ConfiguredFeature<?,?> CONFIGURED_UNDERWATER_CLAM_SAND;

    public static ConfiguredFeature<?,?> CONFIGURED_ARTIFACT_SAND;

    public static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            //------ CLAM SAND ------

            //"Squared" is, believe it or not... "each top-level configuredfeature in a biome runs exactly once per chunk and the 0,0,0 chunk origin is given to the configuredfeature as a position; most features use a square decorator and a height decorator to generate the position at a random xz and y position in the chunk instead" courtesy of Commoble on MMD.
            CONFIGURED_CLAM_SAND = Feature.SIMPLE_BLOCK.configured(new BlockWithContextConfig(BlockRegistry.CLAM_SAND_BLOCK.get().defaultBlockState(),
                    ImmutableList.of(Blocks.SAND.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), Blocks.STONE.defaultBlockState(), Blocks.CLAY.defaultBlockState(), Blocks.DIRT.defaultBlockState()), //On
                    ImmutableList.of(Blocks.SAND.defaultBlockState()), //In
                    ImmutableList.of(Blocks.AIR.defaultBlockState(), Blocks.WATER.defaultBlockState(), Blocks.SUGAR_CANE.defaultBlockState()) //Under
                )
            ).decorated(Placement.RANGE.configured(new TopSolidRangeConfig(61, 0, 3))).squared().count(5);
            // The way range placements work is, Y-level of first-argument + rand (between 0 and (third argument - second argument))
            // Range_biased is slightly different: it's Y-level of first + rand (between 0 and rand (between 0 and (third - second))), so rand gets called twice.

            CONFIGURED_UNDERWATER_CLAM_SAND = Feature.SIMPLE_BLOCK.configured(new BlockWithContextConfig(BlockRegistry.CLAM_SAND_BLOCK.get().defaultBlockState(),
                            ImmutableList.of(Blocks.SAND.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), Blocks.STONE.defaultBlockState(), Blocks.CLAY.defaultBlockState(), Blocks.DIRT.defaultBlockState()), //On
                            ImmutableList.of(Blocks.SAND.defaultBlockState()), //In
                            ImmutableList.of(Blocks.AIR.defaultBlockState(), Blocks.WATER.defaultBlockState(), Blocks.SUGAR_CANE.defaultBlockState()) //Under
                    )
            ).decorated(Placement.RANGE.configured(new TopSolidRangeConfig(31, 0, 30))).squared().count(8);

            //------ ARTIFACT SAND ------
            CONFIGURED_ARTIFACT_SAND = Feature.SIMPLE_BLOCK.configured(new BlockWithContextConfig(BlockRegistry.ARTIFACT_SAND_BLOCK.get().defaultBlockState(),
                            ImmutableList.of(Blocks.SAND.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), Blocks.STONE.defaultBlockState(), Blocks.CLAY.defaultBlockState(), Blocks.DIRT.defaultBlockState()), //On
                            ImmutableList.of(Blocks.SAND.defaultBlockState()), //In
                            ImmutableList.of(Blocks.AIR.defaultBlockState(), Blocks.WATER.defaultBlockState(), Blocks.SUGAR_CANE.defaultBlockState(), Blocks.SAND.defaultBlockState()) //Under
                    )
            ).decorated(Placement.RANGE.configured(new TopSolidRangeConfig(20, 0, 44))).squared().count(3);


            Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(OutOfPlacecraftMod.MODID, "clam_sand"), CONFIGURED_CLAM_SAND);
            Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(OutOfPlacecraftMod.MODID, "underwater_clam_sand"), CONFIGURED_UNDERWATER_CLAM_SAND);

            Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(OutOfPlacecraftMod.MODID, "artifact_sand"), CONFIGURED_ARTIFACT_SAND);
        });
    }
    //@SubscribeEvent
    public static void onBiomeLoad(BiomeLoadingEvent event) {
        if( ((event.getCategory() == Biome.Category.BEACH) || (event.getCategory() == Biome.Category.RIVER))
                || ((event.getCategory() == Biome.Category.SWAMP) || (event.getCategory() == Biome.Category.OCEAN)) ) {
            event.getGeneration().getFeatures(GenerationStage.Decoration.TOP_LAYER_MODIFICATION).add(() -> CONFIGURED_CLAM_SAND);
            event.getGeneration().getFeatures(GenerationStage.Decoration.TOP_LAYER_MODIFICATION).add(() -> CONFIGURED_UNDERWATER_CLAM_SAND);

            event.getGeneration().getFeatures(GenerationStage.Decoration.TOP_LAYER_MODIFICATION).add(() -> CONFIGURED_ARTIFACT_SAND);
        }
    }
}
