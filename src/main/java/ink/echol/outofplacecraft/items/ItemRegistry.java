package ink.echol.outofplacecraft.items;

import ink.echol.outofplacecraft.OutOfPlacecraftMod;
import ink.echol.outofplacecraft.blocks.BlockRegistry;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, OutOfPlacecraftMod.MODID);
    // BLOCKITEMS
    public static final RegistryObject<BlockItem> CLAMSAND_BLOCK_ITEM = ITEMS.register("clam_sand",
            () -> new BlockItem(BlockRegistry.CLAMSAND_BLOCK.get(),
            new Item.Properties().tab(ItemGroup.TAB_BUILDING_BLOCKS))
    );
    // ITEMS
    public static final RegistryObject<Item> CLAM = ITEMS.register("clam",
        () -> new Item(new Item.Properties().tab(ItemGroup.TAB_FOOD))
    );
}