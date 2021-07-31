package ink.echol.outofplacecraft.blocks;

import ink.echol.outofplacecraft.OutOfPlacecraftMod;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, OutOfPlacecraftMod.MODID);

    public static final RegistryObject<Block> CLAMSAND_BLOCK = BLOCKS.register("clam_sand", ()
            -> new FallingBlock(AbstractBlock.Properties.of(Material.SAND, MaterialColor.COLOR_LIGHT_GRAY)
            .harvestLevel(1)
            .harvestTool(ToolType.SHOVEL)
            .strength(1.0F, 1.0F)
            .sound(SoundType.SAND)));
}