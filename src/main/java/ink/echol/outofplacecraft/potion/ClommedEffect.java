package ink.echol.outofplacecraft.potion;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeEffect;

import java.util.ArrayList;
import java.util.List;

public class ClommedEffect extends Effect implements IForgeEffect {

    public ClommedEffect(EffectType p_i50391_1_, int p_i50391_2_) {
        super(p_i50391_1_, p_i50391_2_);
    }
    @Override
    public boolean shouldRender(EffectInstance effect) { return false; }
    @Override
    public boolean shouldRenderInvText(EffectInstance effect) { return false; }
    @Override
    public boolean shouldRenderHUD(EffectInstance effect) { return false; }

    @Override
    public void applyEffectTick(LivingEntity p_76394_1_, int p_76394_2_) {
        super.applyEffectTick(p_76394_1_, p_76394_2_);
    }
    @Override
    public boolean isBeneficial() {
        return true;
    }
}
