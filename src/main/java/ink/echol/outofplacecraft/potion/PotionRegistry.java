package ink.echol.outofplacecraft.potion;

import ink.echol.outofplacecraft.OutOfPlacecraftMod;
import net.minecraft.block.Block;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraftforge.common.extensions.IForgeEffect;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class PotionRegistry {
    public static final DeferredRegister<Effect> EFFECTS = DeferredRegister.create(ForgeRegistries.POTIONS, OutOfPlacecraftMod.MODID);

    public static final RegistryObject<Effect> CLOMMED = EFFECTS.register("clommed",
            () -> (Effect) new ClommedEffect(EffectType.BENEFICIAL, 13458603));
}
