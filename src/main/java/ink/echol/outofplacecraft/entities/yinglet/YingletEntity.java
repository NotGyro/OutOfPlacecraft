package ink.echol.outofplacecraft.entities.yinglet;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class YingletEntity extends LivingEntity implements IAnimatable {
	protected YingletEntity(final EntityType<? extends LivingEntity> p_i48577_1_, final World p_i48577_2_) {
		super(p_i48577_1_, p_i48577_2_);
	}

	@Override
	public Iterable<ItemStack> getArmorSlots() {
		return null;
	}

	@Override
	public ItemStack getItemBySlot(final EquipmentSlotType p_184582_1_) {
		return null;
	}

	@Override
	public void setItemSlot(final EquipmentSlotType p_184201_1_, final ItemStack p_184201_2_) {

	}

	@Override
	public HandSide getMainArm() {
		return null;
	}

	@Override
	public void registerControllers(final AnimationData data) {

	}

	@Override
	public AnimationFactory getFactory() {
		return null;
	}
}
