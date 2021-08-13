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
package ink.echol.outofplacecraft.entities.yinglet;

import ink.echol.outofplacecraft.OutOfPlacecraftMod;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.Collections;

@Mod.EventBusSubscriber(modid = OutOfPlacecraftMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Yinglet extends CreatureEntity implements IAnimatable {

	public Yinglet(final EntityType<? extends CreatureEntity> type, final World world_in) {
		super(type, world_in);
		this.noCulling = false;
	}

	public static final EntityType<Yinglet> ENTITY_TYPE = EntityType.Builder
			.of(Yinglet::new, EntityClassification.CREATURE)
			.sized(1, 1)
			.build(Yinglet.class.getSimpleName().toLowerCase());

	@Override
	public EntityType<?> getType() {
		return ENTITY_TYPE;
	}

	@SubscribeEvent
	public static void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
		Yinglet.ENTITY_TYPE.setRegistryName(new ResourceLocation(OutOfPlacecraftMod.MODID, Yinglet.class.getSimpleName().toLowerCase()));
		event.getRegistry().register(Yinglet.ENTITY_TYPE);
	}

	@SubscribeEvent
	public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
		event.put(
				ENTITY_TYPE,
				MobEntity.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0D).build()
		);
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void registerRenderers(final FMLClientSetupEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(
				ENTITY_TYPE,
				YingletRenderer::new
		);
	}

	public static final Item SPAWN_EGG = new SpawnEggItem(
			ENTITY_TYPE,
			0x000000, // eggPrimary ???
			0xFFFFFF, // eggSecondary ???
			(new Item.Properties()).tab(ItemGroup.TAB_MISC)
	);

	@SubscribeEvent
	public static void registerSpawnEggs(RegistryEvent.Register<Item> event) {
		SPAWN_EGG.setRegistryName(new ResourceLocation(OutOfPlacecraftMod.MODID, "yinglet_spawn_egg"));
		event.getRegistry().register(SPAWN_EGG);
	}


	private AnimationFactory animationFactory = new AnimationFactory(this);

	@Override
	public Iterable<ItemStack> getArmorSlots() {
		return Collections.emptyList();
	}

	@Override
	public ItemStack getItemBySlot(final EquipmentSlotType slot) {
		return ItemStack.EMPTY;
	}

	@Override
	public void setItemSlot(final EquipmentSlotType slot, final ItemStack stack) {

	}

	@Override
	public HandSide getMainArm() {
		return HandSide.RIGHT;
	}

	private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
		event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.yinglet.WHY", true));
		return PlayState.CONTINUE;
	}

	@Override
	public void registerControllers(final AnimationData data) {
		data.addAnimationController(new AnimationController(this, "controller", 0, this::predicate));
	}

	@Override
	public AnimationFactory getFactory() {
		return this.animationFactory;
	}

	@Override
	public boolean shouldRender(double p_145770_1_, double p_145770_3_, double p_145770_5_) {
		return true; //TODO
	}
}