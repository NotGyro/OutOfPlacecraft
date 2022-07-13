package ink.echol.outofplacecraft.items;

import ink.echol.outofplacecraft.OutOfPlacecraftMod;
import ink.echol.outofplacecraft.blocks.BlockRegistry;
import ink.echol.outofplacecraft.potion.PotionRegistry;
import net.minecraft.item.*;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, OutOfPlacecraftMod.MODID);
    // BLOCKITEMS
    public static final RegistryObject<BlockItem> CLAMSAND_BLOCK_ITEM = ITEMS.register("clam_sand",
            () -> new BlockItem(BlockRegistry.CLAM_SAND_BLOCK.get(),
                    new Item.Properties().tab(ItemGroup.TAB_BUILDING_BLOCKS))
    );
    public static final RegistryObject<BlockItem> ARTIFACT_SAND_ITEM = ITEMS.register("artifact_sand",
            () -> new BlockItem(BlockRegistry.ARTIFACT_SAND_BLOCK.get(),
                    new Item.Properties().tab(ItemGroup.TAB_BUILDING_BLOCKS))
    );

    // ITEMS
    public static final RegistryObject<Item> CLAM = ITEMS.register("clam",
            () -> new Item(new Item.Properties().tab(ItemGroup.TAB_FOOD).food(new Food.Builder().meat().alwaysEat().nutrition(3).saturationMod(0.4f).build()).stacksTo(16))
    );
    public static final RegistryObject<Item> COOKED_CLAM = ITEMS.register("cooked_clam",
            () -> new Item(new Item.Properties().tab(ItemGroup.TAB_FOOD).food(new Food.Builder().nutrition(5).fast().saturationMod(0.7f).build()).stacksTo(16))
    );
    public static final RegistryObject<Item> ZAT_ZHING = ITEMS.register("zatzhing",
            () -> new ZatZhingItem(new Item.Properties().tab(ItemGroup.TAB_MISC).stacksTo(1))
    );
}