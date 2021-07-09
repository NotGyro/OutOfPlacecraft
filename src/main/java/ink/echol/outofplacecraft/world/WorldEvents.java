package ink.echol.outofplacecraft.world;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import ink.echol.outofplacecraft.OutOfPlacecraftMod;
import ink.echol.outofplacecraft.blocks.BlockRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.*;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = OutOfPlacecraftMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WorldEvents {

    public static ConfiguredFeature<?,?> CONFIGURED_CLAM_SAND;

    public static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            CONFIGURED_CLAM_SAND = Feature.SIMPLE_BLOCK.configured(new BlockWithContextConfig(BlockRegistry.CLAMSAND_BLOCK.get().defaultBlockState(),
                    ImmutableList.of(Blocks.SAND.defaultBlockState()), //On
                    ImmutableList.of(Blocks.SAND.defaultBlockState()), //In
                    ImmutableList.of(Blocks.AIR.defaultBlockState()) //Under
                    )

            ).range(64).squared().count(14);

            Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(OutOfPlacecraftMod.MODID, "clam_sand"), CONFIGURED_CLAM_SAND);
        });
    }

    /*public static final commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            new ReplaceBlockConfig(Blocks.SAND.defaultBlockState(), BlockRegistry.CLAMSAND_BLOCK.get().defaultBlockState())
            Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(StructureTutorialMain.MODID, "coal_ore"), CONFIGURED_COAL_ORE);
        }
    }*/

    @SubscribeEvent
    static void onBiomeLoad(BiomeLoadingEvent event) {
        event.getGeneration().getFeatures(GenerationStage.Decoration.TOP_LAYER_MODIFICATION).add(() -> CONFIGURED_CLAM_SAND);
    }
}
